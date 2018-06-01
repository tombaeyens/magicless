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

import be.tombaeyens.magicless.db.constraints.NotNull;
import be.tombaeyens.magicless.db.constraints.PrimaryKey;
import be.tombaeyens.magicless.db.impl.SqlBuilder;
import be.tombaeyens.magicless.db.types.IntegerType;
import be.tombaeyens.magicless.db.types.TimestampType;
import be.tombaeyens.magicless.db.types.VarcharType;

import java.util.ArrayList;
import java.util.List;

public class Column implements SelectField {

  protected Table table;
  protected String name;
  protected DataType type;
  protected List<Constraint> constraints;

  public String getName() {
    return this.name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public Column name(String name) {
    this.name = name;
    return this;
  }

  public DataType getType() {
    return this.type;
  }
  public void setType(DataType type) {
    this.type = type;
  }
  public Column type(DataType type) {
    this.type = type;
    return this;
  }
  public Column typeVarchar(int n) {
    type(new VarcharType(n));
    return this;
  }
  public Column typeInteger() {
    type(new IntegerType());
    return this;
  }
  public Column typeTimestamp() {
    type(new TimestampType());
    return this;
  }


  public List<Constraint> getConstraints() {
    return this.constraints;
  }
  public Column constraint(Constraint constraint) {
    if (constraints==null) {
      constraints = new ArrayList<>();
    }
    constraints.add(constraint);
    return this;
  }

  public Column primaryKey() {
    constraint(new PrimaryKey());
    return this;
  }

  public Column notNull() {
    constraint(new NotNull());
    return this;
  }

  @Override
  public void appendTo(Select select, SqlBuilder sql) {
    String qualifiedColumnName = select.getQualifiedColumnName(this);
    sql.append(qualifiedColumnName);
  }

  public Table getTable() {
    return table;
  }
}
