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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import static be.tombaeyens.magicless.app.util.Exceptions.exceptionWithCause;

public class LongType implements DataType {

  @Override
  public String getDefaultSql() {
    return "INTEGER";
  }

  @Override
  public void setParameter(PreparedStatement statement, int i, Object value) {
    try {
      if (value!=null) {
        long longValue = value!=null ? ((Number) value).longValue() : null;
        statement.setLong(i, longValue);
      } else {
        statement.setNull(i, Types.INTEGER);
      }
    } catch (SQLException e) {
      throw exceptionWithCause("set JDBC long parameter value "+value, e);
    }
  }

  @Override
  public boolean isRightAligned() {
    return true;
  }

  @Override
  public Long getResultSetValue(int index, ResultSet resultSet) {
    try {
      return resultSet.getLong(index);
    } catch (SQLException e) {
      throw exceptionWithCause("get JDBC long value "+index+" from result set", e);
    }
  }
}
