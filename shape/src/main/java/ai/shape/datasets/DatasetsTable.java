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

package ai.shape.datasets;

import be.tombaeyens.magicless.db.Column;
import be.tombaeyens.magicless.db.Table;

public class DatasetsTable {

  public static final Column ID = new Column()
    .name("id")
    .typeVarchar(255)
    .primaryKey();

  public static final Column NAME = new Column()
    .name("name")
    .typeVarchar(255)
    .notNull();

  public static final Table TABLE = new Table()
    .name("datasets")
    .column(ID)
    .column(NAME);

}
