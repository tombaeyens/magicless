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

import be.tombaeyens.magicless.app.container.Inject;
import be.tombaeyens.magicless.app.util.Time;
import be.tombaeyens.magicless.db.Condition;
import be.tombaeyens.magicless.db.Db;
import be.tombaeyens.magicless.db.SelectResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.stream.Collectors;

import static be.tombaeyens.magicless.app.util.Exceptions.assertNotNullParameter;
import static be.tombaeyens.magicless.app.util.Exceptions.assertTrue;
import static be.tombaeyens.magicless.db.Condition.*;
import static be.tombaeyens.magicless.db.schema.SchemaHistory.*;

public class SchemaManager {

  static Logger log = LoggerFactory.getLogger(SchemaManager.class);

  @Inject
  Db db;
  SchemaUpdate[] updates;

  /** constructor used when using a {@link be.tombaeyens.magicless.app.container.Container}
   * to inject the db */
  public SchemaManager(SchemaUpdate... updates) {
    // this.db is initialized by the container;
    this.updates = updates;
  }

  /** constructor used to construct SchemaManager programatically */
  public SchemaManager(Db db, SchemaUpdate... updates) {
    assertNotNullParameter(db, "db");
    assertNotNullParameter(updates, "updates");
    this.db = db;
    this.updates = updates;
  }

  /** ENSURE that previously released SchemaUpdates do not change logically
   * (thay may have run, bugfixes are allowed) and that unreleased changes always are
   * appended at the end. */
  public void ensureCurrentSchema() {
    int applicationVersion = updates.length;
    if (!schemaHistoryExists()) {
      createSchemaHistory();
    }
    int dbSchemaVersion = -1;
    while (dbSchemaVersion!=applicationVersion) {
      dbSchemaVersion = getDbSchemaVersion();
      if (dbSchemaVersion<applicationVersion) {
        if (acquireSchemaLock(applicationVersion)) {
          upgradeSchema(updates, dbSchemaVersion, applicationVersion);
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

  /** Skips locking of the db and assumes that a) no db has been created yet
   * and b) no other servers will attempt to create the schema concurrently.
   * Like eg for test scenarios. */
  public void createSchema() {
    createSchemaHistory();
    for (int updateIndex=0; updateIndex<updates.length; updateIndex++) {
      final int finalUpdateIndex = updateIndex;
      db.tx(tx->{
        updates[finalUpdateIndex].update(tx);
      });
    }
    updateDbSchemaVersion(updates.length, updates.length);
  }

  protected boolean acquireSchemaLock(int applicationVersion) {
    return db.tx(tx->{
      int updateCount = tx.newUpdate(SchemaHistory.TABLE)
        .set(DESCRIPTION, db.getProcessRef() + " is upgrading schema to version " + applicationVersion)
        .where(and(isNull(DESCRIPTION),
          equal(TYPE, TYPE_VERSION)))
        .execute();
      if (updateCount>1) {
        throw new RuntimeException("Inconsistent database state: More than 1 version record in schemaHistory table: "+updateCount);
      }
      tx.setResult(updateCount==1);
    });
  }

  protected void releaseSchemaLock() {
    // TODO
  }

  protected void upgradeSchema(SchemaUpdate[] updates, int currentDdbSchemaVersion, int applicationVersion) {
    for (int version=currentDdbSchemaVersion+1; version<=applicationVersion; version++) {
      final int updateIndex = version-1;
      db.tx(tx->{
        updates[updateIndex].update(tx);
      });
      updateDbSchemaVersion(applicationVersion, version);
    }
  }

  private void updateDbSchemaVersion(int applicationVersion, int updateVersion) {
    db.tx(tx->{
      tx.newInsert(SchemaHistory.TABLE)
        .set(SchemaHistory.ID, UUID.randomUUID().toString())
        .set(SchemaHistory.DESCRIPTION, "Application version "+applicationVersion+" executed update to version "+updateVersion)
        .set(SchemaHistory.VERSION, updateVersion)
        .set(SchemaHistory.TIME, Time.now())
        .execute();
      tx.newUpdate(SchemaHistory.TABLE)
        .set(SchemaHistory.VERSION, updateVersion)
        .where(Condition.equal(SchemaHistory.TYPE, TYPE_VERSION))
        .execute();
    });
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
      tx.newInsert(SchemaHistory.TABLE)
        .set(SchemaHistory.ID, "v")
        .set(SchemaHistory.TYPE, SchemaHistory.TYPE_VERSION)
        .set(SchemaHistory.VERSION, 0)
        .execute();
    });
  }

  protected int getDbSchemaVersion() {
    return db.tx(tx->{
      SelectResults selectResults = tx
        .newSelect(SchemaHistory.VERSION)
        .where(equal(TYPE, TYPE_VERSION))
        .execute();
      boolean hasResult = selectResults.next();
      assertTrue(hasResult, "No DB schema version record found");
      selectResults.logResults(); // because .next did not return false, logging results is not triggered automatically.
      tx.setResult(selectResults.get(SchemaHistory.VERSION));
    });
  }

  public String getNodeName() {
    return nodeName;
  }
}
