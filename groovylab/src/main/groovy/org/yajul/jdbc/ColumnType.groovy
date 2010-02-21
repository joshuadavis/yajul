package org.yajul.jdbc

/**
 * Column Type Metadata
 * <br>
 * User: Josh
 * Date: Feb 21, 2010
 * Time: 11:19:08 AM
 */
class ColumnType
{
  String name
  int jdbcType
  def static TYPES_THAT_NEED_SIZE = [
          java.sql.Types.VARCHAR,
          java.sql.Types.CHAR,
          java.sql.Types.VARBINARY
  ] as Set;

  def boolean needsSize()
  {
    return TYPES_THAT_NEED_SIZE.contains(jdbcType)
  }

  def String toString()
  {
    return "ColumnType{" +
            "jdbcType=" + jdbcType +
            ", name='" + name + '\'' +
            '}';
  }
}
