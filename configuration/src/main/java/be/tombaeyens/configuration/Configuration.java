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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;

/**
 * Parses command line args into configuration properties and produces a syntax
 * description in case of configuration parsing exception.
 *
 * Usage:
 *
 * java -jar ... -port 9874 -verbose
 *
 * Configuration configuration = new ConfigurationSyntax()
 *   .propertyIntegerRequired("port")
 *   .propertyStringOptional("server", "localhost")
 *   .propertyBooleanOptional("verbose", false)
 *   .alias("v", "verbose")
 *   .parse(args); // throws runtime exception with syntax description in case of problems
 *
 * int port = configuration.getInt("port");  // -> 9874
 * String server = configuration.getString("server"); // -> localhost
 * boolean verbose = configuration.getOptional("verbose"); // -> true
 */
public class Configuration {

  protected Options options;
  /** Maps option names to the list of specified values */
  protected Map<String, List<Object>> optionValuesMap;
  protected List<String> errors = null; // null means no errors
  protected List<String> loggableArgs = new ArrayList<>();

  public Configuration(Options options) {
    this.options = options;
    this.optionValuesMap = new LinkedHashMap<>();
  }

  /** Retrieves the configured value for the option name.
   * If there are more values configured, this returns the first value.
   * @param name is the name without the - (dash) */
  public <T> T get(String name) {
    List<Object> values = optionValuesMap.get(name);
    return values!=null && !values.isEmpty() ? (T) values.get(0) : null;
  }

  public <T> List<T> getList(String name) {
    return (List<T>) optionValuesMap.get(name);
  }

  public <T> List<T> getArray(String name) {
    return (List<T>) optionValuesMap.get(name);
  }

  public boolean has(String name) {
    return optionValuesMap.containsKey(name);
  }

  protected void optionSpecified(Option option) {
    List<Object> optionValues = getOptionValues(option);
    if (optionValues==null) {
      optionValues = new ArrayList<>();
      for (String name: option.getNames()) {
        optionValuesMap.put(name, optionValues);
      }
    }
  }

  /** Adds a value for the option */
  protected List<Object> addValue(Option option, Object optionValue) {
    List<Object> optionValues = getOptionValues(option);
    optionValues.add(optionValue);
    return optionValues;
  }

  protected List<Object> getOptionValues(Option option) {
    String optionFirstName = option.getNames().get(0);
    return optionValuesMap.get(optionFirstName);
  }

  protected boolean hasValue(Option option) {
    return getOptionValues(option)!=null;
  }

  protected void addError(String errorMessage) {
    if (errors==null) {
      errors = new ArrayList<>();
    }
    errors.add(errorMessage);
  }

  /** @throws ConfigurationException (RuntimeException) if parsing errors occured */
  public Configuration assertNoErrors() {
    if (errors!=null) {
      String errormessage =
        "Configuration errors:\n" +
        errors.stream().collect(joining("\n")) + "\n\n" +
        "Configurations:\n" +
        getOptionValueTextsForLogging() + "\n\n" +
        "Configuration properties:\n" +
        options.getDocumentation();
      throw new ConfigurationException(errormessage);
    }
    return this;
  }

  public String getOptionValueTextsForLogging() {
    return loggableArgs.stream().collect(joining(" "));
  }

  /** collects the args for logging and exception message purposes, the caller masks option password values */
  protected void addLoggableArg(String loggableArg) {
    loggableArgs.add(loggableArg);
  }

  public List<String> getErrors() {
    return errors;
  }
}
