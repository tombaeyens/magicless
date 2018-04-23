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

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.FilterMapping;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;
import java.net.BindException;

public class HttpServer {

  static Logger log = LoggerFactory.getLogger(HttpServer.class);

  protected String name;
  protected int port;
  protected Server server;
  protected ServletHandler servletHandler;

  public HttpServer(String name, int port) {
    this.port = port;
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

  public HttpServer startup() {
    try {
      server.start();
      log.debug("Server started on port "+port);
    } catch (Exception e) {
      if (isPortTakenException(e)) {
        // IDEA consider sending a shutdown command.  But only if you can do it safe so that it's impossible to shutdown production servers.
        throw new RuntimeException("Port " +
                                   getPort() +
                                   " blocked.  You probably have a separate Server running.  Please shut down that one and retry.");
      } else {
        throw new RuntimeException("Couldn't start server: " + e.getMessage(), e);
      }
    }
    return this;
  }

  private static boolean isPortTakenException(Throwable t) {
    return "Address already in use".equals(t.getMessage())
           && (t instanceof BindException);
  }

  public void shutdown() {
    try {
      server.stop();
      server.join();
    } catch (Exception e) {
      throw new RuntimeException("Couldn't shutdown: " + e.getMessage(), e);
    }
  }

  public int getPort() {
    return port;
  }

  public void join() {
    try {
      server.join();
    } catch (InterruptedException e) {
      throw new RuntimeException("Couldn't join: " + e.getMessage(), e);
    }
  }
}
