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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class ValueOptionalTest {


  @Test
  public void testValueOptionalAndProvided() {
    Configuration configuration = new Options()
      .option(new Option()
        .names("server", "s")
        .valueString()) // single, optional string value
      .parse("-s", "localhost") // the -s option value is localhost
      .assertNoErrors();

    assertThat(configuration.has("s"), is(true));
    assertThat(configuration.has("server"), is(true));

    assertThat(configuration.get("s"), is("localhost"));
    assertThat(configuration.get("server"), is("localhost"));
  }

  @Test
  public void testValueOptionalAndNoValueProvided() {
    Configuration configuration = new Options()
      .option(new Option()
        .names("server", "s")
        .valueString()) // single, optional string value
      .parse("-s") // the -s option is specified, but no value is provided
      .assertNoErrors();

    assertThat(configuration.has("s"), is(true));
    assertThat(configuration.has("server"), is(true));

    assertThat(configuration.get("s"), nullValue());
    assertThat(configuration.get("server"), nullValue());
  }

  @Test
  public void testValueOptionalAndNotProvided() {
    Configuration configuration = new Options()
      .option(new Option()
        .names("server", "s")
        .valueString()) // single, optional string value
      .parse() // the option -s is not provided
      .assertNoErrors();

    assertThat(configuration.has("s"), is(false));
    assertThat(configuration.has("server"), is(false));

    assertThat(configuration.get("s"), nullValue());
    assertThat(configuration.get("server"), nullValue());
  }


}
