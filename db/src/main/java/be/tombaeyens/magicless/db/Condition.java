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

import be.tombaeyens.magicless.db.conditions.AndCondition;
import be.tombaeyens.magicless.db.conditions.EqualCondition;
import be.tombaeyens.magicless.db.conditions.IsNullCondition;
import be.tombaeyens.magicless.db.conditions.LikeCondition;
import be.tombaeyens.magicless.db.impl.Parameters;

public interface Condition {

  public static Condition equal(Column column, Object value) {
    return new EqualCondition(column, value);
  }

  public static Condition isNull(Column column) {
    return new IsNullCondition(column);
  }

  public static Condition and(Condition... andConditions) {
    return new AndCondition(andConditions);
  }

  public static Condition like(Column column, String pattern) {
    return new LikeCondition(column, pattern);
  }

  String buildSql(Statement statement);

  void collectParameters(Parameters parameters);
}
