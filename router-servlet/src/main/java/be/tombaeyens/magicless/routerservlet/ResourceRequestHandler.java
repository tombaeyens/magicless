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
package be.tombaeyens.magicless.routerservlet;

import be.tombaeyens.magicless.app.util.Io;
import be.tombaeyens.magicless.app.util.Sets;

import java.util.Map;
import java.util.Set;

import static be.tombaeyens.magicless.app.util.Maps.entry;
import static be.tombaeyens.magicless.app.util.Maps.hashMap;


public class ResourceRequestHandler implements RequestHandler {

  private static final String REQUEST_CONTEXT_KEY_RESOURCE_PATH = "resourcePath";

  String basePath;
  Map<String, String> contentTypesByExtension;
  Set<String> indexFileNames;

  public ResourceRequestHandler(String basePath) {
    this(basePath, createDefaultExtensions(), createDefaultIndexFileNames());
  }

  public static Set<String> createDefaultIndexFileNames() {
    return Sets.hashSet(
      "index.html"
    );
  }

  public ResourceRequestHandler(String basePath, Map<String, String> contentTypesByExtension, Set<String> indexFileNames) {
    this.basePath = basePath;
    this.contentTypesByExtension = contentTypesByExtension;
    this.indexFileNames = indexFileNames;
  }

  public static Map<String, String> createDefaultExtensions() {
    return hashMap(
      entry("html", "text/html")
    );
  }

  @Override
  public boolean matches(ServerRequest request) {
    String resourcePath = this.basePath+request.getPathInfo();
    if (Io.hasResource(resourcePath)) {
      request.setContextObject(REQUEST_CONTEXT_KEY_RESOURCE_PATH, resourcePath);
      return true;
    }
    String optionalSeparator = resourcePath.endsWith("/") ? "" : "/";
    for (String indexFileName: indexFileNames) {
      String indexResourceName = resourcePath + optionalSeparator + indexFileName;
      if (Io.hasResource(indexResourceName)) {
        request.setContextObject("resourcePath", indexResourceName);
        return true;
      }
    }
    return false;
  }

  @Override
  public void handle(ServerRequest request, ServerResponse response) {
    String resourcePath = request.getContextObject(REQUEST_CONTEXT_KEY_RESOURCE_PATH);
    response.headerContentType(getContentType(resourcePath));
    byte[] resourceBytes = Io.getResourceAsBytes(resourcePath);
    response.bodyBytes(resourceBytes);
  }

  private String getContentType(String resourcePath) {
    int lastDotIndex = resourcePath.lastIndexOf('.');
    if (lastDotIndex!=-1 && lastDotIndex<resourcePath.length()-2) {
      String extension = resourcePath.substring(lastDotIndex+1);
      return contentTypesByExtension.get(extension);
    }
    return null;
  }
}