package test

import org.yajul.jdbc.ConnectionInfo
import liquibase.Liquibase
import liquibase.ClassLoaderFileOpener
import liquibase.FileOpener
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

  static void loadSchema(Sql sql, String schemaFile,String contexts)
  {
    def factory = DatabaseFactory.getInstance()
    Database db = factory.findCorrectDatabaseImplementation(sql.connection)
    FileOpener opener = new ClassLoaderFileOpener()
    Liquibase lb = new Liquibase(schemaFile, opener, db)
    println "Dropping all tables..."
    lb.dropAll()
    println "Validating change sets..."
    lb.validate()
    println "Applying change sets..."
    lb.update(contexts)
    println "Finished."
  }
}
