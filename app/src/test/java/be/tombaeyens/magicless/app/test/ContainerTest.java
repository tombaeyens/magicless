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
package be.tombaeyens.magicless.app.test;

import be.tombaeyens.magicless.app.container.Container;
import be.tombaeyens.magicless.app.container.Initializable;
import be.tombaeyens.magicless.app.container.Startable;
import be.tombaeyens.magicless.app.container.Stoppable;
import org.hamcrest.core.IsSame;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ContainerTest {

  public static class TestComponent implements Initializable, Startable, Stoppable {
    int initializedCount = 0;
    int startedCount = 0;
    int stoppedCount = 0;
    @Override
    public void initialize(Container application) {
      initializedCount++;
    }
    @Override
    public void start(Container application) {
      startedCount++;
    }
    @Override
    public void stop(Container application) {
      stoppedCount++;
    }
  }

  @Test
  public void testPlainObjectRetrievalByType() {
    final LinkedHashMap<Object, Object> component = new LinkedHashMap<>();
    Container container = new Container() {
      {
        add(component);
        initialize();
      }
    };
    assertThat(container.getOpt(LinkedHashMap.class), new IsSame<>(component));
    assertThat(container.getOpt(HashMap.class), new IsSame<>(component));
    assertThat(container.getOpt(Map.class), new IsSame<>(component));
    assertThat(container.getOpt(Cloneable.class), new IsSame<>(component));
  }

  @Test
  public void testPlainObjectRetrievalByName() {
    final Map<String,Object> linked = new LinkedHashMap<>();
    final Map<String,Object> tree = new TreeMap<>();
    Container container = new Container() {
      {
        add("linked", linked);
        add("tree", tree);
        initialize();
      }
    };
    assertThat(container.getOpt("linked"), new IsSame<>(linked));
    assertThat(container.getOpt("tree"), new IsSame<>(tree));
    assertThat(container.getAll(LinkedHashMap.class), is(new Object[]{linked}));
    assertThat(container.getAll(TreeMap.class), is(new Object[]{tree}));
    assertThat(container.getAll(Map.class), is(new Object[]{tree, linked}));
    assertThat(container.getAll(), is(new Object[]{tree, linked}));
  }

  @Test
  public void testContainerLifecycleCallbacks() {
    final TestComponent testComponent = new TestComponent();
      Container container = new Container() {
      {
        add(testComponent);
        assertThat(testComponent.initializedCount, is(0));
        assertThat(testComponent.startedCount,     is(0));
        assertThat(testComponent.stoppedCount,     is(0));
        initialize();
      }
    };

    assertThat(testComponent.initializedCount, is(1));
    assertThat(testComponent.startedCount,     is(0));
    assertThat(testComponent.stoppedCount,     is(0));

    container.start();

    assertThat(testComponent.initializedCount, is(1));
    assertThat(testComponent.startedCount,     is(1));
    assertThat(testComponent.stoppedCount,     is(0));

    container.stop();

    assertThat(testComponent.initializedCount, is(1));
    assertThat(testComponent.startedCount,     is(1));
    assertThat(testComponent.stoppedCount,     is(1));
  }

  @Test
  public void testUnavailableObject() {
    Container container = new Container(){
      {initialize();}
    };
    container.getOpt("cleanplanet");
    container.getOpt("cleanplanet");
  }
}
