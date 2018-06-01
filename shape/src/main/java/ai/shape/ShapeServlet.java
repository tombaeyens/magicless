package ai.shape;

import be.tombaeyens.magicless.app.container.Initializable;
import be.tombaeyens.magicless.app.util.Configuration;
import be.tombaeyens.magicless.routerservlet.RequestHandler;
import be.tombaeyens.magicless.routerservlet.ResourceRequestHandler;
import be.tombaeyens.magicless.routerservlet.RouterServlet;

public class ShapeServlet extends RouterServlet implements Initializable<Shape> {

  public ShapeServlet(Configuration configuration) {
  }

  @Override
  public void initialize(Shape shape) {
    for (RequestHandler requestHandler: shape.getAll(RequestHandler.class)) {
      requestHandler(requestHandler);
    }
  }
}
