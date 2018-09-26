package be.tombaeyens.magicless.app.files;

import be.tombaeyens.magicless.app.util.Exceptions;

import java.io.*;

import static be.tombaeyens.magicless.app.util.Exceptions.exceptionWithCause;

public class DefaultFileSystem implements FileSystem {

  private String basedir;

  public DefaultFileSystem() {
    this(null);
  }

  public DefaultFileSystem(String basedir) {
    if (basedir!=null) {
      if (!basedir.endsWith("/")) {
        basedir = basedir+"/";
      }
      this.basedir = basedir;
      Exceptions.assertTrue(new File(basedir).isDirectory(), "%s is not a directory", basedir);
    }
  }

  @Override
  public Writer getWriter(String fileName) {
    try {
      return new FileWriter(getPath(fileName));
    } catch (IOException e) {
      throw exceptionWithCause("create file writer for "+fileName, e);
    }
  }

  private String getPath(String fileName) {
    return basedir!=null ? basedir + fileName : fileName;
  }

  @Override
  public Reader getReader(String fileName) {
    try {
      return new FileReader(getPath(fileName));
    } catch (FileNotFoundException e) {
      throw exceptionWithCause("create file reader for "+fileName, e);
    }
  }

  public String getBasedir() {
    return basedir;
  }

  public void setBasedir(String basedir) {
    this.basedir = basedir;
  }
}
