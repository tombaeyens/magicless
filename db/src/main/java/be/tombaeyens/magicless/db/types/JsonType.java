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

import be.tombaeyens.magicless.app.util.Io;
import be.tombaeyens.magicless.db.DataType;

import java.io.Reader;
import java.io.StringReader;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Date;

import static be.tombaeyens.magicless.app.util.Exceptions.exceptionWithCause;

public class JsonType implements DataType {

  @Override
  public String getDefaultSql() {
    return "CLOB";
  }

  @Override
  public void setParameter(PreparedStatement statement, int i, Object value) {
    try {
      if (value!=null) {
        if (value instanceof String) {
          statement.setClob(i, new StringReader((String) value));
        } else {
          throw new RuntimeException("Unsupported data type: "+value);
        }
      }

    } catch (SQLException e) {
      throw exceptionWithCause("set JDBC clob parameter value "+value, e);
    }
  }

  @Override
  public String getResultSetValue(int index, ResultSet resultSet) {
    try {
      Reader reader = resultSet.getCharacterStream(index);
      return reader!=null ? Io.getString(reader) : null;
    } catch (SQLException e) {
      throw exceptionWithCause("get JDBC clob result set value "+index, e);
    }
  }

}
