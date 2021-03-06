package org.yajul.jdbc

/**
 * Column meta-data
 * <br>
 * User: Josh
 * Date: Feb 21, 2010
 * Time: 11:06:33 AM
 */
class Column {
  static SORT_BY_ORD = {Column a, Column b -> a.ord - b.ord }

  String name
  Table table
  ColumnType type
  int size
  int ord
  boolean nullable = true
  boolean primaryKey = false

  def String columnDefinition() {
    def s = "$name $type.name"

    if (type.needsSize())
      s += "($size)"

    if (!nullable)
      s += ' NOT'

    s += ' NULL'

    if (primaryKey)
      s += ' PRIMARY KEY'
    return s;
  }

  boolean equivalentTo(Column column) {
    equivalentTo(column,false)
  }

  boolean equivalentTo(Column column,boolean ignorePk) {
    if (this.is(column)) return true;
    if (nullable != column.nullable) return false;
    if (ord != column.ord) return false;
    if (name ? !name.equals(column.name) : column.name != null) return false;
    if (!ignorePk && primaryKey != column.primaryKey) return false;
    if (!type.equivalentTo(column.type)) return false;
    if (type.needsSize() && size != column.size) return false;
    return true;
  }

  def String toString() {
    return "Column{name='$name', ord=$ord, type=$type, size=$size, nullable=$nullable, primaryKey=$primaryKey}";
  }
}
