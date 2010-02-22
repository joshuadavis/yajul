package org.yajul.dbarchiver

import groovy.sql.Sql
import org.yajul.jdbc.ConnectionInfo
import org.yajul.jdbc.DbSchema
import org.yajul.jdbc.Column

/**
 * TODO: Add class level comments!
 * <br>
 * User: josh
 * Date: Feb 21, 2010
 * Time: 9:06:47 PM
 */
class Archiver
{
  Sql source
  Sql target

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
    def List<Column> columns = sourceTable.sortedColumns
    def columnNames = columns.collect({ Column c -> c.name }).join(", ")
    def columnValueParams = columns.collect({ "?" }).join(", ")
    def primaryKeys = sourceTable.sortedPrimaryKeys

    def String sql = "select ${columnNames} from ${tableName} ${conditions} ${orderBy} limit ${batchSize}"
    def List<ArchiveRow> rows = [];
    source.eachRow(sql) { row ->
      def List<Object> rowData = columns.collect { Column c -> row[c.name] }
      def List<Object> keys = primaryKeys.collect { Column c -> row[c.name] }
      rows << new ArchiveRow(rowData: rowData,keys: keys)
    }

    def primaryKey = primaryKeys.collect({ Column c -> "${c.name} = ?" }).join(" and ")

    println "${rows.size()} rows read... inserting..."
    def insert = "insert into ${tableName} (${columnNames}) values (${columnValueParams})"
    def delete = "delete from ${tableName} where ${primaryKey}"
    target.cacheStatements = true
    rows.each {
      ArchiveRow r ->
        int inserts = target.executeUpdate(insert,r.rowData)
        println "${inserts} : ${insert} ${r.rowData}"
        int deletes = source.executeUpdate(delete,r.keys)
        println "${deletes} : ${delete} ${r.keys}"
    }
  }
}
