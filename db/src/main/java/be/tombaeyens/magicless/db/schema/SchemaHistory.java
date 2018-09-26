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

import be.tombaeyens.magicless.db.Column;
import be.tombaeyens.magicless.db.Table;

/**
 * NOTE TO SELF: this documentation reflects the direction that I want change towards.  Impl doesn't yet match.
 *
 * Supports schema evolutions in a cluster without the need for bringing the cluster down
 * if the developers apply the following db upgrade procedure:
 *
 * 1) Create new table/columns
 * 2) Any process can start doing double writing to the old as well as to the new table/columns
 * 3) Ensure that all processes are on the right getWriter version
 * 4) Duplicate the old data (recent updates may already have been done in the new table/columns)
 * 5) Remove the writes to the old table/columns
 * 6) Remove the old table/columns
 */
public class SchemaHistory extends Table {

  public static final Column ID = new Column().name("id").typeVarchar(1024).primaryKey();
  public static final Column TIME = new Column().name("time").typeTimestamp();
  public static final Column PROCESS = new Column().name("process").typeVarchar(255);
  public static final Column TYPE = new Column().name("type").typeVarchar(1024);
  public static final Column VERSION = new Column().name("version").typeInteger();
  public static final Column DESCRIPTION = new Column().name("description").typeVarchar(1024);

  public static final String ID_LOCK = "lock";

  /** Single record with a known id (version) to ensure that contains the version and
   * is used to lock the db so that only one process updates the schema at a time */
  public static final String TYPE_LOCK = "lock";

  /** Logs when a {@link SchemaUpdate} is performed */
  public static final String TYPE_UPDATE = "update";

  /** A history record that logs the startup of a process.  All remain in db. */
  public static final String TYPE_STARTUP = "startup";

  /** A history record that logs the clean shutdown of a process.  All remain in db. */
  public static final String TYPE_SHUTDOWN = "shutdown";

  public static final SchemaHistory TABLE = new SchemaHistory();

  private SchemaHistory() {
    name("schemaHistory");
    column(ID);
    column(TIME);
    column(PROCESS);
    column(TYPE);
    column(DESCRIPTION);
    column(VERSION);
  }
}
