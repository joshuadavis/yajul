package org.yajul.dbarchiver

import groovy.sql.Sql
import org.yajul.jdbc.ConnectionInfo
import org.yajul.jdbc.DbSchema
import org.yajul.jdbc.Column
import java.sql.PreparedStatement
import org.yajul.jdbc.JdbcUtil
import java.sql.Connection

/**
 * Safely archives
 * <br>
 * User: josh
 * Date: Feb 21, 2010
 * Time: 9:06:47 PM
 */
class Archiver
{
  Sql source
  Sql target

  int rowsRetrieved;
  int rowsInserted;
  int rowsDeleted;
  int batches;

  Archiver(ConnectionInfo sourceInfo, ConnectionInfo targetInfo) {
    source = sourceInfo.connect()
    target = targetInfo.connect()
  }

  def archiveRows(String tableName,String conditions,String orderBy, int batchSize) {
    def sourceSchema = new DbSchema(source,tableName)
    println "${sourceSchema.tables.size()} tables in source"
    def sourceTable = sourceSchema.tables[tableName]
    if (sourceTable == null)
      throw new IllegalArgumentException("Table $tableName not found in source databaase")
    def targetSchema = new DbSchema(target,tableName)
    if (!targetSchema.tableExists(tableName))
    {
      println "Creating $tableName in target..."
      String createSql = sourceTable.createStatement()
      target.execute(createSql)
    }
    List<Column> columns = sourceTable.sortedColumns
    def columnNames = columns.collect({ Column c -> c.name }).join(", ")
    String columnValueParams = columns.collect({ "?" }).join(", ")
    def primaryKeys = sourceTable.sortedPrimaryKeys
    String primaryKeyParams = primaryKeys.collect({ Column c -> "${c.name} = ?" }).join(" and ")
    String insert = "insert into ${tableName} (${columnNames}) values (${columnValueParams})"
    String delete = "delete from ${tableName} where ${primaryKeyParams}"
    String select = "select ${columnNames} from ${tableName} ${conditions} ${orderBy} limit ${batchSize}"

    def sourceCon = source.connection
    def targetCon = target.connection

    while (true)
    {
      List<ArchiveRow> rows = retrieveRows(select, sourceTable)

      if (rows.isEmpty())
      {
        println "no more rows, exiting"
        break
      }


      println "${batches}: ${rows.size()} rows read... moving..."
      println "first row is " + rows[0].rowData
      moveBatch(sourceCon,insert,targetCon,delete,rows)
      batches++;
    } // while
  }

  private List<ArchiveRow> retrieveRows(String select, sourceTable) {
    def List<ArchiveRow> rows = [];
    source.eachRow(select) { row ->
      List<Object> rowData = sourceTable.sortedColumns.collect {Column c -> row[c.name] }
      List<Object> keys = sourceTable.sortedPrimaryKeys.collect {Column c -> row[c.name] }
      rows << new ArchiveRow(rowData: rowData, keys: keys)
    }
    return rows
  }

  private def moveBatch(Connection sourceCon,
                        String insert,
                        Connection targetCon,
                        String delete,
                        List<ArchiveRow> rows) {
    boolean sourceAuto = sourceCon.autoCommit
    boolean targetAuto = targetCon.autoCommit
    PreparedStatement insertStmt,deleteStmt
    try
    {
      sourceCon.setAutoCommit(false)
      targetCon.setAutoCommit(false)
      insertStmt = targetCon.prepareStatement(insert)
      deleteStmt = sourceCon.prepareStatement(delete)
      rows.each {
        ArchiveRow r ->
          JdbcUtil.setParameters(insertStmt,r.rowData)
          insertStmt.addBatch()
          JdbcUtil.setParameters(deleteStmt,r.keys)
          deleteStmt.addBatch()
      }
      int[] results = insertStmt.executeBatch()
      int inserts = results.toList().sum()
      // println "inserts = " + inserts
      rowsInserted += inserts;
      results = deleteStmt.executeBatch()
      int deletes = results.toList().sum()
      // println "deletes = " + deletes;
      rowsDeleted += deletes;
      sourceCon.commit()
      targetCon.commit()
    }
    catch (Exception e)
    {
      sourceCon.rollback()
      targetCon.rollback()
    }
    finally
    {
      JdbcUtil.close(insertStmt)
      JdbcUtil.close(deleteStmt)
      sourceCon.setAutoCommit(sourceAuto)
      targetCon.setAutoCommit(targetAuto)
    }
  }
}
