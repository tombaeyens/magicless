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

import be.tombaeyens.magicless.db.*;

import java.util.ArrayList;
import java.util.List;

public class SelectBuilder {

  Select select;
  StringBuilder sql = new StringBuilder();
  boolean hasSelectors = false; // set to true after the 1st selector has been appended
  boolean hasFroms = false;     // set to true after the 1st from has been appended
  List<Parameter> parameters;

  public SelectBuilder(Select select) {
    this.select = select;
  }

  public SelectBuilder appendSelect() {
    this.sql.append(getSelectSql());
    this.sql.append(" ");
    return this;
  }

  protected String getSelectSql() {
    return "SELECT";
  }

  public void appendSelector(Selector selector) {
    selector.appendTo(select,this);
  }

  /** @param selectorSql is fully qualified column name or (later possibly) a function */
  public void appendSelectorSql(String selectorSql) {
    if (hasSelectors) {
      sql.append(", ");
    } else {
      hasSelectors = true;
    }
    sql.append(selectorSql);
  }

  public SelectBuilder appendFrom() {
    this.sql.append("\n");
    this.sql.append(getFromSql());
    this.sql.append(" ");
    return this;
  }

  public String getFromSql() {
    return "FROM";
  }

  public void appendFrom(Table table, String alias) {
    if (hasFroms) {
      sql.append(", ");
    } else {
      hasFroms = true;
    }
    sql.append(getFromSql(table, alias));
  }

  public String getFromSql(Table table, String alias) {
    return alias!=null ? table.getName()+" AS "+alias : table.getName();
  }

  public SelectBuilder appendWhere(Select select, Condition whereCondition) {
    this.sql.append("\n");
    this.sql.append(getWhere());
    this.sql.append(" ");

    whereCondition.appendTo(select, this);

    return this;
  }

  public String getWhere() {
    return "WHERE";
  }

  public String getSql() {
    return sql.toString();
  }

  public List<Parameter> getParameters() {
    return parameters;
  }

  public void appendConditionExpressionValueEqual(String columnName, Object value, DataType type) {
    sql.append(columnName);
    sql.append(" = ?");
    addParameter(value, type);
  }

  public void appendConditionIsNull(String columnName, DataType type) {
    sql.append(columnName);
    sql.append(" IS NULL");
  }

  private void addParameter(Object value, DataType type) {
    if (parameters==null) {
      parameters = new ArrayList<>();
    }
    parameters.add(new Parameter(value, type));
  }

  public void appendAnd() {
    sql.append(" \n   ");
  }
}
