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

import java.util.*;

import static java.util.stream.Collectors.joining;

/**
 * Configuration configuration = new Options()
 *   .usage("java -jar myjar.jar <options>")
 *   .option(new Option()
 *     .names("port", "p")
 *     .description("The port on which the service is bound to")
 *     .password()
 *     .valueTypeString()
 *     .defaultValue("8080"))
 *   .option(new Option()
 *     .names("port", "p")
 *     .description("The port on which the service is bound to")
 *     .password()
 *     .valueTypeString()
 *     .defaultValue("8080"))
 *   .parse(args)
 *   .assertNoErrors();
 */
public class Options {

  String usage = null;
  List<Option> options = new ArrayList<>();
  Map<String,Option> optionsByName = new LinkedHashMap<>();

  /** Part of the syntax description produced by {@link #getDocumentation()} and used in case of parsing errors */
  public String getUsage() {
    return usage;
  }

  /** Part of the syntax description produced by {@link #getDocumentation()} and used in case of parsing errors */
  public Options usage(String usage) {
    this.usage = usage;
    return this;
  }

  /** Adds an option */
  public Options option(Option option) {
    option.assertValid();
    options.add(option);
    option.getNames().forEach(name-> optionsByName.put(name, option));
    return this;
  }

  /** Parses the program arguments
   * @throws RuntimeException if the arguments don't match the options specified. The message of the exception
   * will include {@link #getDocumentation()}. */
  public Configuration parse(String... args) {

    Configuration configuration = new Configuration(this);

    if (args!=null) {
      // option will store the last specified option when looping over the args
      Option option = null;
      for (String arg: args) {
        boolean argIsPasswordValue = false;
        if (arg!=null) {
          // if an option was specified
          if (isOptionName(arg)) {
            String optionName = arg.substring(1);
            option = optionsByName.get(optionName);
            configuration.optionSpecified(option);
          } else { // the arg is a value
            if (option!=null) {
              argIsPasswordValue = option.isPassword();

              ValueType valueType = option.getValueType();
              if (valueType!=null) {
                try {
                  Object propertyValue = valueType.parseValue(arg);
                  List<Object> values = configuration.addValue(option, propertyValue);
                  if (!option.isMultipleValuesAllowed() && values.size()==2) {
                    configuration.addError("Multiple values specified for "+option.getNamesText());
                  }
                } catch (Exception e) {
                  configuration.addError("Invalid value "+getLoggableArg(arg, argIsPasswordValue)+" for "+option.getNamesText()+" with type "+valueType.getTypeText());
                }

              } else {
                configuration.addError("Value not allowed for option "+option.getNamesText()+": "+getLoggableArg(arg, argIsPasswordValue));
              }

            } else {
              configuration.addError(
                "Value "+getLoggableArg(arg, argIsPasswordValue)+
                " specified without specifying an option first. Put one of the following option names before the value: "+
                optionsByName.keySet().stream()
                  .map(name->"-"+name)
                  .collect(joining(", ")));
            }
          }
        }
        configuration.addLoggableArg(getLoggableArg(arg, argIsPasswordValue));
      }
    }

    // Check that required options have a value
    options.stream()
      .filter(Option::isValueRequired)
      .filter(option->!configuration.hasValue(option))
      .forEach(option->configuration.addError("Property " + option.getNamesText() + " is required"));

    return configuration;
  }

  private static String getLoggableArg(String arg, boolean argIsPasswordValue) {
    return !argIsPasswordValue ? arg : "***pwd*hidden***";
  }

  private boolean isOptionName(String arg) {
    return arg!=null
           && arg.length()>1
           && arg.startsWith("-")
           && optionsByName.containsKey(arg.substring(1));
  }

  protected String getDocumentation() {
    int maxLength = optionsByName.values().stream()
      .map(option -> option.getNamesText().length())
      .max(Comparator.comparing(Integer::valueOf))
      .get();
    String rightAlign = "%-"+(maxLength+2)+"s";
    return (usage!=null ? "Usage: "+usage+"\n" : "")+
           "Configuration options:\n" +
           optionsByName.values().stream()
             .distinct()
             .map(option -> String.format(rightAlign, option.getNamesText()) + " " + option.getDocumentation())
             .collect(joining("\n"));
  }

  protected Map<String, Option> getOptionsByName() {
    return optionsByName;
  }
}
