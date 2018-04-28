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

import static java.lang.String.format;

public class InternalServerException extends HttpException {

  private static final long serialVersionUID = 1L;

  public InternalServerException() {
    super();
  }

  public static void throwIfNull(Object o, String message, Object... args) {
    if (o==null) {
      throw new InternalServerException(format(message, args));
    }
  }

  public InternalServerException(String message, Throwable cause) {
    super(message, cause);
  }

  public InternalServerException(String message) {
    super(message);
  }

  public InternalServerException(Throwable cause) {
    super(cause);
  }

  @Override
  public int getStatusCode() {
    return 500;
  }
}
