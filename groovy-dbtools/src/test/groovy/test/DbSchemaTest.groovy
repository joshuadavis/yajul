package test

import groovy.sql.Sql
import org.yajul.jdbc.ConnectionInfo
import org.yajul.jdbc.DbSchema

/**
 * DbSchema test.
 * <br>
 * User: Josh
 * Date: Feb 27, 2010
 * Time: 2:51:38 PM
 */
class DbSchemaTest extends GroovyTestCase {
  def helper = new UnitTestHelper()

  protected void setUp()
  {
    super.setUp()
    helper.cleardb()
  }

  void testDbSchema()
  {
    // Create the schema with Liquibase
    ConnectionInfo info = helper.getConnectionInfo("schematest")
    Sql sql = info.connect();
    UnitTestHelper.loadSchema(sql, "test/DbSchemaTest.xml", null)

    DbSchema schema = new DbSchema(sql)

    assertTrue(schema.tableExists("PERSON"))
    assertTrue(schema.tableExists("NOTSAME1"))
    assertTrue(schema.tableExists("NOTSAME2"))

    def columns = schema.tables['PERSON'].sortedColumns
    assertEquals(3, columns.size())

    def a = schema.tables['NOTSAME1']
    def b = schema.tables['NOTSAME2']
    assertTrue(a.equivalentColumns(b.sortedColumns,true))
    assertFalse(a.equivalentColumns(b.sortedColumns,false))
  }
}
