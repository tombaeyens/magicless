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

package be.tombaeyens.util;

import java.util.Collection;

/**
 * Convenience methods for throwing runtime exceptions.
 */
public class Exceptions {

  /** throws a RuntimeException with message String.format(message,messageArgs) if o is null. */
  public static void assertNotNull(Object o, String message, String... messageArgs) {
    if (o==null) {
      throw new RuntimeException(String.format(message, messageArgs));
    }
  }

  /** throws a RuntimeException with message "Parameter "+parameterName+" is null" if o is null. */
  public static void assertNotNullParameter(Object o, String parameterName) {
    if (o==null) {
      throw new RuntimeException("Parameter "+parameterName+" is null");
    }
  }

  /** usage: throw newRuntimeException("describe what was being done", e);
   * returns a new RuntimeException with message "Couldn't "+whatWasBeingDone+": "+ exception.getMessage().
   * If exception is null, the exception message is not added. */
  public static RuntimeException newRuntimeException(String whatWasBeingDone, Throwable exception) {
    return new RuntimeException("Couldn't "+whatWasBeingDone+(exception!=null ? ": "+ exception.getMessage() : ""), exception);
  }

  public static void assertNotEmptyCollection(Collection<?> collection, String name) {
    assertNotNullParameter(collection, name);
    if (collection.isEmpty()) {
      throw new RuntimeException("Collection "+name+" is empty");
    }
  }
}
