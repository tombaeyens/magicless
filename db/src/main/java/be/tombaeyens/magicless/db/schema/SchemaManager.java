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
package be.tombaeyens.magicless.db.schema;

import be.tombaeyens.magicless.db.Db;
import be.tombaeyens.magicless.db.SelectResults;
import be.tombaeyens.magicless.db.Tx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

import static be.tombaeyens.magicless.app.util.Exceptions.assertTrue;
import static be.tombaeyens.magicless.db.Condition.*;
import static be.tombaeyens.magicless.db.schema.SchemaHistory.*;

public abstract class SchemaManager {

  static Logger log = LoggerFactory.getLogger(SchemaManager.class);

  Db db;
  int applicationVersion;
  String nodeName;

  protected SchemaManager(Db db, int applicationVersion, String nodeName) {
    this.db = db;
    this.applicationVersion = applicationVersion;
    this.nodeName = nodeName;
  }

  protected abstract void createApplicationTables(Tx tx);

  public void ensureCurrentSchema() {
    if (!schemaHistoryExists()) {
      createApplicationTables();
    } else {
      int dbSchemaVersion = -1;
      while (dbSchemaVersion!=applicationVersion) {
        dbSchemaVersion = getDbSchemaVersion();
        if (dbSchemaVersion<applicationVersion) {
          if (acquireSchemaLock("appserver1")) {
            upgradeSchema(dbSchemaVersion);
            releaseSchemaLock();
          } else {
            try {
              Thread.sleep(5000);
            } catch (InterruptedException e) {
              log.debug("Waiting for other node to finish upgrade got interrupted");
            }
          }
        } else if (dbSchemaVersion<applicationVersion) {
          throw new RuntimeException("Please upgrade the application.");
        }
      }
    }
  }

  protected boolean acquireSchemaLock(String nodeName) {
    return db.tx(tx->{
      int updateCount = tx.newUpdate(SchemaHistory.TABLE)
        .set(DESCRIPTION, nodeName + " is upgrading the schema")
        .where(and(isNull(DESCRIPTION),
          equal(TYPE, TYPE_LOCK)))
        .execute();
      tx.setResult(updateCount==1);
    });
  }

  protected void releaseSchemaLock() {
  }

  protected void upgradeSchema(int currentDdbSchemaVersion) {
  }

  protected boolean schemaHistoryExists() {
    return db.tx(tx->{
      boolean schemaHistoryExists = tx.getTableNames().stream()
        .map(tableName->tableName.toLowerCase())
        .collect(Collectors.toList())
        .contains(SchemaHistory.TABLE.getName());
      tx.setResult(schemaHistoryExists);
    });
  }

  protected void createSchemaHistory() {
    db.tx(tx->{
      tx.newCreateTable(SchemaHistory.TABLE).execute();
      // tx.newInsert initial version record
      // tx.newInsert initial lock record
    });
  }

  protected void createApplicationTables() {
    db.tx(tx->{
      createApplicationTables(tx);
    });
  }

  protected int getDbSchemaVersion() {
    return db.tx(tx->{
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
