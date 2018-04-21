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

import static be.tombaeyens.util.Collections.arrayList;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class ValueMultiTest {

  @Test
  public void testValueMultiAndZeroProvided() {
    Configuration configuration = new Options()
      .option(new Option()
        .names("server", "s")
        .valueMultiString())
      .parse("-s")
      .assertNoErrors();

    assertThat(configuration.has("s"), is(true));
    assertThat(configuration.has("server"), is(true));

    assertThat(configuration.get("s"), nullValue());
    assertThat(configuration.get("server"), nullValue());

    assertThat(configuration.getList("server").size(), is(0));
  }

  @Test
  public void testValueOptionalAndOneProvided() {
    Configuration configuration = new Options()
      .option(new Option()
        .names("server", "s")
        .valueMultiString())
      .parse("-s", "localhost")
      .assertNoErrors();

    assertThat(configuration.has("s"), is(true));
    assertThat(configuration.has("server"), is(true));

    assertThat(configuration.get("s"), is("localhost"));
    assertThat(configuration.get("server"), is("localhost"));

    assertThat(configuration.getList("server"), is(arrayList("localhost")));
  }

  @Test
  public void testValueOptionalAndTwoProvided() {
    Configuration configuration = new Options()
      .option(new Option()
        .names("server", "s")
        .valueMultiString())
      .parse("-s", "localhost", "127.0.0.1")
      .assertNoErrors();

    assertThat(configuration.has("s"), is(true));
    assertThat(configuration.has("server"), is(true));

    assertThat(configuration.get("s"), is("localhost"));
    assertThat(configuration.get("server"), is("localhost"));

    assertThat(configuration.getList("server"), is(arrayList("localhost", "127.0.0.1")));
  }

  @Test
  public void testValueMultipleOptionOccurrencesAreCombinedInSingleValueList() {
    Configuration configuration = new Options()
      .option(new Option()
        .names("server", "s")
        .valueMultiString())
      .option(new Option()
        .names("v"))
      .parse("-s", "localhost", "127.0.0.1", "-v", "-server", "google.com")
      .assertNoErrors();

    assertThat(configuration.getList("server"),
      is(arrayList("localhost", "127.0.0.1", "google.com")));
  }


  @Test
  public void testValueMultipleValuesWhenOnblyOneIsAllowed() {
    String error = new Options()
      .option(new Option()
        .names("server", "s")
        .valueString()) // single, optional string value
      .parse("-s", "localhost", "127.0.0.1", "-server", "google.com")
      .getErrors()
      .get(0);

    assertThat(error, containsString("Multiple values specified for -server,-s"));
  }
}
