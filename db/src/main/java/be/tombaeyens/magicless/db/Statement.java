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

import be.tombaeyens.magicless.db.impl.SqlBuilder;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static be.tombaeyens.magicless.app.util.Exceptions.exceptionWithCause;
import static be.tombaeyens.magicless.db.Db.DB_LOGGER;

public class Statement {

  protected Tx tx;

  public Statement(Tx tx) {
    this.tx = tx;
  }

  protected PreparedStatement createPreparedStatement(String sqlText) {
    PreparedStatement statement = null;
    try {
      statement = tx
        .getConnection()
        .prepareStatement(sqlText);
    } catch (SQLException e) {
      throw exceptionWithCause("prepare "+getClass().getSimpleName().toUpperCase()+" statement: \n"+sqlText, e);
    }
    return statement;
  }

  protected int executeUpdate(SqlBuilder sql) {
    String sqlText = sql.getSql();
    PreparedStatement statement = createPreparedStatement(sqlText);
    sql.applyParameter(statement);
    try {
      tx.logSQL(sql.getLogText());
      int updateCount = statement.executeUpdate();
      DB_LOGGER.debug(tx + " " + getPastTense() + " " + updateCount + " rows");
      return updateCount;
    } catch (SQLException e) {
      throw exceptionWithCause("execute "+getClass().getSimpleName()+" \n"+sqlText+"\n-->", e);
    }
  }

  protected String getPastTense() {
    return getClass().getSimpleName()+"d";
  }
}
