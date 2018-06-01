package ai.shape;

import be.tombaeyens.magicless.app.util.Configuration;
import be.tombaeyens.magicless.db.Db;
import be.tombaeyens.magicless.db.DbConfiguration;

public class ShapeDb extends Db {

  public ShapeDb(Configuration configuration) {
    super(new DbConfiguration("shape.db", configuration));
  }
}
