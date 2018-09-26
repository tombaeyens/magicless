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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

import static be.tombaeyens.magicless.app.util.Exceptions.exceptionWithCause;

public class TimestampType implements DataType {

  @Override
  public String getDefaultSql() {
    return "TIMESTAMP";
  }

  @Override
  public void setParameter(PreparedStatement statement, int i, Object value) {
    try {
      Timestamp timestamp = null;

      if (value!=null) {
        if (value instanceof Timestamp) {
          timestamp = (Timestamp) value;
        } else if (value instanceof Date) {
          timestamp = new Timestamp(((Date)value).getTime());
        } else if (value instanceof LocalDateTime) {
          timestamp = Timestamp.valueOf((LocalDateTime)value);
        } else {
          throw new RuntimeException("Unsupported data type: "+value);
        }
        statement.setTimestamp(i, timestamp);
      }

    } catch (SQLException e) {
      throw exceptionWithCause("set JDBC timestamp parameter value "+value, e);
    }
  }

  @Override
  public LocalDateTime getResultSetValue(int index, ResultSet resultSet) {
    try {
      Timestamp timestamp = resultSet.getTimestamp(index);
      return timestamp!=null ? timestamp.toLocalDateTime() : null;
    } catch (SQLException e) {
      throw exceptionWithCause("get JDBC timestamp result set value "+index, e);
    }
  }

}
