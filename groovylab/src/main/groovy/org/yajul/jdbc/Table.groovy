package org.yajul.jdbc

/**
 * Table meta data
 * <br>
 * User: Josh
 * Date: Feb 21, 2010
 * Time: 11:06:43 AM
 */
class Table
{
  String name
  Map<String, Column> columns = [:]
  List<Column> primaryKeys = []

  def String toString()
  {
    return "Table{name='$name',columns=\n  ${columns.entrySet().join("\n  ")}}";
  }

  def List<Column> getSortedPrimaryKeys()
  {
    return primaryKeys.sort(Column.SORT_BY_ORD)  
  }

  def List<Column> getSortedColumns()
  {
    return columns.values().sort(Column.SORT_BY_ORD)
  }

  def List<String> getColumnNames()
  {
    return sortedColumns.collect({ Column c -> c.name});
  }

}
