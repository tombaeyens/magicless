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

import be.tombaeyens.magicless.db.impl.Parameters;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static be.tombaeyens.magicless.app.util.Exceptions.assertNotNull;
import static be.tombaeyens.magicless.app.util.Exceptions.assertSame;
import static be.tombaeyens.magicless.app.util.Exceptions.exceptionWithCause;

public class Insert extends Statement {

  Table table;
  List<ColumnValue> columnValues = new ArrayList<>();

  public Table getTable() {
    return table;
  }

  public static class ColumnValue {
    Column column;
    Object value;
    public ColumnValue(Column column, Object value) {
      this.column = column;
      this.value = value;
    }
    public Column getColumn() {
      return column;
    }
    public Object getValue() {
      return value;
    }
  }

  public Insert(Tx tx, Table table) {
    super(tx);
    this.table = table;
  }

  public Insert set(Column column, Object value) {
    assertNotNull(column.getTable(), "Column %s isn't added to the table.", column.getName());
    assertSame(table, column.getTable(), "The provided column must be from the table passed in the constructor");
    if (value!=null) {
      columnValues.add(new ColumnValue(column, value));
    }
    return this;
  }

  public int execute() {
    String sql = getDialect().buildInsertSql(this);
    return executeUpdate(sql);
  }

  @Override
  protected void collectParameters(Parameters parameters) {
    columnValues.stream()
      .forEach(columnValue -> parameters.addParameter(columnValue.getValue(), columnValue.getColumn().getType()));
  }

  protected String getPastTense() {
    return "Inserted";
  }

  public List<ColumnValue> getColumnValues() {
    return columnValues;
  }
}
