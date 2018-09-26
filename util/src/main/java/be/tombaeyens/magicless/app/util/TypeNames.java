package be.tombaeyens.magicless.app.util;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class TypeNames {

  protected Map<String,Type> typesByName = new HashMap<>();
  protected Map<Type,String> namesByType = new HashMap<>();

  public TypeNames typeName(Type type, String typeName) {
    typesByName.put(typeName, type);
    namesByType.put(type, typeName);
    return this;
  }

  public Map<String, Type> getTypesByName() {
    return typesByName;
  }

  public Map<Type, String> getNamesByType() {
    return namesByType;
  }

  public Type getType(String typeName) {
    return typesByName.get(typeName);
  }

  public String getName(Type type) {
    return namesByType.get(type);
  }
}
