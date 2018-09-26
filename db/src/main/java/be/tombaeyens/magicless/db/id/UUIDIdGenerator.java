package be.tombaeyens.magicless.db.id;

import java.util.UUID;

public class UUIDIdGenerator implements IdGenerator {

  @Override
  public String generateId(String prefix) {
    return UUID.randomUUID().toString();
  }
}
