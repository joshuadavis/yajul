package test

/**
 * TODO: Class level comments!
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
    
  }
}
