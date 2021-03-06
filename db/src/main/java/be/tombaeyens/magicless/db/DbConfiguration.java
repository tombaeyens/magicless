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

import be.tombaeyens.magicless.app.util.Configuration;
import be.tombaeyens.magicless.db.dialects.H2Dialect;

public class DbConfiguration {

  String driver;
  String url;
  String username;
  String password;
  Dialect dialect;
  String processRef;

  public DbConfiguration() {
  }

  public DbConfiguration(String prefix, Configuration configuration) {
    url(configuration.getStringRequired(prefix + ".url"));
    driver(configuration.getString(prefix + ".driver"));
    username(configuration.getString(prefix + ".username"));
    password(configuration.getString(prefix + ".password"));
    processRef(configuration.getString(prefix + ".processref"));
  }

  public DbConfiguration driver(String driver) {
    this.driver = driver;
    return this;
  }

  public DbConfiguration url(String url) {
    this.url = url;
    if (dialect==null) {
      String dialectText = getDialectTextFromUrl(url);
      if ("h2".equals(dialectText)) {
        this.dialect = H2Dialect.INSTANCE;
      }
    }
    return this;
  }

  private String getDialectTextFromUrl(String url) {
    // Calculate the dialect from the url
    if (url.startsWith("jdbc:") && url.length()>6) {
      int endIndex = url.indexOf(":", 5);
      if (endIndex!=-1) {
        return url.substring(5, endIndex);
      }
    }
    return null;
  }

  public DbConfiguration username(String username) {
    this.username = username;
    return this;
  }

  public DbConfiguration password(String password) {
    this.password = password;
    return this;
  }

  public DbConfiguration dbDialect(Dialect dialect) {
    this.dialect = dialect;
    return this;
  }

  public DbConfiguration processRef(String processRef) {
    this.processRef = processRef;
    return this;
  }

  public String getDriver() {
    return driver;
  }

  public String getUrl() {
    return url;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public Dialect getDialect() {
    return dialect;
  }

  public String getProcessRef() {
    return processRef;
  }
}
