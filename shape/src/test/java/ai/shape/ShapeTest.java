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
package ai.shape;

import ai.shape.datasets.DatasetsTable;
import be.tombaeyens.magicless.db.Db;
import be.tombaeyens.magicless.db.schema.SchemaManager;
import be.tombaeyens.magicless.httpclient.ClientResponse;
import be.tombaeyens.magicless.httpclient.GsonSerializer;
import be.tombaeyens.magicless.httpclient.Serializer;
import be.tombaeyens.magicless.httpserver.HttpServer;
import be.tombaeyens.magicless.httptest.HttpTest;
import com.google.gson.Gson;
import org.junit.After;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShapeTest extends HttpTest {

  public static Logger log = LoggerFactory.getLogger(ShapeTest.class);

  static {
    System.setErr(System.out);
  }

  protected static Shape shape = null;

  @Override
  protected void setUpStatic() {
    super.setUpStatic();

    // Create the DB schema
    shape.get(SchemaManager.class).createSchema();
  }

  @After
  public void tearDown() {
    if (shape!=null) {
      shape.get(Db.class).tx(tx-> {
        tx.newSelectStarFrom(DatasetsTable.TABLE).execute();
        tx.newDelete(DatasetsTable.TABLE).execute();
      });
    }
  }

  private static class TestConfiguration extends ShapeConfiguration {
    public TestConfiguration() {
      put(DB_PREFIX+".url",   "jdbc:h2:mem:shapetest");
    }
  }

  @Override
  public HttpServer createHttpServer() {
    shape = new Shape(new TestConfiguration());
    shape.start(); // also starts the HttpServer
    return shape.get(HttpServer.class);
  }

  @Override
  protected Serializer createSerializer() {
    Gson gson = shape.get(Gson.class);
    return new GsonSerializer(gson);
  }

  public ClientResponse execute(Command command) {
    return newPost("/command")
            .bodyJson(command)
            .execute()
            .assertStatusOk();
  }

  public ClientResponse execute(Query query) {
    return newPost("/query")
            .bodyJson(query)
            .execute()
            .assertStatusOk();
  }

}
