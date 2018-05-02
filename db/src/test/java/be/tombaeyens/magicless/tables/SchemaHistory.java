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
package be.tombaeyens.magicless.tables;

import be.tombaeyens.magicless.db.Column;
import be.tombaeyens.magicless.db.Table;

public class SchemaHistory extends Table {

  public static final SchemaHistory TABLE = new SchemaHistory();

  public static final Column ID = new Column().name("id").typeVarchar(1024).primaryKey();
  public static final Column TYPE = new Column().name("type").typeVarchar(1024);
  public static final Column DESCRIPTION = new Column().name("description").typeVarchar(1024);
  public static final Column VERSION_SCHEMA = new Column().name("versionSchema").typeInteger();
  public static final Column UPDATE_INDEX = new Column().name("updateIndex").typeInteger();
  public static final Column UPDATE_START = new Column().name("updateStart").typeTimestamp();
  public static final Column UPDATE_END = new Column().name("updateEnd").typeTimestamp();

  public static final String TYPE_VERSION = "version";
  public static final String TYPE_LOCK = "lock";
  public static final String TYPE_UPDATE = "update";

  private SchemaHistory() {
    name("schemaHistory");
    column(ID);
    column(TYPE);
    column(DESCRIPTION);
    column(VERSION_SCHEMA);
    column(UPDATE_INDEX);
    column(UPDATE_START);
    column(UPDATE_END);
  }
}
