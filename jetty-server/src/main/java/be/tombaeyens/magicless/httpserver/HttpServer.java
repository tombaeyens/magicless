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
package be.tombaeyens.magicless.httpserver;

import be.tombaeyens.magicless.app.container.Container;
import be.tombaeyens.magicless.app.container.Startable;
import be.tombaeyens.magicless.app.container.Stoppable;
import be.tombaeyens.magicless.app.util.Exceptions;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.FilterMapping;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.net.BindException;
import java.util.Arrays;
import java.util.Optional;

public class HttpServer implements Startable, Stoppable {

  static Logger log = LoggerFactory.getLogger(HttpServer.class);

  protected String name;
  protected Server server;
  protected ServletHandler servletHandler;

  public HttpServer(int port) {
    this.server = new Server(port);
    this.servletHandler = new ServletHandler();
    server.setHandler(servletHandler);
  }

  public HttpServer servlet(Class<? extends HttpServlet> servletClass) {
    return servlet(servletClass, "/*");
  }

  public HttpServer servlet(Class<? extends HttpServlet> servletClass, String path) {
    servletHandler.addServletWithMapping(servletClass, path);
    return this;
  }

  public HttpServer servlet(HttpServlet servlet) {
    return servlet(servlet, "/*");
  }

  public HttpServer servlet(HttpServlet servlet, String path) {
    ServletHolder servletHolder = new ServletHolder(servlet);
    servletHandler.addServletWithMapping(servletHolder, path);
    return this;
  }

  public <T extends HttpServlet> T getServlet(Class<T> servletClass) {
    Optional<ServletHolder> servletHolder = Arrays.stream(servletHandler.getServlets())
      .filter(sh -> servletClass.isAssignableFrom(sh.getServletInstance().getClass()))
      .findFirst();
    try {
      return servletHolder.isPresent() ? (T) servletHolder.get().getServlet() : null;
    } catch (ServletException e) {
      throw Exceptions.exceptionWithCause("retrieve servlet from servletHolder", e);
    }
  }

  public HttpServer filter(Filter filter) {
    return filter(filter, "/*");
  }

  public HttpServer filter(Filter filter, String path) {
    FilterMapping filterMapping = new FilterMapping();
    filterMapping.setFilterName(filter.getClass().getName());
    filterMapping.setPathSpec(path);
    FilterHolder filterHolder = new FilterHolder(filter);
    filterHolder.setName(filter.getClass().getName());
    servletHandler.addFilter(filterHolder, filterMapping);
    return this;
  }

  public HttpServer start() {
    try {
      server.start();
      log.debug((name!=null ? name : "Server") + " started on port "+getPort());
    } catch (Exception e) {
      if (isPortTakenException(e)) {
        // IDEA consider sending a shutdown command.  But only if you can do it safe so that it's impossible to shutdown production servers.
        throw new RuntimeException(
          "Port " +
          getPort() +
          " blocked.  You probably have a separate "+
          (name!=null?name+" ":"")+
          "Server running.  Please shut down that one and retry.");
      } else {
        throw new RuntimeException("Couldn't start server: " + e.getMessage(), e);
      }
    }
    return this;
  }

  /** from {@link be.tombaeyens.magicless.app.container.Startable} */
  @Override
  public void start(Container container) {
    start();
  }

  private static boolean isPortTakenException(Throwable t) {
    return "Address already in use".equals(t.getMessage())
           && (t instanceof BindException);
  }

  public HttpServer stop() {
    try {
      server.stop();
      server.join();
    } catch (Exception e) {
      throw new RuntimeException("Couldn't shutdown: " + e.getMessage(), e);
    }
    return this;
  }

  /** from {@link be.tombaeyens.magicless.app.container.Stoppable} */
  @Override
  public void stop(Container container) {
    stop();
  }

  /** Blocks the thread until the HttpServer stops.
   * This can be used when you're launching a server from the command line.
   * Crtl+C will then stop the server and stop the server. */
  public void join() {
    try {
      server.join();
    } catch (InterruptedException e) {
      throw new RuntimeException("Couldn't join: " + e.getMessage(), e);
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getPort() {
    return ((ServerConnector)server.getConnectors()[0]).getPort();
  }
}
