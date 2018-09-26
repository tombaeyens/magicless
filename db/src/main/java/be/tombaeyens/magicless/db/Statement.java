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

import be.tombaeyens.magicless.db.conditions.AndCondition;
import be.tombaeyens.magicless.db.impl.Parameters;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import static be.tombaeyens.magicless.app.util.Exceptions.exceptionWithCause;
import static be.tombaeyens.magicless.db.Db.DB_LOGGER;

public class Statement {

  protected Tx tx;
  Map<Table,String> aliases;
  Condition whereCondition;

  public Statement(Tx tx) {
    this.tx = tx;
  }

  protected PreparedStatement createPreparedStatement(String sql) {
    PreparedStatement statement = null;
    try {
      statement = tx
        .getConnection()
        .prepareStatement(sql);
    } catch (SQLException e) {
      throw exceptionWithCause("prepare "+getClass().getSimpleName().toUpperCase()+" statement: \n"+sql, e);
    }
    return statement;
  }

  protected int executeUpdate(String sql) {
    PreparedStatement statement = createPreparedStatement(sql);
    try {
      Parameters parameters = new Parameters();
      collectParameters(parameters);
      parameters.apply(statement);
      tx.logSQL(parameters.toLogSql(sql));
      int updateCount = statement.executeUpdate();
      DB_LOGGER.debug(tx + " " + getPastTense() + " " + updateCount + " rows");
      return updateCount;
    } catch (SQLException e) {
      throw exceptionWithCause("execute "+getClass().getSimpleName()+" \n"+sql+"\n-->", e);
    } finally {
      try {
        statement.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  protected SelectResults executeQuery(Select select, String sql) {
    PreparedStatement statement = createPreparedStatement(sql);
    try {
      Parameters parameters = new Parameters();
      collectParameters(parameters);
      parameters.apply(statement);
      tx.logSQL(parameters.toLogSql(sql));
      ResultSet resultSet = statement.executeQuery();
      return new SelectResults(select, resultSet, sql);
    } catch (SQLException e) {
      throw exceptionWithCause("execute query \n"+sql+"\n-->", e);
    }
  }

  /** override if the statement sets parameters and ensure that the
   * ordering of parameters corresponds to the ordering of the ?
   * that were generated in the sql */
  protected void collectParameters(Parameters parameters) {
    if (whereCondition!=null) {
      whereCondition.collectParameters(parameters);
    }
  }

  protected String getPastTense() {
    return getClass().getSimpleName()+"d";
  }

  public String getQualifiedColumnName(Column column) {
    String alias = aliases!=null ? aliases.get(column.getTable()) : null;
    return alias!=null ? alias+"."+column.getName() : column.getName();
  }

  protected Statement alias(Table table, String alias) {
    if (aliases==null) {
      aliases = new LinkedHashMap<>();
    }
    aliases.put(table, alias);
    return this;
  }

  protected String getAlias(Table table) {
    return aliases.get(table);
  }

  public Statement where(Condition whereCondition) {
    if (this.whereCondition!=null) {
      if (this.whereCondition instanceof AndCondition) {
        ((AndCondition)this.whereCondition).add(whereCondition);
      } else if (whereCondition instanceof AndCondition) {
        ((AndCondition)whereCondition).add(this.whereCondition);
        this.whereCondition = whereCondition;
      } else {
        this.whereCondition = new AndCondition(new Condition[]{this.whereCondition, whereCondition});
      }
    } else {
      this.whereCondition = whereCondition;
    }
    return this;
  }

  public Condition getWhereCondition() {
    return whereCondition;
  }

  public boolean hasWhereCondition() {
    return whereCondition!=null;
  }

  protected Dialect getDialect() {
    return tx.getDb().getDialect();
  }
}
