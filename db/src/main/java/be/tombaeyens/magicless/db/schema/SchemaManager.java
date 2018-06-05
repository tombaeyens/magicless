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
import be.tombaeyens.magicless.db.Db;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static be.tombaeyens.magicless.app.util.Exceptions.assertNotNullParameter;
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



  ////////////////////////////////////////////////////////////
  //
  //  SEE NOTE TO SELF IN SchemaHistory
  //
  ////////////////////////////////////////////////////////////


  /** ENSURE that previously released SchemaUpdates do not change logically
   * (thay may have run, bugfixes are allowed) and that unreleased changes always are
   * appended at the end. */
  public void ensureCurrentSchema() {
    if (!schemaHistoryExists()) {
      createSchemaHistory();
    }
    if (!isSchemaUpToDate()) {
      if (acquireSchemaLock()) {
        upgradeSchema();
        releaseSchemaLock();
      } else {
        try {
          Thread.sleep(5000);
        } catch (InterruptedException e) {
          log.debug("Waiting for other node to finish upgrade got interrupted");
        }
      }
    }
  }

  private boolean isSchemaUpToDate() {
    Set<String> dbSchemaUpdates = getDbSchemaUpdates();
    for (SchemaUpdate update: updates) {
      if (!dbSchemaUpdates.contains(update.getId())) {
        return false;
      }
    }
    return true;
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
  }

  protected boolean acquireSchemaLock() {
    return db.tx(tx->{
      int updateCount = tx.newUpdate(SchemaHistory.TABLE)
        .set(DESCRIPTION, db.getProcess() + " is upgrading schema")
        .set(PROCESS, db.getProcess())
        .where(and(
          isNull(DESCRIPTION),
          isNull(PROCESS),
          equal(TYPE, TYPE_LOCK)))
        .execute();
      if (updateCount>1) {
        throw new RuntimeException("Inconsistent database state: More than 1 version record in schemaHistory table: "+updateCount);
      }
      tx.setResult(updateCount==1);
    });
  }

  protected boolean releaseSchemaLock() {
    return db.tx(tx->{
      int updateCount = tx.newUpdate(SchemaHistory.TABLE)
        .set(DESCRIPTION, null)
        .set(PROCESS, null)
        .where(and(
          isNull(PROCESS),
          equal(TYPE, TYPE_LOCK)))
        .execute();
      if (updateCount>1) {
        throw new RuntimeException("Inconsistent database state: More than 1 version record in schemaHistory table: "+updateCount);
      }
      tx.setResult(updateCount==1);
    });
  }

  protected void upgradeSchema() {
    Set<String> dbSchemaUpdates = getDbSchemaUpdates();
    for (SchemaUpdate update: updates) {
      if (!dbSchemaUpdates.contains(update.getId())) {
        db.tx(tx->{
          update.update(tx);

          int updateCount = tx.newInsert(SchemaHistory.TABLE)
            .set(ID, update.getId())
            .set(TIME, Time.now())
            .set(PROCESS, db.getProcess())
            .set(TYPE, TYPE_UPDATE)
            .set(DESCRIPTION, "Executed update " + update.getId())
            .execute();
          if (updateCount!=1) {
            throw new RuntimeException("Expected 1 insert of update "+update.getId());
          }
        });
      }
    }
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
        .set(SchemaHistory.ID, ID_LOCK)
        .set(SchemaHistory.TYPE, TYPE_LOCK)
        .execute();
    });
  }

  /** The SchemaUpdate IDs that already have been applied on the DB schema */
  protected Set<String> getDbSchemaUpdates() {
    return db.tx(tx->{
      tx.setResult(
        tx.newSelect(SchemaHistory.ID)
          .where(equal(TYPE, TYPE_UPDATE))
          .execute()
          .stream()
          .map(selectResults->selectResults.get(SchemaHistory.ID))
          .collect(Collectors.toSet()));
    });
  }

  public void processStarts() {
    db.tx(tx->{
      int updateCount = tx.newInsert(SchemaHistory.TABLE)
        .set(ID, UUID.randomUUID().toString())
        .set(TIME, Time.now())
        .set(PROCESS, db.getProcess())
        .set(TYPE, TYPE_STARTUP)
        .set(VERSION, updates.length)
        .set(DESCRIPTION, "Process " + db.getProcess() + " started")
        .execute();
    });
  }
}
