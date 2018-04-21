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

import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ValuelessOptionsTest {


  @Test
  public void testValuelessNormalUsage() {
    Configuration configuration = new Options()
      .option(new Option()
        .names("verbose", "v")) // no valueType(...) is specified
      .option(new Option()
        .names("debug", "d"))   // no valueType(...) is specified
      .parse("-v") // only the -v option is specified
      .assertNoErrors();

    // Check that both the -v and the -verbose flags are present
    assertThat(configuration.has("verbose"), is(true));
    assertThat(configuration.has("v"),       is(true));

    // Check that both the -d and the -debug flags are not present
    assertThat(configuration.has("debug"),   is(false));
    assertThat(configuration.has("d"),       is(false));
  }

  @Test
  public void testValueNotAllowedError() {
    Options options = new Options()
      .option(new Option()
        .names("verbose", "v"));

    List<String> errors = options
      .parse("-v", "notAllowedValue")
      .getErrors();

    String error = errors.get(0);

    assertThat(error, containsString("Value not allowed for option -verbose,-v: notAllowedValue"));
    assertThat(errors.size(), is(1));
  }

}
