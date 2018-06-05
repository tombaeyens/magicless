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
package be.tombaeyens.magicless.db.types;

import be.tombaeyens.magicless.db.DataType;

import java.sql.*;

import static be.tombaeyens.magicless.app.util.Exceptions.exceptionWithCause;

public class VarcharType implements DataType {

  int n;

  public VarcharType(int n) {
    this.n = n;
  }

  public int getN() {
    return n;
  }

  @Override
  public String getDefaultSql() {
    return "VARCHAR("+n+")";
  }

  @Override
  public void setParameter(PreparedStatement statement, int i, Object value) {
    try {
      String string = null;

      if (value instanceof String) {
        string = (String) value;
      } else if (value!=null) {
        string = value.toString();
      }
      statement.setString(i, string);

    } catch (SQLException e) {
      throw exceptionWithCause("set JDBC varchar parameter value "+value, e);
    }
  }

  @Override
  public String toText(Object value) {
    return value!=null ? "'"+value.toString()+"'" : "null";
  }

  @Override
  public boolean isRightAlinged() {
    return false;
  }

  @Override
  public String getResultSetValue(int index, ResultSet resultSet) {
    try {
      return resultSet.getString(index);
    } catch (SQLException e) {
      throw exceptionWithCause("get JDBC string result set value "+index, e);
    }
  }
}
