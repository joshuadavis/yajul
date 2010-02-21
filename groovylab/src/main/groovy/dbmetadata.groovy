import groovy.sql.Sql
import groovy.grape.Grape

import org.yajul.jdbc.DbSchema
import groovy.sql.GroovyRowResult
import org.yajul.jdbc.Column
import java.sql.Statement
import java.sql.Connection
import java.sql.PreparedStatement

/**
 * Database Metadata with Groovy
 * <br>
 * User: Josh
 * Date: Feb 21, 2010
 * Time: 7:45:28 AM
 */

Grape.grab(group: 'mysql', module: 'mysql-connector-java', version: '5.1.10', classLoader: this.class.classLoader.rootLoader)

def die(GString string)
{
  println "ERROR: ${string}"
  System.exit(-1)
}

def server = "localhost"
def database = "tradesnapshot1"
def username = "sa"
def password = "sa"
def driverClassName = 'com.mysql.jdbc.Driver'
def source = Sql.newInstance("jdbc:mysql://${server}/${database}", username, password, driverClassName)
def target = Sql.newInstance("jdbc:mysql://${server}/target", username, password, driverClassName)


def tableName = "fix_event_log"

def sourceSchema = new DbSchema(source,tableName)
println sourceSchema

sourceTable = sourceSchema.tables[tableName]
if (sourceTable == null)
  die "Table not found in ${database}"

def targetSchema = new DbSchema(target,tableName)
println targetSchema

if (!targetSchema.tableExists(tableName))
{
  println "Creating $tableName in target..."
  String createSql = sourceTable.createStatement()
  target.execute(createSql)
}

// Get a batch of rows from the source table.
def conditions = "where event_timestamp < '2009-01-01 00:00:00'"
def orderBy = "order by event_timestamp asc, event_millis asc, id asc"
def batchSize = 100
def List<Column> columns = sourceTable.sortedColumns
def columnNames = columns.collect({ Column c -> c.name }).join(", ")
def columnValueParams = columns.collect({ "?" }).join(", ")

def String sql = "select ${columnNames} from ${tableName} ${conditions} ${orderBy} limit ${batchSize}"
def List<List<Object>> rows = [];
source.eachRow(sql) { row ->
  def List<Object> rowData = columns.collect { Column c -> row[c.name] }
  rows << rowData
}

println "${rows.size()} rows read... inserting..."
def insert = "insert into ${tableName} (${columnNames}) values (${columnValueParams})"
rows.each {
  List<Object> row ->
    int r = target.executeUpdate(insert,row)
    println "r=$r"
}



