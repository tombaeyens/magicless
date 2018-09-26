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

import java.util.ArrayList;
import java.util.List;

import static be.tombaeyens.magicless.app.util.Exceptions.assertNotNullParameter;

public class Update extends Statement {

  Table table;
  List<UpdateSet> sets;

  public Update(Tx tx, Table table, String alias) {
    super(tx);
    assertNotNullParameter(table, "table");
    this.table = table;
    alias(table, alias);
  }

  public int execute() {
    String sql = getDialect().buildUpdateSql(this);

    return executeUpdate(sql);
  }

  @Override
  protected void collectParameters(Parameters parameters) {
    sets.stream().forEach(set->set.collectParameters(parameters));
    super.collectParameters(parameters);
  }

  public Update set(Column column, Object value) {
    if (sets==null) {
      sets = new ArrayList<>();
    }
    sets.add(new UpdateSet(column, value));
    return this;
  }

  @Override
  public Update where(Condition whereCondition) {
    return (Update) super.where(whereCondition);
  }

  public Table getTable() {
    return table;
  }

  public List<UpdateSet> getSets() {
    return sets;
  }
}
