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

import be.tombaeyens.magicless.db.Db;
import be.tombaeyens.magicless.db.DbConfiguration;
import be.tombaeyens.magicless.db.Tx;
import be.tombaeyens.magicless.db.schema.SchemaManager;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbTest {

  static Logger log = LoggerFactory.getLogger(DbTest.class);

  private static final int APPLICATION_VERSION = 1;

  @Test
  public void testDb() {
    Db db = new Db(new DbConfiguration()
      .url("jdbc:h2:mem:test"));

    SchemaManager schemaManager = new SchemaManager(db, APPLICATION_VERSION, "testnode1") {
      @Override
      protected void createApplicationTables(Tx tx) {

      }
    };

    schemaManager.ensureCurrentSchema();
  }

  // TODO move ensureCurrentSchema(Db db) in some SchemaManager
  // Add drop db to schema manager (which delegates to the dialect)


}
