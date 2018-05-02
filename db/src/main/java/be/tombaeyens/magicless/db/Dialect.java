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

import be.tombaeyens.magicless.db.impl.SelectBuilder;

import java.util.List;
import java.util.stream.Collectors;

public class Dialect {

  public String getCreateTableSql(Table table) {
    return String.format(getCreateTableTemplate(),
      table.getName(),
      getCreateTableColumnDefinitions(table));
  }

  protected String getCreateTableTemplate() {
    return "CREATE TABLE %s (\n  %s);";
  }

  protected String getCreateTableColumnDefinitions(Table table) {
    return table.getColumns().values().stream()
      .map(column->getCreateTableColumnDefinition(column))
      .collect(Collectors.joining(",\n  "));
  }

  protected String getCreateTableColumnDefinition(Column column) {
    return column.getName()
           + " " + getDataTypeSql(column.getType())
           +getCreateTableColumnConstraits(column.getConstraints());
  }

  protected String getCreateTableColumnConstraits(List<Constraint> constraints) {
    if (constraints==null) {
      return "";
    }
    return constraints
      .stream()
      .map(constraint->getCreateTableColumnConstraint(constraint))
      .collect(Collectors.joining(" "));
  }

  protected String getCreateTableColumnConstraint(Constraint constraint) {
    return constraint.getDefaultSql();
  }

  public String getDataTypeSql(DataType dataType) {
    return dataType.getDefaultSql();
  }

  public SelectBuilder newSelectBuilder(Select select) {
    return new SelectBuilder(select);
  }
}
