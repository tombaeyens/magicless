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

import be.tombaeyens.magicless.db.DataType;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parameters {

  List<Parameter> parameters;

  public void addParameter(Object value, DataType type) {
    if (parameters==null) {
      parameters = new ArrayList<>();
    }
    parameters.add(new Parameter(value, type));
  }

  public String toLogSql(String jdbcSql) {
    String[] sqlSplit = jdbcSql.split("//?");
    if (sqlSplit.length>1) {
      if (parameters!=null && parameters.size()==sqlSplit.length-1) {
        StringBuilder logSql = new StringBuilder();
        logSql.append(sqlSplit[0]);
        int splitIndex = 1;
        for (Parameter parameter: parameters) {
          String parameterLogText = parameter.getLogValue();
          logSql.append(parameterLogText);
          logSql.append(sqlSplit[splitIndex++]);
        }
      } else {
        throw new RuntimeException("Parameter mismatch: \n"+jdbcSql+" \n"+ parameters);
      }
    }
    return jdbcSql;
  }

  public void apply(PreparedStatement statement) {
    if (parameters!=null) {
      for (int i=0; i<parameters.size(); i++) {
        Parameter parameter = parameters.get(i);
        DataType type = parameter.getType();
        Object value = parameter.getValue();
        type.setParameter(statement, i + 1, value);
        parameter.setLogValue(type.toLogText(value));
      }
    }
  }
}
