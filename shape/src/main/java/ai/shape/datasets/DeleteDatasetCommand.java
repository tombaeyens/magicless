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

import ai.shape.Command;
import ai.shape.Shape;
import be.tombaeyens.magicless.db.Db;
import be.tombaeyens.magicless.routerservlet.BadRequestException;

import static be.tombaeyens.magicless.db.Condition.equal;

public class DeleteDatasetCommand implements Command {

  String id;

  public DeleteDatasetCommand() {
  }

  public DeleteDatasetCommand(String id) {
    this.id = id;
  }

  public static class DeleteDatasetResponse {
    int deletedRows;
    public DeleteDatasetResponse() {
    }
    public DeleteDatasetResponse(int deletedRows) {
      this.deletedRows = deletedRows;
    }
    public int getDeletedRows() {
      return deletedRows;
    }
  }

  @Override
  public Object execute(Shape shape) {
    BadRequestException.throwIf(id==null || "".equals(id), "id is not specified");
    Db db = shape.get(Db.class);
    return db.tx(tx->{
      int updateCount = tx.newDelete(DatasetsTable.TABLE)
        .where(equal(DatasetsTable.ID, id))
        .execute();
      tx.setResult(new DeleteDatasetResponse(updateCount));
      });
  }

}
