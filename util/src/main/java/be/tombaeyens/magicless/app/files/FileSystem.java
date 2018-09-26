package be.tombaeyens.magicless.app.files;

import java.io.Reader;
import java.io.Writer;

public interface FileSystem {

  Writer getWriter(String location);

  Reader getReader(String location);
}
