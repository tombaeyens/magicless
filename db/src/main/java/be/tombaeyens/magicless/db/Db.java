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

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static be.tombaeyens.magicless.app.util.Exceptions.assertNotNull;
import static be.tombaeyens.magicless.app.util.Exceptions.exceptionWithCause;

public class Db {

  public static final Logger DB_LOGGER = LoggerFactory.getLogger(Db.class);

  protected DataSource dataSource;
  protected Dialect dialect;
  private List<String> tableNames;

  public Db(DbConfiguration dbConfiguration) {
    // assertNotNull(dbConfiguration.getDriver(), "Db driver is null");
    assertNotNull(dbConfiguration.getUrl(), "Db url is null");
    try {
      ComboPooledDataSource dataSource = new ComboPooledDataSource();
      this.dataSource = dataSource;
      dataSource.setDriverClass(dbConfiguration.getDriver()); //loads the jdbc driver
      dataSource.setJdbcUrl(dbConfiguration.getUrl());
      dataSource.setUser(dbConfiguration.getUsername());
      dataSource.setPassword(dbConfiguration.getPassword());
      dataSource.setAcquireRetryAttempts(1);
      dataSource.setMinPoolSize(1);
      // the settings below are optional -- c3p0 can work with defaults
      // dataSource.setMinPoolSize(5);
      // dataSource.setAcquireIncrement(5);
      // dataSource.setMaxPoolSize(20);

      dialect = dbConfiguration.getDialect();

    } catch (Exception e) {
      throw exceptionWithCause("create data source " + dbConfiguration.getUrl(), e);
    }
  }

  @SuppressWarnings("unchecked")
  public <T> T tx(TxLogic txLogic) {
    Connection connection = null;
    Tx tx = null;
    Exception exception = null;
    try {
      connection = dataSource.getConnection();
      connection.setAutoCommit(false);
      tx = new Tx(this, connection);
      txLogic.execute(tx);
    } catch (Exception e) {
      exception = e;
      tx.setRollbackOnly(e);
    }
    if (tx!=null) {
      tx.end();
    }
    if (connection!=null) {
      try {
        connection.close();
      } catch (SQLException e) {
        DB_LOGGER.error("Tx connection close: " + e.getMessage(), e);
      }
    }
    if (exception!=null) {
      if (exception instanceof RuntimeException) {
        throw (RuntimeException) exception;
      } else {
        throw new RuntimeException("Transaction failed: "+exception.getMessage(), exception);
      }
    }
    return tx!=null ? (T) tx.getResult() : null;
  }

  public DataSource getDataSource() {
    return dataSource;
  }

  public Dialect getDialect() {
    return dialect;
  }
}
