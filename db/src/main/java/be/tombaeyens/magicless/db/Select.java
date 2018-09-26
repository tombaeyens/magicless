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

import be.tombaeyens.magicless.app.util.Exceptions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static be.tombaeyens.magicless.app.util.Exceptions.assertNotNull;
import static be.tombaeyens.magicless.app.util.Exceptions.assertNotNullParameter;

public class Select extends Statement {

  List<SelectField> fields = new ArrayList<>();
  List<Table> froms = new ArrayList<>();
  OrderBy orderBy;

  public Select(Tx tx) {
    super(tx);
  }

  public SelectResults execute() {
    if (froms.size()>1)  {
      if (fields.stream().allMatch(field->field instanceof Column)) {
        Set<String> aliases = new HashSet<>();
        froms.forEach(from->{
          String alias = getAlias(from);
          if (alias==null) {
            alias = findNextAlias(aliases);
            alias(from, alias);
          }
          aliases.add(alias);
        });
      }
    }

    String sql = getDialect().buildSelectSql(this);

    return executeQuery(this, sql);
  }

  private String findNextAlias(Set<String> aliases) {
    int i = aliases.size()+1;
    while (aliases.contains("T"+i)) {
      i++;
    }
    return "T"+i;
  }

  public Select field(SelectField selectField) {
    fields.add(selectField);
    if (selectField instanceof Column) {
      from(((Column)selectField).getTable());
    }
    return this;
  }

  public Select fields(SelectField... fields) {
    if (fields!=null) {
      for (SelectField field: fields) {
        field(field);
      }
    }
    return this;
  }

  public Select fields(Table table) {
    return fields(table, null);
  }

  public Select fields(Table table, String alias) {
    if (table!=null && table.getColumns()!=null) {
      for (Column column: table.getColumns().values()) {
        field(column);
      }
    }
    return this;
  }

  public Select from(Table table) {
    from(table, null);
    return this;
  }

  public Select from(Table table, String alias) {
    assertNotNullParameter(table, "table");
    if (!fromContains(table, alias)) {
      froms.add(table);
      alias(table, alias);
    }
    return this;
  }

  private boolean fromContains(Table table, String alias) {
    for (Table from: froms) {
      String fromAlias = getAlias(from);
      if (table==from
          && ( (alias==null && fromAlias ==null)
               || (alias!=null && alias.equals(fromAlias)))
             ) {
        return true;
      }
    }
    return false;
  }

  @Override
  public Select where(Condition whereCondition) {
    return (Select) super.where(whereCondition);
  }



  /** Returns JDBC (meaning starts at 1) index of the results. */
  public Integer getSelectorJdbcIndex(Column column) {
    for (int i = 0; i<fields.size(); i++) {
      SelectField selectField = fields.get(i);
      if (selectField==column) {
        return i+1;
      }
    }
    return null;
  }

  public Select orderAsc(SelectField selectField) {
    addOrderBy(new OrderBy.Ascending(selectField));
    return this;
  }

  public Select orderDesc(SelectField selectField) {
    addOrderBy(new OrderBy.Descending(selectField));
    return this;
  }

  protected void addOrderBy(OrderBy.FieldDirection fieldDirection) {
    if (orderBy==null) {
      orderBy = new OrderBy();
    }
    orderBy.add(fieldDirection);
  }

  public boolean hasOrderBy() {
    return orderBy!=null && !orderBy.isEmpty();
  }

  public Tx getTx() {
    return tx;
  }

  public List<SelectField> getFields() {
    return fields;
  }

  public List<Table> getFroms() {
    return froms;
  }

  public OrderBy getOrderBy() {
    return orderBy;
  }

  public Select join(Table table) {
    Column foreignKeyColumn = findForeignKeyInFromsTo(table);
    assertNotNull(foreignKeyColumn, "No foreign key found to "+table+" in the froms of this select");
    Column primaryKey = table.getPrimaryKeyColumn();
    assertNotNull(foreignKeyColumn, "No primary key found in "+table);
    from(table);
    where(Condition.equal(foreignKeyColumn, primaryKey));
    return this;
  }

  private Column findForeignKeyInFromsTo(Table destination) {
    for (Table from: froms) {
      for (Column candidate: from.getColumns().values()) {
        if (candidate.isForeignKeyTo(destination)) {
          return candidate;
        }
      }
    }
    return null;
  }
}
