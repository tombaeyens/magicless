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


import be.tombaeyens.magicless.app.container.Container;
import be.tombaeyens.magicless.app.util.Configuration;
import be.tombaeyens.magicless.db.DbConfiguration;
import be.tombaeyens.magicless.httpserver.HttpServer;

public class ExampleApplication extends Container {

  public static void main(String[] args) {
    Configuration configuration = new Configuration();
    configuration.loadConfigurationFromResource("example.properties");
    configuration.loadConfigurationFromSystemProperties();

    ExampleApplication exampleApplication = new ExampleApplication(configuration);
    exampleApplication.start();
    exampleApplication.get(HttpServer.class).join();
  }

  public ExampleApplication(Configuration configuration) {
    // addFactory(Db.class, createExampleDbConfiguration(configuration));
    add(createExampleHttpServer(configuration));

    initialize();
  }

  protected DbConfiguration createExampleDbConfiguration(Configuration configuration) {
    return new DbConfiguration(configuration, "example.db");
  }

  protected ExampleHttpServer createExampleHttpServer(Configuration configuration) {
    return new ExampleHttpServer(configuration);
  }
}
