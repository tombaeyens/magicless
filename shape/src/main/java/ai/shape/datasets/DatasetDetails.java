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

public class DatasetDetails extends Dataset {

  String datasourceId;
  String datasourceName; // resolved from datasource table

  public String getDatasourceId() {
    return datasourceId;
  }

  public Dataset datasourceId(String datasourceId) {
    this.datasourceId = datasourceId;
    return this;
  }

  public String getDatasourceName() {
    return datasourceName;
  }

  public Dataset datasourceName(String datasourceName) {
    this.datasourceName = datasourceName;
    return this;
  }
}
