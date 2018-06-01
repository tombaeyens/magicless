package ai.shape;

import ai.shape.datasets.CreateDataset;
import ai.shape.datasets.DatasetCreatedEvent;
import ai.shape.datasets.GetDataset;
import ai.shape.datasets.GetDatasets;
import be.tombaeyens.magicless.app.container.Container;
import be.tombaeyens.magicless.app.util.Configuration;
import be.tombaeyens.magicless.gson.PolymorphicTypeAdapterFactory;
import be.tombaeyens.magicless.httpserver.HttpServer;
import be.tombaeyens.magicless.routerservlet.ResourceRequestHandler;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


public class Shape extends Container {

  public static void main(String[] args) {
    System.out.println("Hello World!");
    Configuration configuration = new ShapeConfiguration();

    Shape exampleApplication = new Shape(configuration);
    exampleApplication.start();
    exampleApplication.get(HttpServer.class).join();
  }

  public Shape(Configuration configuration) {
    add(this);
    addShapeHttpServer(configuration);
    addShapeServlet(configuration);
    addRequestHandlers(configuration);
    addDb(configuration);
    addSchemaManager(configuration);
    addGson(configuration);

    initialize();
  }

  public static PolymorphicTypeAdapterFactory createQueryGsonTypeAdapterFactory() {
   return new PolymorphicTypeAdapterFactory()
     .typeName(new TypeToken<Query>() {},"query")
     .typeName(new TypeToken<GetDatasets>() {},"datasets")
     .typeName(new TypeToken<GetDataset>() {},"dataset")
     ;
  }

  public static PolymorphicTypeAdapterFactory createCommandGsonTypeAdapterFactory() {
    return  new PolymorphicTypeAdapterFactory()
      .typeName(new TypeToken<Command>() {}, "command")
      .typeName(new TypeToken<CreateDataset>() {}, "createDataset")
      .typeName(new TypeToken<CreateDataset>() {}, "updateDataset")
      ;
  }

  public static PolymorphicTypeAdapterFactory createEventGsonTypeAdapterFactory() {
    return new PolymorphicTypeAdapterFactory()
      .typeName(new TypeToken<Event>() {},"event")
      .typeName(new TypeToken<DatasetCreatedEvent>() {},"datasetCreated")
      ;
  }

  protected void addDb(Configuration configuration) {
    add(new ShapeDb(configuration));
  }

  protected void addSchemaManager(Configuration configuration) {
    add(new ShapeSchemaManager(configuration));
  }

  protected void addRequestHandlers(Configuration configuration) {
    // if all goes well, the container should preserve the ordering
    add(new QueryRequestHandler());
    add(new CommandRequestHandler());
    add(new ResourceRequestHandler("http"));
  }

  protected void addShapeServlet(Configuration configuration) {
    add(new ShapeServlet(configuration));
  }

  protected void addShapeHttpServer(Configuration configuration) {
    add(new ShapeHttpServer(configuration));
  }

  protected void addGson(Configuration configuration) {
    add(new GsonBuilder()
      .registerTypeAdapterFactory(createQueryGsonTypeAdapterFactory())
      .registerTypeAdapterFactory(createCommandGsonTypeAdapterFactory())
      .create());
  }
}
