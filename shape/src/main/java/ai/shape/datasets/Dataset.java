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

import be.tombaeyens.magicless.db.SelectResults;

public class Dataset {

  String id;
  String name;

  /** constructor for JSON & DB deserializations */
  public Dataset() {}

  public Dataset(SelectResults selectResults) {
    id = selectResults.get(DatasetsTable.ID);
    name = selectResults.get(DatasetsTable.NAME);
  }

  public String getId() {
    return id;
  }

  public Dataset id(String id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return name;
  }

  public Dataset name(String name) {
    this.name = name;
    return this;
  }
}
