package be.tombaeyens.magicless.db;

public class ColumnSelectField implements SelectField {

  Column column;
  String alias;

  public ColumnSelectField(Column column, String alias) {
    this.column = column;
    this.alias = alias;
  }

  @Override
  public String getName() {
    return column.getName();
  }

  @Override
  public String buildSelectFieldSql(Select select) {
    return alias+"."+column.getName();
  }

  @Override
  public DataType getType() {
    return column.getType();
  }
}
