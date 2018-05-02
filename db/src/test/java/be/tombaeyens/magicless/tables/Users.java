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
package be.tombaeyens.magicless.tables;

import be.tombaeyens.magicless.db.Column;
import be.tombaeyens.magicless.db.Table;

public class Users extends Table {

  public static final Users TABLE = new Users();

  public static final Column ID = new Column().name("id").typeVarchar(1024).primaryKey();
  public static final Column FIRST_NAME = new Column().name("firstName").typeVarchar(1024);
  public static final Column LAST_NAME = new Column().name("lastName").typeVarchar(1024);
  public static final Column EMAIL = new Column().name("email").typeVarchar(1024);

  private Users() {
    name("users");
    column(ID);
    column(FIRST_NAME);
    column(LAST_NAME);
    column(EMAIL);
  }
}
