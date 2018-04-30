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
package be.tombaeyens.magicless;

import be.tombaeyens.magicless.app.util.Configuration;
import be.tombaeyens.magicless.example.ExampleApplication;
import be.tombaeyens.magicless.httpserver.HttpServer;
import be.tombaeyens.magicless.httptest.HttpTest;

public class ExampleTest extends HttpTest {

  static {
    System.setErr(System.out);
  }

  protected static ExampleApplication exampleApplication = null;

  private static class TestConfiguration extends Configuration {
    public TestConfiguration() {
      put("environment", "test");
    }
  }

  @Override
  public HttpServer initialize() {
    exampleApplication = new ExampleApplication(new TestConfiguration()
      .put("http.server.port", "8080")
      .put("http.server.name", "Example test server"));

    exampleApplication.start(); // also starts the HttpServer
    return exampleApplication.get(HttpServer.class);
  }
}
