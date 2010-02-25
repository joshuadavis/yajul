package org.yajul.dbarchiver

import org.yajul.jdbc.ConnectionInfo
import groovy.sql.Sql
import org.yajul.jdbc.DbSchema
import org.yajul.jdbc.Table

/**
 * Describes one end of the archiving process
 * <br>
 * User: josh
 * Date: Feb 22, 2010
 * Time: 8:49:09 AM
 */
class Endpoint {
  ConnectionInfo info
  String tableName

  Sql sql
  DbSchema schema
  Table table
}
