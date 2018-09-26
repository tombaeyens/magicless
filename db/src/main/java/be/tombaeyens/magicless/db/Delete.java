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

import be.tombaeyens.magicless.db.impl.Parameters;

import static be.tombaeyens.magicless.app.util.Exceptions.assertNotNullParameter;

public class Delete extends Statement {

  Table table;

  public Delete(Tx tx, Table table, String alias) {
    super(tx);
    assertNotNullParameter(table, "table");
    this.table = table;
    alias(table, alias);
  }

  public int execute() {
    String sql = getDialect().buildDeleteSql(this);

    return executeUpdate(sql);
  }

  @Override
  public Delete where(Condition whereCondition) {
    return (Delete) super.where(whereCondition);
  }

  public Table getTable() {
    return table;
  }
}
