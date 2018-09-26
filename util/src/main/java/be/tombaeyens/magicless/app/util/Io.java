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
package be.tombaeyens.magicless.app.util;

import be.tombaeyens.magicless.app.files.DefaultFileSystem;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.Scanner;

import static be.tombaeyens.magicless.app.util.Exceptions.assertTrue;
import static be.tombaeyens.magicless.app.util.Exceptions.exceptionWithCause;

public class Io {

  public static final String DEFAULT_CHARSET = "UTF-8";

  public static String getString(InputStream inputStream) {
    return getString(inputStream, DEFAULT_CHARSET);
  }

  public static String getString(InputStream inputStream, String charset) {
    if (inputStream==null) {
      return null;
    }
    Scanner scanner = new Scanner(inputStream, charset);
    scanner.useDelimiter("\\A");
    try {
      if (scanner.hasNext()) {
        return scanner.next();
      } else {
        return "";
      } 
    } finally {
      scanner.close();
    }
  }

  public static String getString(Reader reader) {
    if (reader==null) {
      return null;
    }
    char[] charBuffer = new char[8 * 1024];
    StringBuilder stringBuilder = new StringBuilder();
    int numCharsRead;
    try {
      while ((numCharsRead = reader.read(charBuffer, 0, charBuffer.length)) != -1) {
        stringBuilder.append(charBuffer, 0, numCharsRead);
      }
    } catch (IOException e) {
      throw new RuntimeException("Couldn't read reader to string: "+e.toString(), e);
    } finally {
      try {
        reader.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return stringBuilder.toString();
  }

  public static String getResourceAsString(String resource) {
    return getResourceAsString(resource, DEFAULT_CHARSET);
  }

  public static String getResourceAsString(String resource, String charset) {
    InputStream resourceStream = getResourceAsStream(resource);
    RuntimeException exception = null;
    try {
      return getString(resourceStream, charset);
    } catch (RuntimeException e) {
      exception = e;
      throw e;
    } finally {
      closeResourceStream(resourceStream, exception);
    }
  }

  private static void closeResourceStream(InputStream resourceStream, Exception cause) {
    try {
      if (resourceStream!=null) {
        resourceStream.close();
      }
    } catch (Exception e) {
      if (cause != null) {
        throw exceptionWithCause("close resource stream", cause);
      } else {
        throw exceptionWithCause("close resource stream", e);
      }
    }
  }

  public static boolean hasResource(String resource) {
    return Io.class.getClassLoader().getResource(resource)!=null;
  }

  public static byte[] getResourceAsBytes(String resource) {
    InputStream resourceStream = getResourceAsStream(resource);
    RuntimeException exception = null;
    try {
      return getBytes(resourceStream);
    } catch (RuntimeException e) {
      exception = e;
      throw e;
    } finally {
      closeResourceStream(resourceStream, exception);
    }
  }

  public static byte[] getBytes(InputStream inputStream) {
    if (inputStream==null) {
      return null;
    }
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    RuntimeException exception = null;
    try {
      int nRead;
      byte[] data = new byte[16384];
      while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
        buffer.write(data, 0, nRead);
      }
      buffer.flush();
      return buffer.toByteArray();
    } catch (IOException e) {
      exception = new RuntimeException("Couldn't read bytes from stream: "+e.getMessage(), e);
      throw exception;
    } finally {
      try {
        buffer.close();
      } catch (IOException e) {
        if (exception!=null) {
          throw exceptionWithCause("close buffer", exception);
        } else {
          throw exceptionWithCause("close buffer", e);
        }
      }
    }
  }

  public static InputStream getResourceAsStream(String resource) {
    return Io.class.getClassLoader().getResourceAsStream(resource);
  }

  public static void loadPropertiesFromResource(Properties properties, String resource) {
    if (properties!=null && resource!=null) {
      InputStream resourceStream = Io.getResourceAsStream(resource);
      if (resourceStream!=null) {
        Exception exception = null;
        try {
          properties.load(resourceStream);
        } catch (Exception e) {
          exception = e;
          throw exceptionWithCause("read properties from resource", e);
        } finally {
          closeResourceStream(resourceStream, exception);
        }
      }
    }
  }

  public static String readFileAsString(String fileName) {
    return readFileAsString(fileName, DEFAULT_CHARSET);
  }

  public static String readFileAsString(String fileName, String charset) {
    try {
      FileInputStream inputStream = new FileInputStream(fileName);
      byte[] bytes = getBytes(inputStream);
      return new String(bytes, Charset.forName(charset!=null ? charset : DEFAULT_CHARSET));
    } catch (Exception e) {
      throw exceptionWithCause("read file "+fileName, e);
    }
  }

  public static File createTempFile(String prefix, String suffix) {
    return createTempFile(prefix, suffix, null);
  }

  public static File createTempFile(String prefix, String suffix, File directory) {
    try {
      return File.createTempFile(prefix, suffix, directory);
    } catch (IOException e) {
      throw exceptionWithCause("create temp file "+prefix+"..."+suffix, e);
    }
  }

  public static Writer createFileWriter(File file) {
    try {
      return new FileWriter(file);
    } catch (IOException e) {
      throw exceptionWithCause("create file writer for "+file, e);
    }
  }

  public static String getCanonicalPath(File file) {
    try {
      return file!=null ? file.getCanonicalPath() : null;
    } catch (IOException e) {
      throw exceptionWithCause("get canonical path for "+file, e);
    }
  }

  public interface CheckedFunction<T> {
    void apply(T t) throws Exception;
  }

  public static class WriterCloser {
    String description;
    Writer writer;
    public WriterCloser(Writer writer) {
      this.writer = writer;
    }
    public WriterCloser description(String description) {
      this.description = description;
      return this;
    }
    public void execute(CheckedFunction<Writer> function) {
      Exception exception = null;
      try {
        function.apply(writer);
        writer.flush();
      } catch (Exception e) {
        throw exceptionWithCause(description, e);
      } finally {
        try {
          writer.close();
        } catch (IOException e) {
          if (exception!=null) {
            throw exceptionWithCause(description, exception);
          } else {
            throw exceptionWithCause(description, e);
          }
        }
      }
    }
  }

  public static WriterCloser flushAndClose(Writer writer) {
    return new WriterCloser(writer);
  }
}
