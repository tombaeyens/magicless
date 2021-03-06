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

public class DoubleType implements DataType {

  @Override
  public String getDefaultSql() {
    return "DOUBLE"; // postgresql should overwrite to "DOUBLE PRECISION"
  }

  @Override
  public void setParameter(PreparedStatement statement, int i, Object value) {
    try {
      if (value!=null) {
        double doubleValue = ((Number) value).doubleValue();
        statement.setDouble(i, doubleValue);
      } else {
        statement.setNull(i, Types.DOUBLE);
      }
    } catch (SQLException e) {
      throw exceptionWithCause("set JDBC double parameter value "+value, e);
    }
  }

  @Override
  public boolean isRightAligned() {
    return true;
  }

  @Override
  public Double getResultSetValue(int index, ResultSet resultSet) {
    try {
      return resultSet.getDouble(index);
    } catch (SQLException e) {
      throw exceptionWithCause("get JDBC double value "+index+" from result set", e);
    }
  }
}
