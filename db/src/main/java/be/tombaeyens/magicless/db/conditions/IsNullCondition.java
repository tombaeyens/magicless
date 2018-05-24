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
package be.tombaeyens.magicless.db.conditions;

import be.tombaeyens.magicless.db.AliasableStatement;
import be.tombaeyens.magicless.db.Column;
import be.tombaeyens.magicless.db.Condition;
import be.tombaeyens.magicless.db.impl.SqlBuilder;


public class IsNullCondition implements Condition {

  Column column;

  public IsNullCondition(Column column) {
    this.column = column;
  }

  @Override
  public void appendTo(AliasableStatement aliasableStatement, SqlBuilder sql) {
    String qualifiedColumnName = aliasableStatement.getQualifiedColumnName(column);
    sql.append(qualifiedColumnName);
    sql.append(" IS NULL");
  }
}
