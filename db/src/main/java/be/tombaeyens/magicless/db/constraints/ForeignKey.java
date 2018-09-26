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
package be.tombaeyens.magicless.db.constraints;

import be.tombaeyens.magicless.db.Column;
import be.tombaeyens.magicless.db.Constraint;

public class ForeignKey implements Constraint {

  Column from;
  Column to;

  public ForeignKey(Column from, Column to) {
    this.from = from;
    this.to = to;
  }

  @Override
  public String getDefaultCreateTableColumnConstraintSql() {
    return "REFERENCES "+to.getTable().getName()+"("+to.getName()+")";
  }

  public Column getFrom() {
    return from;
  }

  public Column getTo() {
    return to;
  }
}
