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
package be.tombaeyens.magicless.db.impl;

import be.tombaeyens.magicless.db.Constraint;
import be.tombaeyens.magicless.db.DataType;
import be.tombaeyens.magicless.db.Dialect;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class SqlBuilder {

  Dialect dialect;
  StringBuilder sql = new StringBuilder();
  List<Parameter> parameters;
  String logText = null;

  public SqlBuilder(Dialect dialect) {
    this.dialect = dialect;
  }

  public void append(String sql) {
    this.sql.append(sql);
  }

  public void addParameter(Object value, DataType type) {
    if (parameters==null) {
      parameters = new ArrayList<>();
    }
    parameters.add(new Parameter(value, type));
  }

  public String getSql() {
    this.logText = this.sql.toString();
    return logText;
  }

  public void appendDataType(DataType type) {
    append(dialect.getDataTypeSql(type));
  }

  public void appendConstraint(Constraint constraint) {
    append(constraint.getDefaultSql());
  }

  public Dialect getDialect() {
    return dialect;
  }

  public void applyParameter(PreparedStatement statement) {
    if (parameters!=null) {
      for (int i=0; i<parameters.size(); i++) {
        Parameter parameter = parameters.get(i);
        DataType type = parameter.getType();
        Object value = parameter.getValue();
        type.setParameter(statement, i + 1, value);
        logText = logText.replaceFirst("\\?", type.toText(value));
      }
    }
  }

  public String getLogText() {
    return logText;
  }
}
