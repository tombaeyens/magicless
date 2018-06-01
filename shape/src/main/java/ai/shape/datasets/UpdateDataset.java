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

import ai.shape.Shape;
import ai.shape.Command;
import be.tombaeyens.magicless.db.Db;
import be.tombaeyens.magicless.routerservlet.BadRequestException;

import java.util.UUID;

public class UpdateDataset implements Command {

  String id;
  String name;

  public UpdateDataset() {
  }

  public UpdateDataset(String id) {
    this.id = id;
  }

  @Override
  public Object execute(Shape shape) {
    BadRequestException.throwIf(id==null || "".equals(id), "id is not specified");
    if (name!=null && !"".equals(name)) {
      Db db = shape.get(Db.class);
      return db.tx(tx->{
        String id = UUID.randomUUID().toString();
        int updateCount = tx.newUpdate(DatasetsTable.TABLE)
          .set(DatasetsTable.ID, id)
          .set(DatasetsTable.NAME, name)
          .execute();
        if (updateCount==1) {
          tx.setResult(new Dataset()
            .id(id)
            .name(name));
        }
      });
    }
    return null;
  }

  public UpdateDataset name(String name) {
    this.name = name;
    return this;
  }
}
