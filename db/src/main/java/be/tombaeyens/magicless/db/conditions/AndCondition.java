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

import be.tombaeyens.magicless.db.Condition;
import be.tombaeyens.magicless.db.Select;
import be.tombaeyens.magicless.db.impl.SelectBuilder;


public class AndCondition implements Condition {

  Condition[] andConditions;

  public AndCondition(Condition[] andConditions) {
    this.andConditions = andConditions;
  }

  @Override
  public void appendTo(Select select, SelectBuilder selectBuilder) {
    for (int i=0; i<andConditions.length; i++) {
      if (i>0) {
        selectBuilder.appendAnd();
      }
      andConditions[i].appendTo(select, selectBuilder);
    }
  }
}