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
package be.tombaeyens.magicless.routerservlet;

/** RequestHandler's must be thread safe.  Meaning that one request handler will be used
 * to handle all the request in a RouterServlet */
public interface RequestHandler {

  /** One of the {@link be.tombaeyens.magicless.app.util.Http.Methods} */
  String method();

  /** True if this request handler handles is applicable for request.getPathInfo() */
  boolean pathMatches(ServerRequest request);

  /** Handles the request. It's guaranteed that a pathMatches has been called before for each request. */
  void handle(ServerRequest request, ServerResponse response);

}
