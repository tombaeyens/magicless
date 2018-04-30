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
package be.tombaeyens.magicless.app.util;

import java.util.Properties;

public class Configuration {

  private Properties properties = new Properties();

  /** Loads configuration properties from resource file.
   * Overwrites existing configuration properties. */
  public void loadConfigurationFromResource(String resource) {
    Io.loadPropertiesFromResource(properties, resource);
  }

  /** Loads the System.getProperties().
   * Overwrites existing configuration properties */
  public void loadConfigurationFromSystemProperties() {
    properties.putAll(System.getProperties());
  }

  public String getStringOpt(String name) {
    return getString(name, false);
  }

  public String getString(String name) {
    return getString(name, true);
  }

  private String getString(String name, boolean required) {
    String value = properties.getProperty(name);
    if (required && (value==null || "".equals(value))) {
      throw new RuntimeException("Configuration property "+name+" is required");
    }
    return value;
  }

  public Integer getInteger(String name) {
    return getInteger(name, true);
  }

  public Integer getIntegerOpt(String name) {
    return getInteger(name, false);
  }

  private Integer getInteger(String name, boolean required) {
    String textValue = getString(name, required);
    if (textValue==null || "".equals(textValue)) {
      return null;
    }
    try {
      return Integer.parseInt(textValue);
    } catch (NumberFormatException e) {
      throw new RuntimeException("Invalid number value (integer) for configuration "+name+": "+textValue);
    }
  }

  public Configuration put(String name, String value) {
    properties.put(name, value);
    return this;
  }
}
