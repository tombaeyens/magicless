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
import be.tombaeyens.configuration.Option;
import be.tombaeyens.configuration.Options;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ValueRequiredTest {


  @Test
  public void testValueRequiredAndProvided() {
    Configuration configuration = new Options()
      .option(new Option()
        .names("server", "s")
        .valueString() // single string value
        .valueRequired())   // is required
      .parse("-s", "localhost") // the -s option value is localhost
      .assertNoErrors();

    assertThat(configuration.has("s"), is(true));
    assertThat(configuration.has("server"), is(true));

    assertThat(configuration.get("s"), is("localhost"));
    assertThat(configuration.get("server"), is("localhost"));
  }

  @Test
  public void testValueMultiRequiredAndProvided() {
    Configuration configuration = new Options()
      .option(new Option()
        .names("server", "s")
        .valueMultiString() // single string value
        .valueRequired())   // is required
      .parse("-s", "localhost") // the -s option value is localhost
      .assertNoErrors();

    assertThat(configuration.has("s"), is(true));
    assertThat(configuration.has("server"), is(true));

    assertThat(configuration.get("s"), is("localhost"));
    assertThat(configuration.get("server"), is("localhost"));
  }

  @Test
  public void testValueRequiredAndNotProvidedError() {
    Options options = new Options()
      .option(new Option()
        .names("server", "s")
        .valueString() // single string value
        .valueRequired());

    assertThat(Assertions.assertOneElement(options.parse().getErrors()).get(0),
      containsString("Property -server,-s is required"));
  }

  @Test
  public void testValueMultiRequiredAndNotProvidedError() {
    Options options = new Options()
      .option(new Option()
        .names("server", "s")
        .valueMultiString() // single string value
        .valueRequired());

    assertThat(Assertions.assertOneElement(options.parse().getErrors()).get(0),
      containsString("Property -server,-s is required"));
  }
}
