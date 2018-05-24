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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static be.tombaeyens.magicless.app.util.Exceptions.*;

public class Select extends AliasableStatement {

  List<SelectField> fields;
  List<SelectFrom> froms;
  Condition whereCondition;

  public Select(Tx tx, SelectField... fields) {
    super(tx);
    this.fields = Arrays.asList(fields);
  }

  public SelectResults execute() {
    Dialect dialect = tx.getDb().getDialect();
    SqlBuilder sql = dialect.newSqlBuilder();

    assertNotEmptyCollection(fields, "fields is empty. Specify at least one non-null Column or Function in Tx.newSelect(...)");
    sql.append("SELECT ");
    for (int i = 0; i<fields.size(); i++) {
      if (i>0) {
        sql.append(", ");
      }
      SelectField selectField = fields.get(i);
      selectField.appendTo(this,sql);
    }
    sql.append(" \n");

    // If columns were specified in the select from one table,
    // and of no .from(...) is specified, then the next section
    // calculates the from based on the first column.
    if (froms==null && fields.size()>0) {
      Optional<Table> tableOptional = fields.stream()
        .filter(field -> field instanceof Column)
        .map(field -> ((Column) field).getTable())
        .findFirst();
      if (tableOptional.isPresent()) {
        from(tableOptional.get());
      }
    }

    assertNotEmptyCollection(froms, "froms is empty. Specify at least one non-null select.from(...)");
    sql.append("FROM ");
    for (int i = 0; i<froms.size(); i++) {
      if (i>0) {
        sql.append(", \n     ");
      }
      SelectFrom from = froms.get(i);
      Table table = from.getTable();
      String alias = aliases.get(table);
      String fromSql = alias!=null ? table.getName()+" AS "+alias : table.getName();
      sql.append(fromSql);
    }
    sql.append("\n");

    if (whereCondition!=null) {
      sql.append("WHERE ");
      whereCondition.appendTo(this, sql);
    }

    sql.append(";");

    String sqlText = sql.getSql();
    PreparedStatement statement = createPreparedStatement(sqlText);

    sql.applyParameter(statement);

    try {
      tx.logSQL(sql.getLogText());
      ResultSet resultSet = statement.executeQuery();
      return new SelectResults(this, resultSet, sqlText);
    } catch (SQLException e) {
      throw exceptionWithCause("execute query \n"+sqlText+"\n-->", e);
    }
  }
  public Select from(Table table) {
    from(table, null);
    return this;
  }

  public Select from(Table table, String alias) {
    assertNotNullParameter(table, "table");
    if (froms==null) {
      froms = new ArrayList<>();
    }
    froms.add(new SelectFrom(table, alias));
    alias(table, alias);
    return this;
  }

  public Select where(Condition whereCondition) {
    this.whereCondition = whereCondition;
    return this;
  }

  /** Returns JDBC (meaning starts at 1) index of the column. */
  public Integer getSelectorJdbcIndex(Column column) {
    for (int i = 0; i<fields.size(); i++) {
      SelectField selectField = fields.get(i);
      if (selectField==column) {
        return i+1;
      }
    }
    return null;
  }

  public Tx getTx() {
    return tx;
  }

  public List<SelectField> getFields() {
    return fields;
  }
}
