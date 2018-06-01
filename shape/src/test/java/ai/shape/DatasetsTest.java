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
package ai.shape;

import ai.shape.datasets.*;
import ai.shape.domain.datasets.*;
import com.google.gson.reflect.TypeToken;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class DatasetsTest extends ShapeTest {

  @Test
  public void testDatasetCrud() {
    Dataset dataset = newPost("/command")
      .bodyJson(new CreateDataset()
        .name("Accounts"))
      .execute()
      .assertStatusOk()
      .getBodyAs(Dataset.class);

    String datasetId = dataset.getId();
    assertThat(datasetId, notNullValue());
    assertThat(dataset.getName(), is("Accounts"));

    List<Dataset> datasets = newPost("/query")
      .bodyJson(new GetDatasets())
      .execute()
      .assertStatusOk()
      .getBodyAs(new TypeToken<List<Dataset>>(){}.getType());

    dataset = datasets.get(0);
    assertThat(datasetId, notNullValue());
    assertThat(dataset.getName(), is("Accounts"));
    assertThat(datasets.size(), is(1));

    assertThat(newPost("/query")
      .bodyJson(new GetDataset())
      .execute()
      .assertStatusBadRequest()
      .getBody(), containsString("id is not specified"));

    dataset = newPost("/query")
      .bodyJson(new GetDataset(datasetId))
      .execute()
      .assertStatusOk()
      .getBodyAs(Dataset.class);
    assertThat(dataset.getId(), is(datasetId));
    assertThat(dataset.getName(), is("Accounts"));

    dataset = newPost("/command")
      .bodyJson(new UpdateDataset(datasetId)
        .name("Accounts"))
      .execute()
      .assertStatusOk()
      .getBodyAs(Dataset.class);
  }
}
