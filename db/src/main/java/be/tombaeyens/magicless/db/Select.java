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

import be.tombaeyens.magicless.db.impl.Parameter;
import be.tombaeyens.magicless.db.impl.SelectBuilder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static be.tombaeyens.magicless.app.util.Exceptions.*;

public class Select {

  Tx tx;
  List<Selector> selectors;
  Map<Table,String> froms;
  Condition whereCondition;

  public Select(Tx tx, Selector... selectors) {
    this.tx = tx;
    this.selectors = Arrays.asList(selectors);
  }

  public SelectResults execute() throws RuntimeException {
    SelectBuilder selectBuilder = tx.getDb().getDialect().newSelectBuilder(this);

    assertNotEmptyCollection(selectors, "selectors is null. Please specify at least one non-null Selector in Tx.newSelect(...)");
    selectBuilder.appendSelect();
    selectors.forEach(selector->selector.appendTo(this, selectBuilder));

    assertNotEmptyMap(froms, "froms is null. Please specify at least one non-null tx.from(...)");
    selectBuilder.appendFrom();
    froms.keySet().forEach(table->{
      String alias = froms.get(table);
      selectBuilder.appendFrom(table, alias);
    });

    if (whereCondition!=null) {
      selectBuilder.appendWhere(this, whereCondition);
    }

    String sql = selectBuilder.getSql();
    PreparedStatement statement = null;
    try {
      statement = tx
        .getConnection()
        .prepareStatement(sql);
    } catch (SQLException e) {
      throw exceptionWithCause("prepare JDBC statement", e);
    }

    List<Parameter> parameters = selectBuilder.getParameters();
    if (parameters!=null) {
      for (int i=0; i<parameters.size(); i++) {
        parameters.get(i).apply(statement, i);
      }
    }

    try {
      ResultSet resultSet = statement.executeQuery();
      return new SelectResults(this, resultSet, sql);
    } catch (SQLException e) {
      throw exceptionWithCause("execute query", e);
    }
  }

  public String getAlias(Table table) {
    return null;
  }

  public String getColumnName(Column column) {
    String alias = froms.get(column.getTable());
    return alias!=null ? alias+"."+column.getName() : column.getName();
  }

  public Select from(Table table) {
    from(table, null);
    return this;
  }

  public Select from(Table table, String alias) {
    if (froms==null) {
      // Preserving the order is important, that's why
      // it's a *Linked*HashMap
      froms = new LinkedHashMap<>();
    }
    froms.put(table, alias);
    return this;
  }

  public Select where(Condition whereCondition) {
    this.whereCondition = whereCondition;
    return this;
  }

  public Integer getSelectorIndex(Column column) {
    for (int i=0; i<selectors.size(); i++) {
      Selector selector = selectors.get(i);
      if (selector==column) {
        return i;
      }
    }
    return null;
  }
}
