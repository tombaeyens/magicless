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
package be.tombaeyens.magicless.httptest;

import be.tombaeyens.magicless.httpclient.ClientRequest;
import be.tombaeyens.magicless.httpclient.ClientResponse;
import be.tombaeyens.magicless.httpclient.HttpClient;
import be.tombaeyens.magicless.httpserver.HttpServer;
import be.tombaeyens.magicless.routerservlet.RouterServlet;
import org.junit.Before;

/** Base test class for testing a HTTP server.
 *
 * There are convenience methods to build and execute HTTP requests:
 * {@link #newGet(String)}, {@link #newPost(String)}, {@link #newPut(String)},
 * {@link #newDelete(String)}, etc.
 * HTTP requests are executed synchronous, which is convenient for testing
 * and debugging purposes.
 *
 * The cool feature is that server side exceptions are captured and added
 * as the cause to the client side exceptions when asserting the http
 * response status with {@link ClientResponse#assertStatusOk()} etc.
 *
 * Usage see example/src/test/java/be/tombaeyens/magicless/ExampleTest.java */
public abstract class HttpTest {

  protected static HttpClient httpClient = null;
  protected static Throwable latestServerException = null;

  @Before
  public void setUp() {
    if (httpClient==null) {
      HttpServer httpServer = initialize();

      httpServer
        .getServlet(RouterServlet.class)
        .setExceptionListener(new ServerExceptionListener());

      int serverPort = httpServer.getPort();
      httpClient = new TestHttpClient();
      httpClient.setBaseUrl("http://localhost:" + serverPort);
    }

    // This ensures that no exceptions will pass through from the previous
    // test to this test because latestServerException is static
    latestServerException = null;
  }

  /** Called once before the first test is executed and can be used
   * to initialize static fields.  Ensure that those static fields
   * do not carry state from one test to another. */
  public abstract HttpServer initialize();

  /** Starts building a new GET request.
   * The path is relative to the root like eg newGet("/hello").
   * The request is performed against the server that was created in the {@link #initialize()}.
   * The base url is "http://localhost:" + serverPort */
  public ClientRequest newGet(String url) {
    return httpClient.newGet(url);
  }

  /** Starts building a new POST request.
   * The path is relative to the root like eg newGet("/hello").
   * The request is performed against the server that was created in the {@link #initialize()}.
   * The base url is "http://localhost:" + serverPort */
  public ClientRequest newPost(String url) {
    return httpClient.newPost(url);
  }

  /** Starts building a new PUT request.
   * The path is relative to the root like eg newGet("/hello").
   * The request is performed against the server that was created in the {@link #initialize()}.
   * The base url is "http://localhost:" + serverPort */
  public ClientRequest newPut(String url) {
    return httpClient.newPut(url);
  }

  /** Starts building a new DELETE request.
   * The path is relative to the root like eg newGet("/hello").
   * The request is performed against the server that was created in the {@link #initialize()}.
   * The base url is "http://localhost:" + serverPort */
  public ClientRequest newDelete(String url) {
    return httpClient.newDelete(url);
  }
}
