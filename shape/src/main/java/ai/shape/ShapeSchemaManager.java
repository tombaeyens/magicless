package ai.shape;

import ai.shape.datasets.DatasetsTable;
import be.tombaeyens.magicless.app.util.Configuration;
import be.tombaeyens.magicless.db.schema.SchemaManager;

public class ShapeSchemaManager extends SchemaManager {

  public ShapeSchemaManager(Configuration configuration) {
    super(tx->{
      tx.newCreateTable(DatasetsTable.TABLE).execute();
    });
  }
}
