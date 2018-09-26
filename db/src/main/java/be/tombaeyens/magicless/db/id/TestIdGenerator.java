package be.tombaeyens.magicless.db.id;

public class TestIdGenerator implements IdGenerator {

  long nextId = 1;

  @Override
  public String generateId(String prefix) {
    return prefix!=null ? prefix+nextId() : nextId();
  }

  private String nextId() {
    return Long.toString(nextId++);
  }
}
