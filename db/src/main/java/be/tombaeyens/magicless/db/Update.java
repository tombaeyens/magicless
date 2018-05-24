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

import java.util.ArrayList;
import java.util.List;

import static be.tombaeyens.magicless.app.util.Exceptions.assertNotEmptyCollection;
import static be.tombaeyens.magicless.app.util.Exceptions.assertNotNullParameter;

public class Update extends AliasableStatement {

  Table table;
  List<UpdateSet> sets;
  Condition whereCondition;

  public Update(Tx tx, Table table, String alias) {
    super(tx);
    assertNotNullParameter(table, "table");
    this.table = table;
    alias(table, alias);
  }

  public int execute() {
    Dialect dialect = tx.getDb().getDialect();
    SqlBuilder sql = dialect.newSqlBuilder();

    sql.append("UPDATE ");
    sql.append(table.getName());
    String alias = getAlias(table);
    if (alias!=null) {
      sql.append("AS ");
      sql.append(alias);
    }

    sql.append("\nSET ");
    assertNotEmptyCollection(sets, "sets is empty. Specify at least one non-null update.set(...)");
    for (int i = 0; i<sets.size(); i++) {
      if (i>0) {
        sql.append(", \n    ");
      }
      UpdateSet updateSet = sets.get(i);
      updateSet.appendTo(this,sql);
    }
    sql.append(" \n");

    if (whereCondition!=null) {
      sql.append("WHERE ");
      whereCondition.appendTo(this, sql);
    }

    sql.append(";");

    return executeUpdate(sql);
  }

  public Update set(Column column, Object value) {
    if (sets==null) {
      sets = new ArrayList<>();
    }
    sets.add(new UpdateSet(column, value));
    return this;
  }

  public Update where(Condition whereCondition) {
    this.whereCondition = whereCondition;
    return this;
  }
}
