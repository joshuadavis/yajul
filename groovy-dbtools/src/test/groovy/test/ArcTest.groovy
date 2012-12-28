package test

import groovy.sql.Sql
import org.yajul.dbarchiver.Archiver
import org.yajul.dbarchiver.ArchiverException

import java.sql.Timestamp

/**
 * TODO: Add class level comments!
 * <br>
 * User: josh
 * Date: Feb 23, 2010
 * Time: 8:08:57 AM
 */
class ArcTest extends GroovyTestCase
{
  def helper = new UnitTestHelper()

  def ArcTest()
  {
  }

  protected void setUp()
  {
    super.setUp();
    helper.cleardb()
  }

  void testArchiver()
  {

    // Grape.grab(group: 'org.hsqldb', module: 'hsqldb', version: '1.8.0.10', classLoader: this.class.classLoader.rootLoader)

    def sourceInfo = helper.getConnectionInfo('sourcedb')
    def targetInfo = helper.getConnectionInfo('targetdb')
    def archiver = new Archiver(sourceInfo, targetInfo)
    archiver.jdbcBatchMode = false
    println "batch mode OFF:"
    doArchiverTest(archiver)
    archiver = new Archiver(sourceInfo, targetInfo)
    archiver.jdbcBatchMode = true
    println "batch mode ON:"
    doArchiverTest(archiver)
  }

  private void doArchiverTest(Archiver archiver)
  {
// HSQLDB capitalizes table names
    def sourceTableName = 'LOG_EVENT'
    def targetTableName = 'COPY_TO'
    def batchSize = 1000

    Sql sourceSql = archiver.source.sql
    Sql targetSql = archiver.target.sql

    UnitTestHelper.loadSchema(sourceSql, "test/ArcTest.xml","source")
    UnitTestHelper.loadSchema(targetSql, "test/ArcTest.xml","target")

    try
    {
      archiver.checkSchema('NOTSAME', 'NOTSAME')
      fail("Exception expected!")
    }
    catch (ArchiverException ignore)
    {
    }

// Create some rows, one every day starting with 2008-12-01
    def date = new GregorianCalendar(2008, Calendar.DECEMBER, 01).time
    println "adding test data..."
    for (i in 1..100000)
    {
      sourceSql.executeUpdate('INSERT INTO ' + sourceTableName +
              ' (event_timestamp,event_millis,message) values (?,?,?)',
                             [new Timestamp(date.time), date.time, new String("MSG ${i}")])
      date = new Date(date.time + 1000 * 60)
    }

    println "copying data..."

    // Get a batch of rows from the source table.
    def conditions = "where event_timestamp < '2009-01-01 00:00:00'"
    def orderBy = "order by event_timestamp asc, event_millis asc, id asc"
    archiver.with {
      createTargetTable = true
      archiveRows(sourceTableName, targetTableName, conditions, orderBy, batchSize)
    }

    sourceSql.eachRow('select * from ' + sourceTableName + ' ' + orderBy + ' limit 1') {
      r -> println "${r}"
    }

    targetSql.eachRow('select * from ' + targetTableName + ' ' + orderBy + ' limit 1') {
      r -> println "${r}"
    }
  }
}
