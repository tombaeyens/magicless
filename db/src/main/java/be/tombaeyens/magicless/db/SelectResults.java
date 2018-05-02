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

import java.sql.ResultSet;
import java.sql.SQLException;

import static be.tombaeyens.magicless.app.util.Exceptions.assertNotNull;

public class SelectResults {

  Select select;
  ResultSet resultSet;
  String sql;

  public SelectResults(Select select, ResultSet resultSet, String sql) {
    this.select = select;
    this.resultSet = resultSet;
    this.sql = sql;
  }

  public boolean next() {
    try {
      return resultSet.next();
    } catch (SQLException e) {
      throw Exceptions.exceptionWithCause("get next() on JDBC result set for select \n"+sql, e);
    }
  }

  public <T> T get(Column column) {
    Integer index = select.getSelectorIndex(column);
    assertNotNull(index, "Could find index position of column "+column+" in select \n"+sql);
    return column.getType().getResultSetValue(index, resultSet);
  }
}
