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

package be.tombaeyens.configuration.test;

import be.tombaeyens.configuration.Configuration;
import be.tombaeyens.configuration.ConfigurationException;
import be.tombaeyens.configuration.Option;
import be.tombaeyens.configuration.Options;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class ExampleTest {

  @Test
  public void testReadmeExample() {
    String[] args = {"-port", "4567", "-env", "dev", "-disabledFeatures", "login", "preview"};

    try {
      Configuration configuration = new Options()
        .usage("java -jar myjar.jar <options>")
        .option(new Option()
          .names("verbose", "v")) // no valueType means it's a boolean flag option without value
        .option(new Option()
          .names("port", "p")
          .description("The port on which the service is bound.")
          .valueInteger(0, 65000)
          .defaultValue("8080")) // specifying a default value implies a value is optional
        .option(new Option()
          .name("env")
          .description("The environment.")
          .valueEnum("dev", "test", "prod")) // No default value means a value is required.
        .option(new Option()
          .name("disabledFeatures")
          .description("Disables all specified features.")
          .valueMultiEnum("search", "login", "preview")) // Allows multiple values
        .parse(args)
        .assertNoErrors(); // throws runtime exception with syntax description in case of problems

      boolean verbose = configuration.has("verbose");
      int port = configuration.get("port");
      String env = configuration.get("env");
      List<String> disabledFeatures = configuration.getList("disabledFeatures");

      // ...

      // Delete these lines in the docs
      assertThat(verbose, is(false));
      assertThat(port, is(4567));
      assertThat(env, is("dev"));
      assertThat(disabledFeatures, hasItems("login","preview"));

    } catch (ConfigurationException e) {
      // This will print the error(s) and the complete option syntax documentation
      System.out.println(e.getMessage());

      // Delete these lines in the docs
      throw e;
    }
  }


}
