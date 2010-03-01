package test

import org.yajul.jdbc.ConnectionInfo

/**
 * Helper methods for tests
 * <br>
 * User: Josh
 * Date: Feb 28, 2010
 * Time: 9:08:49 AM
 */
class TestHelper
{
  def username = "sa"
  def password = ""
  def driverClassName = 'org.hsqldb.jdbcDriver'
  def driverProtocol = 'jdbc:hsqldb:file:data/'
  def driverOptions = ''
  def dbdir = new File("data")

  def cleardb()
  {
    if (dbdir.exists() && dbdir.isDirectory())
    {
      print "Deleting ${dbdir}..."
      dbdir.deleteDir()
      println "OK"
    }
  }

  ConnectionInfo getConnectionInfo(dbname)
  {
    return new ConnectionInfo(
            url: "${driverProtocol}${dbname}${driverOptions}",
            username: username,
            password: password,
            driverClassName: driverClassName)
  }
}
