package ai.shape;

import be.tombaeyens.magicless.app.container.Initializable;
import be.tombaeyens.magicless.app.util.Configuration;
import be.tombaeyens.magicless.httpserver.HttpServer;

import static ai.shape.ShapeConfiguration.SERVER_PORT;


public class ShapeHttpServer extends HttpServer implements Initializable<Shape> {

  public ShapeHttpServer(Configuration configuration) {
    super(configuration.getInteger(SERVER_PORT));
  }

  @Override
  public void initialize(Shape shape) {
    servlet(shape.get(ShapeServlet.class));
  }
}
