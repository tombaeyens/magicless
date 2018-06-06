package ai.shape;

import ai.shape.datasets.DatasetsTable;
import be.tombaeyens.magicless.app.util.Configuration;
import be.tombaeyens.magicless.db.Tx;
import be.tombaeyens.magicless.db.schema.SchemaManager;
import be.tombaeyens.magicless.db.schema.SchemaUpdate;

public class ShapeSchemaManager extends SchemaManager {

  public ShapeSchemaManager(Configuration configuration) {
    super(new CreateTables());
  }

  public static class CreateTables implements SchemaUpdate {
    @Override
    public String getId() {
      return "create-tables";
    }
    @Override
    public void update(Tx tx) {
      tx.newCreateTable(DatasetsTable.TABLE).execute();
    }
  }
}
