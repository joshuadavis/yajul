package org.yajul.jdbc

import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.PreparedStatement
import java.util.logging.Logger
import java.util.logging.Level

/**
 * JDBC Helper Methods
 * <br>
 * User: Josh
 * Date: Feb 21, 2010
 * Time: 10:51:34 AM
 */
class JdbcUtil
{
  protected static Logger log = Logger.getLogger(JdbcUtil.class.getName());

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
      close(resultSet)
    }
  }

  def static List<ResultSetColumn> getColumns(ResultSet resultSet)
  {
    ResultSetMetaData md = resultSet.metaData
    List<ResultSetColumn> columns = []
    int columnCount = md.getColumnCount()
    for(i in 1..columnCount)  // Stupid one-based index.
    {
      columns << new ResultSetColumn(
              name: md.getColumnName(i))
    }
    return columns;
  }

  def static setParameters(PreparedStatement ps, List<Object> values)
  {
    values.eachWithIndex { Object o, int i -> ps.setObject(i+1,o)}
  }

  def static close(PreparedStatement ps)
  {
    try
    {
      if (ps != null)
        ps.close()
    }
    catch (Exception e)
    {
      log.log(Level.SEVERE,"Unexpected: ${e.message}",e)
    }
  }

  def static close(ResultSet rs)
  {
    try
    {
      if (rs != null)
        rs.close()
    }
    catch (Exception e)
    {
      log.log(Level.SEVERE,"Unexpected: ${e.message}",e)
    }
  }
}
