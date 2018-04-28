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

import be.tombaeyens.magicless.routerservlet.RouterServlet;
import be.tombaeyens.magicless.app.container.Initializable;
import be.tombaeyens.magicless.app.util.Configuration;
import be.tombaeyens.magicless.db.Db;
import be.tombaeyens.magicless.httpserver.HttpServer;

public class ExampleHttpServer extends HttpServer implements Initializable<ExampleApplication> {

  public ExampleHttpServer(Configuration configuration) {
    super(configuration, "http.server");
  }

  @Override
  public void initialize(ExampleApplication application) {
    Db db = application.getOpt(Db.class);

    RouterServlet servlet = new RouterServlet();

    servlet.requestHandler(new HelloWorldGet(db));

    servlet(servlet, "/*");
  }
}
