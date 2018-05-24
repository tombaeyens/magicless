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

import be.tombaeyens.magicless.app.util.Exceptions;
import be.tombaeyens.magicless.db.impl.SqlBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static be.tombaeyens.magicless.app.util.Exceptions.assertNotNull;

public class CreateTable {

  Tx tx;
  Table table;

  public CreateTable(Tx tx, Table table) {
    this.tx = tx;
    this.table = table;
  }

  public void execute() {
    Dialect dialect = tx.getDb().getDialect();
    SqlBuilder sql = dialect.newSqlBuilder();
    sql.append("CREATE TABLE ");
    sql.append(table.getName());
    sql.append(" ( \n  ");

    Map<String, Column> columnsByName = table.getColumns();
    assertNotNull(columnsByName, "Table must have at least one column");
    List<Column> columns = new ArrayList<>(columnsByName.values());
    for (int i = 0; i<columns.size(); i++) {
      if (i>0) {
        sql.append(", \n  ");
      }
      Column column = columns.get(i);
      assertNotNull(column, "Column " + i + " is null");
      DataType type = column.getType();
      assertNotNull(type, "Column " + i + " has type null");
      List<Constraint> constraints = column.getConstraints();
      sql.append(column.getName());
      sql.append(" ");
      sql.appendDataType(type);

      if (constraints!=null) {
        for (Constraint constraint : constraints) {
          sql.append(" ");
          sql.appendConstraint(constraint);
        }
      }
    }
    sql.append(" \n);");

    String sqlText = sql.getSql();
    try {
      tx.logSQL(sqlText);
      tx.getConnection()
        .prepareStatement(sqlText)
        .execute();
    } catch (SQLException e) {
      throw Exceptions.exceptionWithCause("Create table "+table.getName()+": \n"+sqlText, e);
    }
  }

  public Tx getTx() {
    return tx;
  }
}
