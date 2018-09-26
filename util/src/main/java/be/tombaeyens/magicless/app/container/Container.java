/*
 * Copyright (c) 2018 Tom Baeyens
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.tombaeyens.magicless.app.container;

import be.tombaeyens.magicless.app.util.Exceptions;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

import static be.tombaeyens.magicless.app.util.Exceptions.assertNotNullParameter;
import static be.tombaeyens.magicless.app.util.Exceptions.assertTrue;

/**
 * Container for making application context components configurable and
 * accessible.
 *
 * Lifecycle: (instantiate)--> creating --.initialize()--> initialized
 * --.start()--> started --.stop()--> stopped
 *
 * The container can only once be started and stopped.
 *
 * Usage: Make your application context inherit from Container. Eg MyAppContext
 * extends Container Then in the constructor, use the add and addFactory methods
 * to add components and factories to the container and at the end of the
 * constructor, call initialize();
 */
@SuppressWarnings("unchecked")
public class Container {

  public static enum State {
    CREATING, INITIALIZED, STARTED, STOPPED
  }

  /**
   * list of components by name. multiple names might point to the same component.
   */
  Map<String, Object> componentsByName = new LinkedHashMap<>();
  /**
   * list of unique components, so you can get a list of components in the order
   * as they are added.
   */
  List<Object> components = new ArrayList<>();
  /** factories are used to create components when they are retrieved */
  Map<String, Factory> factoriesByName = new HashMap<>();
  State state = State.CREATING;

  public void add(Object component) {
    addByClass(component.getClass(), component);
  }

  private void addByClass(Class<?> clazz, Object component) {
    add(clazz.getName(), component);
    for (Class<?> interfaze : clazz.getInterfaces()) {
      addByClass(interfaze, component);
    }
    Class<?> superclass = clazz.getSuperclass();
    if (superclass != Object.class && superclass != null) {
      addByClass(superclass, component);
    }
  }

  public void add(String name, Object component) {
    assertNotNullParameter(name, "name");
    assertNotNullParameter(component, "component");
    componentsByName.put(name, component);
    if (!containsSame(components, component)) {
      components.add(component);
      if (state != State.CREATING) {
        initialize(component);
      }
      if (state == State.STARTED) {
        start(component);
      }
    }
  }

  /**
   * true if the component is already in the list of objects using == (not
   * .equals()).
   */
  private boolean containsSame(List<Object> objects, Object component) {
    for (Object existing : objects) {
      if (existing == component) {
        return true;
      }
    }
    return false;
  }

  /**
   * add a factory to create an object of a given clazz when it is retrieved from
   * the container
   */
  public void addFactory(Class<?> clazz, Factory factory) {
    addFactory(clazz.getName(), factory);
    for (Class<?> interfaze : clazz.getInterfaces()) {
      addFactory(interfaze, factory);
    }
    Class<?> superclass = clazz.getSuperclass();
    if (superclass != Object.class && superclass != null) {
      addFactory(superclass, factory);
    }
  }

  public void addFactory(String name, Factory factory) {
    assertNotNullParameter(name, "name");
    assertNotNullParameter(factory, "factory");
    factoriesByName.put(name, factory);
  }

  public <T> T getOpt(Class<T> clazz) {
    if (clazz == null) {
      return null;
    }
    return (T) getOpt(clazz.getName());
  }

  public <T> T getOpt(String name) {
    if (name == null) {
      return null;
    }
    T component = (T) componentsByName.get(name);
    if (component != null) {
      return component;
    }
    Factory factory = factoriesByName.get(name);
    if (factory != null) {
      component = (T) factory.create(this);
      add(name, component);
      initialize(component);
      if (state == State.STARTED) {
        start(component);
      }

      return component;
    }
    return null;
  }

  public <T> T get(String name) {
    T component = getOpt(name);
    if (component == null) {
      throw new RuntimeException("Component " + name + " not found");
    }
    return component;
  }

  public <T> T get(Class<T> clazz) {
    T component = getOpt(clazz);
    if (component == null) {
      throw new RuntimeException("Component " + clazz.getName() + " not found");
    }
    return component;
  }

  public <T> T[] getAll(Class<T> classFilter) {
    List<T> filteredComponents = new ArrayList<>();
    for (Object object : components) {
      if (classFilter == null || classFilter.isAssignableFrom(object.getClass())) {
        filteredComponents.add((T) object);
      }
    }
    Class<?> arrayType = classFilter != null ? classFilter : Object.class;
    T[] array = (T[]) Array.newInstance(arrayType, filteredComponents.size());
    return (T[]) filteredComponents.toArray(array);
  }

  public Object[] getAll() {
    return getAll(null);
  }

  protected void initialize() {
    assertTrue(state == State.CREATING, "To initialize, the container should be in state CREATING, but was " + state);
    for (Object component : components) {
      initialize(component);
    }
    state = State.INITIALIZED;
  }

  private void initialize(Object component) {
    if (component instanceof Initializable) {
      @SuppressWarnings("rawtypes")
      Initializable initializable = (Initializable) component;
      initializable.initialize(this);
    }
    inject(component, component.getClass());
  }

  private void inject(Object component, Class<?> clazz) {
    for (Field field : clazz.getDeclaredFields()) {
      Object dependency = null;
      Inject inject = field.getAnnotation(Inject.class);
      if (inject != null) {
        String dependencyName = inject.value();
        boolean required = inject.required();
        if (!"".equals(dependencyName)) {
          dependency = required ? get(dependencyName) : getOpt(dependencyName);
        } else {
          dependency = required ? get(field.getType()) : getOpt(field.getType());
        }
        try {
          if (!field.isAccessible()) {
            field.setAccessible(true);
          }
          field.set(component, dependency);
        } catch (IllegalAccessException e) {
          throw Exceptions.exceptionWithCause("inject field " + field, e);
        }
      }
    }
    Class<?> superclass = clazz.getSuperclass();
    if (superclass != Object.class) {
      inject(component, superclass);
    }
  }

  public void start() {
    assertTrue(state == State.INITIALIZED,
        "To initialize, the container should be in state INITIALIZED, but was " + state);
    for (Startable startable : getAll(Startable.class)) {
      startable.start(this);
    }
    state = State.STARTED;
  }

  private void start(Object component) {
    if (component instanceof Startable) {
      ((Startable) component).start(this);
    }
  }

  public void stop() {
    for (Stoppable stoppable : getAll(Stoppable.class)) {
      stoppable.stop(this);
    }
    state = State.STOPPED;
  }
}
