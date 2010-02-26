package org.yajul.jdbc

/**
 * Result set column metadata
 * <br>
 * User: Josh
 * Date: Feb 21, 2010
 * Time: 10:52:55 AM
 */
class ResultSetColumn {
  String name
  String typeName
  int jdbcType

  def String toString()
  {
    return "ResultSetColumn{" +
            "name='" + name + '\'' +
            ", typeName='" + typeName + '\'' +
            ", jdbcType=" + jdbcType +
            "}"
  }

}
