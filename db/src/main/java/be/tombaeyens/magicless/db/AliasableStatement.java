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
package be.tombaeyens.magicless.db;

import be.tombaeyens.magicless.db.impl.SqlBuilder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import static be.tombaeyens.magicless.app.util.Exceptions.exceptionWithCause;
import static be.tombaeyens.magicless.db.Db.DB_LOGGER;

public class AliasableStatement extends Statement {

  Map<Table,String> aliases;

  public AliasableStatement(Tx tx) {
    super(tx);
  }

  public String getQualifiedColumnName(Column column) {
    String alias = aliases!=null ? aliases.get(column.getTable()) : null;
    return alias!=null ? alias+"."+column.getName() : column.getName();
  }

  protected AliasableStatement alias(Table table, String alias) {
    if (aliases==null) {
      aliases = new LinkedHashMap<>();
    }
    aliases.put(table, alias);
    return this;
  }

  protected String getAlias(Table table) {
    return aliases.get(table);
  }

}