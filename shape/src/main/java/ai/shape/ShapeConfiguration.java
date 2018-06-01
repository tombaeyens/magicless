package ai.shape;

import be.tombaeyens.magicless.app.util.Configuration;

public class ShapeConfiguration extends Configuration {

  public static final String SERVER_PORT = "server.port";
  public static final String DB_PREFIX = "shape.db";

  public ShapeConfiguration() {
    put(SERVER_PORT, "5000");
    put(DB_PREFIX+".url", "jdbc:h2:....:shape");
    loadConfigurationFromSystemProperties();
  }
}
