package test

import liquibase.database.DatabaseFactory
import org.yajul.jdbc.ConnectionInfo
import groovy.sql.Sql
import liquibase.database.Database
import liquibase.Liquibase
import liquibase.FileOpener
import liquibase.ClassLoaderFileOpener

/**
 * DbSchema test.
 * <br>
 * User: Josh
 * Date: Feb 27, 2010
 * Time: 2:51:38 PM
 */
class DbSchemaTest extends GroovyTestCase {
  def helper = new TestHelper()

  protected void setUp()
  {
    super.setUp()
    helper.cleardb()
  }

  void testDbSchema() {
    // Create the schema with Liquibase
    ConnectionInfo info = helper.getConnectionInfo("schematest")
    Sql sql = info.connect();
    Database db = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(sql.connection)
    FileOpener opener = new ClassLoaderFileOpener()
    Liquibase lb = new Liquibase("test/DbSchemaTest.xml",opener,db)
    println "Dropping all tables..."
    lb.dropAll()
    println "Validating change sets..."
    lb.validate()
  }
}
