package test

import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import liquibase.resource.ResourceAccessor
import org.yajul.jdbc.ConnectionInfo
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.Database
import groovy.sql.Sql

/**
 * Helper methods for tests
 * <br>
 * User: Josh
 * Date: Feb 28, 2010
 * Time: 9:08:49 AM
 */
class UnitTestHelper
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

  static void loadSchema(Sql sql, String schemaFile,String contexts)
  {
    def factory = DatabaseFactory.getInstance()
    Database db = factory.findCorrectDatabaseImplementation(new JdbcConnection(sql.connection))
      ResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor();
    Liquibase lb = new Liquibase(schemaFile, resourceAccessor, db)
    println "Dropping all tables..."
    lb.dropAll()
    println "Validating change sets..."
    lb.validate()
    println "Applying change sets..."
    lb.update(contexts)
    println "Finished."
  }
}
