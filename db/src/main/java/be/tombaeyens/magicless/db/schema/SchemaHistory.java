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

public class SchemaHistory extends Table {

  public static final Column ID = new Column().name("id").typeVarchar(1024).primaryKey();
  public static final Column TIME = new Column().name("time").typeTimestamp();
  public static final Column PROCESS_REF = new Column().name("processRef").typeVarchar(255);
  public static final Column TYPE = new Column().name("type").typeVarchar(1024);
  public static final Column DESCRIPTION = new Column().name("description").typeVarchar(1024);
  public static final Column VERSION = new Column().name("version").typeInteger();

  /** single record to ensure that only one process updates the schema at a time */
  public static final String TYPE_LOCK = "lock";

  /** single record to ensure that only one process updates the schema at a time */
  public static final String TYPE_VERSION = "version";
  public static final String TYPE_HEARTBEAT = "heartBeat";
  public static final String TYPE_STARTUP = "startup";
  public static final String TYPE_SHUTDOWN = "shutdown";
  public static final String TYPE_UPDATE = "update";

  public static final SchemaHistory TABLE = new SchemaHistory();

  private SchemaHistory() {
    name("schemaHistory");
    column(ID);
    column(TIME);
    column(PROCESS_REF);
    column(TYPE);
    column(DESCRIPTION);
    column(VERSION);
  }
}
