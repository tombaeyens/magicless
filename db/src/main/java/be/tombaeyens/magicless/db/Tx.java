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

import be.tombaeyens.magicless.app.util.Io;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static be.tombaeyens.magicless.app.util.Exceptions.exceptionWithCause;
import static be.tombaeyens.magicless.app.util.Log.logLines;
import static be.tombaeyens.magicless.db.Db.DB_LOGGER;

public class Tx {

  private static long nextTxId = 1;
  private static ThreadLocal<Tx> currentTx = new ThreadLocal<>();

  protected long id = nextTxId++;
  protected Db db;
  protected Connection connection;
  protected boolean isRollbackOnly = false;
  protected Object result;
  protected Throwable rollbackReason;

  public Tx(Db db, Connection connection) {
    this.db = db;
    this.connection = connection;
    DB_LOGGER.debug(this+" starting");
    currentTx.set(this);
  }

  public static Tx getCurrentTx() {
    return currentTx.get();
  }

  public String toString() {
    return "Tx"+id;
  }

  public Db getDb() {
    return this.db;
  }

  public Connection getConnection() {
    return this.connection;
  }

  public Object getResult() {
    return this.result;
  }
  public void setResult(Object result) {
    this.result = result;
  }
  public Tx result(Object result) {
    this.result = result;
    return this;
  }

  public void setRollbackOnly() {
    setRollbackOnly(null);
  }
  
  public void setRollbackOnly(Throwable rollbackReason) {
    this.isRollbackOnly = true;
    this.rollbackReason = rollbackReason;
  }

  public boolean isRollbackOnly() {
    return isRollbackOnly;
  }

  protected void end() {
    currentTx.set(null);
    if (isRollbackOnly) {
      try {
        DB_LOGGER.warn(this+" rolling back" + (rollbackReason!=null ? " because: " + rollbackReason : ""));
        connection.rollback();
      } catch (SQLException e) {
        DB_LOGGER.error(this+" rollback failed: " + e.getMessage(), e);
      }
    } else {
      try {
        DB_LOGGER.debug(this+" committing");
        connection.commit();
      } catch (SQLException e) {
        DB_LOGGER.error(this+" commit failed: " + e.getMessage(), e);
      }
    }
  }

  public void logSQL(String sql) {
    if (sql!=null && DB_LOGGER.isDebugEnabled()) {
      logLines(DB_LOGGER, sql, this + " ");
    }
  }

  public void executeScriptResource(String resource) {
    String script = Io.getResourceAsString(resource);
    for (String ddl: script.split(";")) {
      try {
        int updates = connection.prepareStatement(ddl).executeUpdate();
        logLines(DB_LOGGER, ddl.trim());

      } catch (SQLException e) {
        throw exceptionWithCause("executing script statement "+ddl, e);
      }
    }
  }

  public List<String> getTableNames() {
    List<String> tableNames = new ArrayList<>();
    try {
      ResultSet tables = connection.getMetaData().getTables(null, null, "%", new String[]{"TABLE"});
      while (tables.next()) {
        String tableName = tables.getString(3);
        logSQL(" <- "+tableName);
        tableNames.add(tableName);
      }
      return tableNames;
    } catch (SQLException e) {
      throw exceptionWithCause("get table names", e);
    }
  }

  public CreateTable newCreateTable(Table table) {
    return new CreateTable(this, table);
  }

  public Select newSelect(SelectField... selectFields) {
    return new Select(this, selectFields);
  }

  public Update newUpdate(Table table) {
    return newUpdate(table, null);
  }

  public Update newUpdate(Table table, String alias) {
    return new Update(this, table, alias);
  }

  public Insert newInsert(Table table) {
    return new Insert(this, table);
  }

  public Select newSelectStarFrom(Table table) {
    Collection<Column> columnsCollection = table.getColumns().values();
    SelectField[] columnsArray = columnsCollection.toArray(new SelectField[table.getColumns().size()]);
    return new Select(this, columnsArray).from(table);
  }

  public Delete newDelete(Table table) {
    return newDelete(table, null);
  }

  public Delete newDelete(Table table, String alias) {
    return new Delete(this, table, alias);
  }
}
