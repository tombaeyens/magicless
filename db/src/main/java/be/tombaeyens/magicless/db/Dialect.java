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

import java.util.List;
import java.util.Optional;

import static be.tombaeyens.magicless.app.util.Exceptions.assertNotEmptyCollection;
import static be.tombaeyens.magicless.app.util.Exceptions.assertNotNull;
import static java.util.stream.Collectors.joining;

public class Dialect {

  public String buildCreateTableSql(Table table) {
    return String.format("CREATE TABLE %s ( %s );",table.getName(), buildCreateTableColumnsSql(table));
  }

  protected String buildCreateTableColumnsSql(Table table) {
    return table.getColumns().values().stream()
      .map(column->buildCreateTableColumnSql(column))
      .collect(joining(", \n  "));
  }

  protected String buildCreateTableColumnSql(Column column) {
    assertNotNull(column, "Column %d is null", column.getIndex());
    DataType type = column.getType();
    assertNotNull(type, "Column %d has type null", column.getIndex());
    List<Constraint> constraints = column.getConstraints();
    return column.getName() + " " + buildTypeSql(type) + buildCreateTableColumnConstraintsSql(column);
  }

  protected Object buildCreateTableColumnConstraintsSql(Column column) {
    List<Constraint> constraints = column.getConstraints();
    if (constraints == null) {
      return "";
    }
    return " " + constraints.stream()
      .map(constraint->buildCreateTableColumnConstraintSql(constraint))
      .collect(joining(" "));
  }

  protected String buildCreateTableColumnConstraintSql(Constraint constraint) {
    return constraint.getDefaultCreateTableColumnConstraintSql();
  }

  protected String buildTypeSql(DataType dataType) {
    return dataType.getDefaultSql();
  }

  // SELECT

  public String buildSelectSql(Select select) {
    return "SELECT "+buildSelectFieldsSql(select)+" \n"+
           "FROM "+buildSelectFromsSql(select)+
           (select.hasWhereCondition() ? " \nWHERE "+ buildConditionSql(select.getWhereCondition(), select) : "")+
           (select.hasOrderBy() ? " \nORDER BY "+ buildOrderBySql(select.getOrderBy(), select) : "")+
           ";";
  }

  private String buildOrderBySql(OrderBy orderBy, Select select) {
    return orderBy.getFieldDirections().stream()
      .map(direction->direction.getSelectField().buildSelectFieldSql(select)+" "+(direction.isAscending() ? "ASC" : "DESC" ))
      .collect(joining(", "));
  }

  protected String buildConditionSql(Condition condition, Statement statement) {
    return condition.buildSql(statement);
  }

  protected String buildSelectFieldsSql(Select select) {
    List<SelectField> fields = select.getFields();
    assertNotEmptyCollection(fields, "fields is empty. Specify at least one non-null Column or Function in Tx.newSelect(...)");
    return fields.stream()
      .map(selectField->selectField.buildSelectFieldSql(select))
      .collect(joining(", "));
  }

  protected String buildSelectFromsSql(Select select) {
    // If columns were specified in the select from one table,
    // and of no .from(...) is specified, then the next section
    // calculates the from based on the first results.
    List<SelectField> fields = select.getFields();
    if (select.getFroms()==null && fields.size()>0) {
      Optional<Table> tableOptional = fields.stream()
        .filter(field -> field instanceof Column)
        .map(field -> ((Column) field).getTable())
        .findFirst();
      if (tableOptional.isPresent()) {
        select.from(tableOptional.get());
      }
    }

    List<Table> froms = select.getFroms();
    assertNotEmptyCollection(froms, "froms is empty. Specify at least one non-null select.from(...)");
    return froms.stream()
      .map(from-> buildTableWithAliasSql(select, from))
      .collect(joining(", \n     "));
  }

  protected String buildTableWithAliasSql(Statement statement, Table table) {
    String alias = statement.getAlias(table);
    if (alias!=null) {
      return table.getName()+" AS "+alias;
    } else {
      return table.getName();
    }
  }

  public String buildInsertSql(Insert insert) {
    Table table = insert.getTable();
    List<Insert.ColumnValue> columnValues = insert.getColumnValues();
    return "INSERT INTO "+table.getName()+" ("+
           columnValues.stream()
              .map(columnValue->columnValue.getColumn().getName())
              .collect(joining(", "))+
           ") \nVALUES ("+
           columnValues.stream()
              .map(columnValue->"?")
              .collect(joining(", "))+
          ");";
  }

  public String buildUpdateSql(Update update) {
    Table table = update.getTable();
    return
      "UPDATE "+buildTableWithAliasSql(update, table)+" \n"+
      "SET "+buildUpdateAssignmentsSql(update)+
      (update.hasWhereCondition() ? " \nWHERE "+ buildConditionSql(update.getWhereCondition(), update) : "")+
      ";";
  }

  protected String buildUpdateAssignmentsSql(Update update) {
    List<UpdateSet> sets = update.getSets();
    assertNotEmptyCollection(sets, "sets is empty. Specify at least one non-null update.set(...)");
    return sets.stream()
      .map(updateSet->buildUpdateAssignmentSql(update, updateSet))
      .collect(joining(", \n    "));
  }

  protected String buildUpdateAssignmentSql(Update update, UpdateSet updateSet) {
    return updateSet.toSql(update);
  }

  public String buildDeleteSql(Delete delete) {
    Table table = delete.getTable();
    return
      "DELETE FROM "+buildTableWithAliasSql(delete, table)+
      (delete.hasWhereCondition() ? " \nWHERE "+ buildConditionSql(delete.getWhereCondition(), delete) : "")+
      ";";
  }
}
