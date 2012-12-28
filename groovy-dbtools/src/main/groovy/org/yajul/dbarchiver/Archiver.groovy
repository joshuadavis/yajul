package org.yajul.dbarchiver

import groovy.util.logging.Log

import java.sql.Connection
import java.sql.PreparedStatement
import org.yajul.jdbc.Column
import org.yajul.jdbc.ConnectionInfo
import org.yajul.jdbc.JdbcUtil

import org.yajul.jdbc.DbSchema

/**
 * Safely archives
 * <br>
 * User: josh
 * Date: Feb 21, 2010
 * Time: 9:06:47 PM
 */
@Log
class Archiver {
  Endpoint source
  Endpoint target
  boolean createTargetTable
  boolean jdbcBatchMode
  int batchLimit = Integer.MAX_VALUE
  boolean ignorePk = true
  
  long startTime
  int rowsRetrieved;
  int rowsInserted;
  int rowsDeleted;
  int batches;

  List<Column> columns

  Archiver(ConnectionInfo sourceInfo, ConnectionInfo targetInfo) {
    source = new Endpoint(info: sourceInfo)
    target = new Endpoint(info: targetInfo)
    connect(source)
    connect(target)
  }

  def archiveRows(String sourceTableName, String targetTableName, String conditions, String orderBy, int batchSize) {
    checkSchema(sourceTableName, targetTableName)
    def columnNames = columns.collect({Column c -> c.name }).join(", ")
    String columnValueParams = columns.collect({ "?" }).join(", ")
    def primaryKeys = source.table.sortedPrimaryKeys
    String primaryKeyParams = primaryKeys.collect({Column c -> "${c.name} = ?" }).join(" and ")
    String insert = "insert into ${targetTableName} (${columnNames}) values (${columnValueParams})"
    String delete = "delete from ${sourceTableName} where ${primaryKeyParams}"
    String select = "select ${columnNames} from ${sourceTableName} ${conditions} ${orderBy} limit ${batchSize}"

    def sourceCon = source.sql.connection
    def targetCon = target.sql.connection

    startTime = System.currentTimeMillis()
    while (batches < batchLimit) {
      log.print "${batches}: retrieving..."
      List<ArchiveRow> rows = retrieveRows(select, source.table)

      if (rows.isEmpty()) {
        log.println "no more rows, exiting"
        break
      }
      moveBatch(sourceCon, insert, targetCon, delete, rows)
      batches++;
    } // while
  }

  def checkSchema(String sourceTableName, String targetTableName) {
    initEndpoint(source, sourceTableName)
    if (source.table == null)
      throw new ArchiverException("Table $sourceTableName not found in source ${source.info.url}")
    initEndpoint(target, targetTableName)
    if (target.table == null) {
      if (createTargetTable) {
        println "Creating $targetTableName in target..."
        String createSql = createStatement(targetTableName, source.table.sortedColumns)
        target.sql.execute(createSql)
        initEndpoint(target, targetTableName) // Get the schema again.
      }
      else
        throw new IllegalArgumentException("Table $targetTableName not found in target ${target.info.url}")
    }

    columns = source.table.sortedColumns

    if (!source.table.equivalentColumns(target.table.sortedColumns,ignorePk))
      throw new ArchiverException("Columns differ:\n" + source.table + "\n" + target.table);
  }

  private String createStatement(String name, List<Column> sortedColumns) {
    return "CREATE TABLE $name (\n  ${sortedColumns.collect {Column c -> c.columnDefinition() }.join(",\n  ")} )"
  }

  private initEndpoint(Endpoint endpoint, String aTableName) {
    connect(endpoint)
    endpoint.with {
      tableName = aTableName
      schema = new DbSchema(sql, tableName)
      table = schema.tables[tableName]
    }
  }

  private connect(Endpoint endpoint) {
    endpoint.with {
      if (sql == null) {
        log.print "Connecting to ${endpoint.info.url} ..."
        sql = info.connect()
        log.println "OK"
      }
    }
  }

  private List<ArchiveRow> retrieveRows(String select, sourceTable) {
    def List<ArchiveRow> rows = [];
    source.sql.eachRow(select) {row ->
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
    log.print "moving..."
    boolean sourceAuto = sourceCon.autoCommit
    boolean targetAuto = targetCon.autoCommit
    PreparedStatement insertStmt = null, deleteStmt = null
    try {
      sourceCon.setAutoCommit(false)
      targetCon.setAutoCommit(false)

      insertStmt = targetCon.prepareStatement(insert)
      deleteStmt = sourceCon.prepareStatement(delete)
      rows.each {
        ArchiveRow r ->
        moveRow(r, insertStmt, deleteStmt)
      }
      if (jdbcBatchMode) {
        log.print "execute batch..."
        int inserts = executeBatch(insertStmt)
        rowsInserted += inserts;
        int deletes = executeBatch(deleteStmt)
        rowsDeleted += deletes;
      }
      log.print "committing..."
      sourceCon.commit()
      targetCon.commit()
      def elapsed = (System.currentTimeMillis() - startTime) / 1000
      log.println " OK (${rowsInserted}, ${String.format("%.2f",rowsInserted / elapsed)} rows/sec)"
    }
    catch (Exception e) {
      log.print " ERROR: ${e}, rolling back..."
      sourceCon.rollback()
      targetCon.rollback()
      log.println "Changes rolled back."
      throw e;
    }
    finally {
      JdbcUtil.close(insertStmt)
      JdbcUtil.close(deleteStmt)
      sourceCon.setAutoCommit(sourceAuto)
      targetCon.setAutoCommit(targetAuto)
    }
  }

  private def moveRow(ArchiveRow r, PreparedStatement insertStmt, PreparedStatement deleteStmt)
  {
    JdbcUtil.setParameters(insertStmt, r.rowData)
    if (jdbcBatchMode)
    {
      insertStmt.addBatch()
    }
    else
    {
      int inserts = insertStmt.executeUpdate()
      rowsInserted += inserts;
    }
    JdbcUtil.setParameters(deleteStmt, r.keys)
    if (jdbcBatchMode)
    {
      deleteStmt.addBatch()
    }
    else
    {
      int deletes = deleteStmt.executeUpdate()
      rowsDeleted += deletes;
    }
  }

  int executeBatch(PreparedStatement statement) {
    int[] results = statement.executeBatch()
    int sum = 0
    for (r in results) { sum += r }
    return sum
  }
}
