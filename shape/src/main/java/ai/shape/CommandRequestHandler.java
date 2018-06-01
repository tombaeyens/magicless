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

package ai.shape;

import be.tombaeyens.magicless.app.container.Inject;
import be.tombaeyens.magicless.routerservlet.PathRequestHandler;
import be.tombaeyens.magicless.routerservlet.ServerRequest;
import be.tombaeyens.magicless.routerservlet.ServerResponse;
import com.google.gson.Gson;

public class CommandRequestHandler extends PathRequestHandler {

  @Inject
  Gson gson;

  @Inject
  Shape shape;

  public CommandRequestHandler() {
    super(POST, "/command");
  }

  @Override
  public void handle(ServerRequest request, ServerResponse response) {
    String queryJsonText = request.getBodyAsString();
    Command command = gson.fromJson(queryJsonText, Command.class);
    Object result = command.execute(shape);
    response.statusOk();
    response.bodyJsonString(gson.toJson(result));
  }
}
