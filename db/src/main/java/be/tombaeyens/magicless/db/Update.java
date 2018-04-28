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

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static be.tombaeyens.magicless.app.util.Exceptions.exceptionWithCause;
import static be.tombaeyens.magicless.db.Db.DB_LOGGER;

public class Update {

  PreparedStatement preparedStatement;

  public Update(PreparedStatement preparedStatement) {
    this.preparedStatement = preparedStatement;
  }

  // TODO add methods for setting parameters

  public int execute() {
    try {
      int result = preparedStatement.executeUpdate();
      DB_LOGGER.debug("Update result: "+result);
      return result;
    } catch (SQLException e) {
      throw exceptionWithCause("execute db update", e);
    }
  }
}
