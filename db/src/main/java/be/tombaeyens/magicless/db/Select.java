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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static be.tombaeyens.magicless.app.util.Exceptions.*;

public class Select extends Aliasable {

  Tx tx;
  List<SelectField> fields;
  List<SelectFrom> froms;
  Condition whereCondition;

  public Select(Tx tx, SelectField... fields) {
    this.tx = tx;
    this.fields = Arrays.asList(fields);
  }

  public SelectResults execute() throws RuntimeException {
    Dialect dialect = tx.getDb().getDialect();
    SqlBuilder sql = dialect.newSqlBuilder();

    assertNotEmptyCollection(fields, "fields is empty. Specify at least one non-null Column or Function in Tx.newSelect(...)");
    sql.append("SELECT ");
    for (int i = 0; i<fields.size(); i++) {
      if (i>0) {
        sql.append(", ");
      }
      SelectField selectField = fields.get(i);
      selectField.appendTo(this,sql);
    }
    sql.append(" \n");

    assertNotEmptyCollection(froms, "froms is empty. Specify at least one non-null select.from(...)");
    sql.append("FROM ");
    for (int i = 0; i<froms.size(); i++) {
      if (i>0) {
        sql.append(", \n     ");
      }
      SelectFrom from = froms.get(i);
      Table table = from.getTable();
      String alias = aliases.get(table);
      String fromSql = alias!=null ? table.getName()+" AS "+alias : table.getName();
      sql.append(fromSql);
    }
    sql.append("\n");

    if (whereCondition!=null) {
      sql.append("WHERE ");
      whereCondition.appendTo(this, sql);
    }

    String sqlText = sql.getSql();
    PreparedStatement statement = null;
    try {
      statement = tx
        .getConnection()
        .prepareStatement(sqlText);
    } catch (SQLException e) {
      throw exceptionWithCause("prepare JDBC statement: \n"+sqlText, e);
    }

    sql.applyParameter(statement);

    try {
      ResultSet resultSet = statement.executeQuery();
      return new SelectResults(this, resultSet, sqlText);
    } catch (SQLException e) {
      throw exceptionWithCause("execute query \n"+sqlText+"\n-->", e);
    }
  }
  public Select from(Table table) {
    from(table, null);
    return this;
  }

  public Select from(Table table, String alias) {
    assertNotNullParameter(table, "table");
    if (froms==null) {
      froms = new ArrayList<>();
    }
    froms.add(new SelectFrom(table, alias));
    alias(table, alias);
    return this;
  }

  public Select where(Condition whereCondition) {
    this.whereCondition = whereCondition;
    return this;
  }

  public Integer getSelectorIndex(Column column) {
    for (int i = 0; i<fields.size(); i++) {
      SelectField selectField = fields.get(i);
      if (selectField==column) {
        return i;
      }
    }
    return null;
  }

  public Tx getTx() {
    return tx;
  }
}
