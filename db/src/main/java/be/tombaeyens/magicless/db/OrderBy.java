package be.tombaeyens.magicless.db;

import java.util.ArrayList;
import java.util.List;

public class OrderBy {

  List<FieldDirection> fieldDirections = new ArrayList<>();

  public static abstract class FieldDirection {
    SelectField selectField;
    public FieldDirection(SelectField selectField) {
      this.selectField = selectField;
    }

    public SelectField getSelectField() {
      return selectField;
    }

    public abstract boolean isAscending();
  }

  public static class Ascending extends FieldDirection {
    public Ascending(SelectField selectField) {
      super(selectField);
    }
    public boolean isAscending() {
      return true;
    }
  }

  public static class Descending extends FieldDirection {
    public Descending(SelectField selectField) {
      super(selectField);
    }
    public boolean isAscending() {
      return false;
    }
  }

  public void add(FieldDirection fieldDirection) {
    fieldDirections.add(fieldDirection);
  }

  public List<FieldDirection> getFieldDirections() {
    return fieldDirections;
  }

  public boolean isEmpty() {
    return fieldDirections.isEmpty();
  }
}
