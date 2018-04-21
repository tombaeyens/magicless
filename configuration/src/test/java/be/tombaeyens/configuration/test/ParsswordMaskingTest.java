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
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class ParsswordMaskingTest {

  @Test
  public void testPaswordMasking() {
    Configuration configuration = null;
    try {
      configuration = new Options()
        .option(new Option()
          .names("pwd")
          .valuePassword())
        .option(new Option()
          .names("nr")
          .valueInteger())
        .parse("-pwd", "sensitive", "-nr", "x3")
        .assertNoErrors();
      fail("Expected exception");
    } catch (Exception e) {
      String errorMessage = e.getMessage();
      assertThat(errorMessage, not(containsString("sensitive")));
      assertThat(errorMessage, containsString("***pwd*hidden***"));
    }
  }
}
