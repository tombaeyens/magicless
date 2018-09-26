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

public class SelectFrom {

  Table table;
  String alias;

  public SelectFrom(Table table, String alias) {
    this.table = table;
    this.alias = alias;
  }

  public Table getTable() {
    return table;
  }

  public String getAlias() {
    return alias;
  }

  public void setTable(Table table) {
    this.table = table;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }
}
