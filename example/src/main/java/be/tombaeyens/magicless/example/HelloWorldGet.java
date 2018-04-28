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
package be.tombaeyens.magicless.example;

import be.tombaeyens.magicless.routerservlet.PathRequestHandler;
import be.tombaeyens.magicless.routerservlet.ServerRequest;
import be.tombaeyens.magicless.routerservlet.ServerResponse;
import be.tombaeyens.magicless.db.Db;

public class HelloWorldGet extends PathRequestHandler {

  Db db;

  public HelloWorldGet(Db db) {
    super(GET, "/hello");
    this.db = db;
  }

  @Override
  public void handle(ServerRequest request, ServerResponse response) {
    response.headerContentType("text/plain");
    response.bodyString("hello world");
    response.statusOk();
  }
}
