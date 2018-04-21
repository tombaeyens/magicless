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

package be.tombaeyens.configuration;

import be.tombaeyens.configuration.advanced.ValueType;
import be.tombaeyens.util.Exceptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static be.tombaeyens.util.Exceptions.assertNotNull;
import static be.tombaeyens.util.Exceptions.assertNotNullParameter;

/**
 * Configuration configuration = new Options()
 *   .option(new Option()
 *     .names("port", "p")
 *     .description("The port on which the service is bound to")
 *     .password()
 *     .valueTypeString()
 *     .defaultValue("8080"))
 */
public class Option {

  /** The option names without the - (dash).
   * Multiple names can be given to distinct between short and long names like eg "port" & "p" */
  protected List<String> names;
  protected String description;
  protected boolean password = false;
  /** The type of values this option can take.
   * A non null valueType means that this option expects one or more values.
   * A null valueType means no values are allowed. */
  protected ValueType valueType = null; // null means no values are allowed
  /** Indicates if a value for this option is required */
  protected boolean valueRequired = false;
  /** Indicates if multiple values for this option can be specified */
  protected boolean multipleValuesAllowed = false;
  protected Object defaultValue;

  /** Asserts if this option configuration is valid and throws a RuntimeException if this option is not valid.
   * This method is automatically called when adding an Option to Options with {@link Options#option(Option)} */
  protected void assertValid() {
    Exceptions.assertNotEmptyCollection(names, "names");
    for (int i=0; i<names.size(); i++) {
      assertNotNull(names.get(i), "Name "+i+" is null.  Specify a non-null");
    }
    if (valueRequired && valueType==null) {
      throw new RuntimeException("A value is required and no value type is configured.  Use one of the .valueXxx() methods.");
    }
    if (defaultValue!=null && valueType==null) {
      throw new RuntimeException("A default value is specified and no value type is configured.  Use one of the .valueXxx() methods or remove the .defaultValue(...)");
    }
    if (valueRequired && defaultValue!=null) {
      throw new RuntimeException("Value is required and defaultValue is specified.  Remove either the .required() or the .defaultValue(...)");
    }
  }

  /** Comma separated list of all the option names prefixed with the - (dash).
   * Used by {@link Options#getDocumentation()} and error messages to
   * get all the names of an option as a comma separated list
   * (without the brackets produced by List.toString) */
  protected String getNamesText() {
    return names.stream()
      .map(name->"-"+name)
      .collect(Collectors.joining(","));
  }

  /** The option names without the - (dash).
   * Multiple names can be given to distinct between short and long names like eg "port" & "p" */
  protected List<String> getNames() {
    return names;
  }

  /** Adds the name */
  public Option name(String name) {
    assertNotNullParameter(name, "name");
    if (names==null) {
      names = new ArrayList<>();
    }
    names.add(name);
    return this;
  }

  /** Adds all the given names */
  public Option names(String... names) {
    assertNotNullParameter(names, "names");
    this.names = Arrays.asList(names);
    return this;
  }

  protected String getDescription() {
    return description;
  }

  public Option description(String description) {
    this.description = description;
    return this;
  }

  protected boolean isValueRequired() {
    return valueRequired;
  }

  public Option valueRequired() {
    this.valueRequired = true;
    return this;
  }

  protected boolean isPassword() {
    return password;
  }

  protected ValueType getValueType() {
    return valueType;
  }

  public Option valueType(ValueType valueType) {
    this.valueType = valueType;
    return this;
  }

  public Option valueMultiType(ValueType valueType) {
    valueType(valueType);
    multipleValuesAllowed = true;
    return this;
  }

  public Option valueString() {
    valueType(ValueType.STRING);
    return this;
  }

  public Option valueMultiString() {
    valueMultiType(ValueType.STRING);
    return this;
  }

  public Option valuePassword() {
    valueString();
    this.password = true;
    return this;
  }

  public Option valueMultiPassword() {
    valueMultiString();
    this.password = true;
    return this;
  }

  public Option valueInteger() {
    valueType(ValueType.INTEGER);
    return this;
  }

  public Option valueMultiInteger() {
    valueMultiType(ValueType.INTEGER);
    return this;
  }

  public Option valueInteger(Integer minValue, Integer maxValue) {
    valueType(new ValueType.IntegerType(minValue, maxValue));
    return this;
  }

  public Option valueMultiInteger(Integer minValue, Integer maxValue) {
    valueMultiType(new ValueType.IntegerType(minValue, maxValue));
    return this;
  }

  public Option valueEnum(String... possibleValues) {
    valueType(new ValueType.EnumType(possibleValues));
    return this;
  }

  public Option valueMultiEnum(String... possibleValues) {
    valueMultiType(new ValueType.EnumType(possibleValues));
    return this;
  }

  protected Object getDefaultValue() {
    return defaultValue;
  }

  public Option defaultValue(Object defaultValue) {
    this.defaultValue = defaultValue;
    return this;
  }

  protected boolean hasValue() {
    return valueType!=null;
  }

  protected boolean isMultipleValuesAllowed() {
    return multipleValuesAllowed;
  }

  public String getDocumentation() {
    if (valueType!=null) {
      return (description!=null ? description : "") +
             (!valueRequired && multipleValuesAllowed ? " Zero or more "+valueType.getTypeText()+" values can be specified." : "") +
             (!valueRequired && !multipleValuesAllowed ? " A "+valueType.getTypeText()+" value is optional." : "") +
             (valueRequired && multipleValuesAllowed ? " One or more "+valueType.getTypeText()+" values can be specified.": "") +
             (valueRequired && !multipleValuesAllowed ? " Exactly one "+valueType.getTypeText()+" value must be specified." : "") +
             (defaultValue!=null ? " Default value is "+defaultValue.toString() : "");
    } else {
      return description!=null ? description+" Option flag without values." : "Option flag without values.";
    }
  }
}
