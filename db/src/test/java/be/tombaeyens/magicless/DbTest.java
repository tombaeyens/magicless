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
import be.tombaeyens.magicless.db.SelectResults;
import be.tombaeyens.magicless.tables.SchemaHistory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

import static be.tombaeyens.magicless.app.util.Exceptions.assertTrue;
import static be.tombaeyens.magicless.db.Condition.and;
import static be.tombaeyens.magicless.db.Condition.equal;
import static be.tombaeyens.magicless.db.Condition.isNull;
import static be.tombaeyens.magicless.tables.SchemaHistory.*;

public class DbTest {

  static Logger log = LoggerFactory.getLogger(DbTest.class);

  private static final int APPLICATION_VERSION = 1;

  @Test
  public void testDb() {
    Db db = new Db(new DbConfiguration()
      .url("jdbc:h2:mem:test"));

    ensureCurrentSchema(db);

    db.tx(tx->{

//
//      tx.newUpdate(
    });
  }

  private void ensureCurrentSchema(Db db) {
    if (!schemaHistoryExists(db)) {
      createSchema(db);
    } else {
      int dbSchemaVersion = -1;
      while (dbSchemaVersion!=APPLICATION_VERSION) {
        dbSchemaVersion = getDbSchemaVersion(db);
        if (dbSchemaVersion<APPLICATION_VERSION) {
          if (lockSchema(db, "appserver1")) {
            upgradeSchema(db, dbSchemaVersion);
            releaseSchema(db);
          } else {
            try {
              Thread.sleep(5000);
            } catch (InterruptedException e) {
              log.debug("Waiting for other node to finish upgrade got interrupted");
            }
          }
        } else if (dbSchemaVersion<APPLICATION_VERSION) {
          throw new RuntimeException("Please upgrade the application.");
        }
      }
    }
  }

  public boolean lockSchema(Db db, String nodeName) {
    return db.tx(tx->{
      int updateCount = tx.newSqlUpdate(SchemaHistory.TABLE)
        .set(DESCRIPTION, nodeName + " is upgrading the schema")
        .where(and(isNull(DESCRIPTION),
                   equal(TYPE, TYPE_LOCK)))
        .execute();
      tx.setResult(updateCount==1);
    });
  }

  public void upgradeSchema(Db db, int currentDdbSchemaVersion) {

  }

  public void releaseSchema(Db db) {
  }

  public boolean schemaHistoryExists(Db db) {
    return db.tx(tx->{
      boolean schemaHistoryExists = tx.getTableNames().stream()
        .map(tableName->tableName.toLowerCase())
        .collect(Collectors.toList())
        .contains(SchemaHistory.TABLE.getName());
      tx.setResult(schemaHistoryExists);
    });
  }

  public void createSchema(Db db) {
    db.tx(tx->{
      tx.newCreateTable(SchemaHistory.TABLE).execute();
    });
  }

  public int getDbSchemaVersion(Db db) {
    db.tx(tx->{
      SelectResults selectResults = tx
        .newSelect(SchemaHistory.VERSION_SCHEMA)
        .where(equal(TYPE, TYPE_VERSION))
        .execute();
      boolean hasResult = selectResults.next();
      assertTrue(hasResult, "No DB schema version record found");
      tx.setResult(selectResults.get(SchemaHistory.VERSION_SCHEMA));
    });
  }
}
