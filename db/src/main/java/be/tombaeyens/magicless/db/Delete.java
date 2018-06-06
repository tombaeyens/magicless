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

import static be.tombaeyens.magicless.app.util.Exceptions.assertNotNullParameter;

public class Delete extends AliasableStatement {

  Table table;
  Condition whereCondition;

  public Delete(Tx tx, Table table, String alias) {
    super(tx);
    assertNotNullParameter(table, "table");
    this.table = table;
    alias(table, alias);
  }

  public int execute() {
    Dialect dialect = tx.getDb().getDialect();
    SqlBuilder sql = dialect.newSqlBuilder();

    sql.append("DELETE FROM ");
    sql.append(table.getName());
    String alias = getAlias(table);
    if (alias!=null) {
      sql.append("AS ");
      sql.append(alias);
    }

    if (whereCondition!=null) {
      sql.append("\nWHERE ");
      whereCondition.appendTo(this, sql);
    }

    sql.append(";");

    return executeUpdate(sql);
  }

  public Delete where(Condition whereCondition) {
    this.whereCondition = whereCondition;
    return this;
  }
}
