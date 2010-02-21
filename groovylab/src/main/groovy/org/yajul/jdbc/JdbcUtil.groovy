package org.yajul.jdbc

import java.sql.ResultSet
import java.sql.ResultSetMetaData

/**
 * JDBC Helper Methods
 * <br>
 * User: Josh
 * Date: Feb 21, 2010
 * Time: 10:51:34 AM
 */
class JdbcUtil
{
  def static iterate(ResultSet resultSet, Closure callme)
  {
    try
    {
      while (resultSet.next())
      {
        callme(resultSet)
      }
    }
    finally
    {
      resultSet.close()
    }
  }

  def static List<ResultSetColumn> getColumns(ResultSet resultSet)
  {
    def ResultSetMetaData md = resultSet.metaData
    def List<ResultSetColumn> columns = []
    def columnCount = md.getColumnCount()
    for(i in 1..columnCount)  // Stupid one-based index.
    {
      columns << new ResultSetColumn(
              name: md.getColumnName(i))
    }
    return columns;
  }
}
