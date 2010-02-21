package org.yajul.jdbc

import groovy.sql.Sql
import java.sql.Connection
import java.sql.DatabaseMetaData
import java.sql.ResultSet

/**
 * TODO: Class level comments!
 * <br>
 * User: Josh
 * Date: Feb 21, 2010
 * Time: 11:06:38 AM
 */
class DbSchema
{
  Map<String, Table> tables = [:]
  Map<String, ColumnType> types = [:]

  DbSchema(Sql db, String tableName = null)
  {
    load(db,tableName)
  }

  private def load(Sql db,String tableName)
  {
    db.cacheConnection {
      Connection con ->
      def DatabaseMetaData md = con.metaData;
      println "driverName=${md.driverName}"
      loadTypes(md)
      loadTables(md,tableName)
      loadColumns(md,tableName)
      loadPrimaryKeys(md)
    }
  }

  private def loadTypes(DatabaseMetaData md)
  {
    /*
    If SQL distinct or structured types are supported, then information on the individual types may be obtained from the getUDTs() method.
    Each type description has the following columns:
     1. TYPE_NAME String => Type name
     2. DATA_TYPE int => SQL data type from java.sql.Types
     3. PRECISION int => maximum precision
     4. LITERAL_PREFIX String => prefix used to quote a literal (may be null)
     5. LITERAL_SUFFIX String => suffix used to quote a literal (may be null)
     6. CREATE_PARAMS String => parameters used in creating the type (may be null)
     7. NULLABLE short => can you use NULL for this type.
            * typeNoNulls - does not allow NULL values
            * typeNullable - allows NULL values
            * typeNullableUnknown - nullability unknown
     8. CASE_SENSITIVE boolean=> is it case sensitive.
     9. SEARCHABLE short => can you use "WHERE" based on this type:
            * typePredNone - No support
            * typePredChar - Only supported with WHERE .. LIKE
            * typePredBasic - Supported except for WHERE .. LIKE
            * typeSearchable - Supported for all WHERE ..
    10. UNSIGNED_ATTRIBUTE boolean => is it unsigned.
    11. FIXED_PREC_SCALE boolean => can it be a money value.
    12. AUTO_INCREMENT boolean => can it be used for an auto-increment value.
    13. LOCAL_TYPE_NAME String => localized version of type name (may be null)
    14. MINIMUM_SCALE short => minimum scale supported
    15. MAXIMUM_SCALE short => maximum scale supported
    16. SQL_DATA_TYPE int => unused
    17. SQL_DATETIME_SUB int => unused
    18. NUM_PREC_RADIX int => usually 2 or 10
     */

    def typeInfo = md.typeInfo
    JdbcUtil.iterate(typeInfo,
                     {
                       ResultSet rs ->
                       def t = new ColumnType(name: rs.getString('TYPE_NAME'), jdbcType: rs.getInt('DATA_TYPE'))
                       types[t.name] = t
                     })
  }

  private def loadPrimaryKeys(DatabaseMetaData md)
  {
    tables.values().each {
      Table table ->
      JdbcUtil.iterate(
              md.getPrimaryKeys(null, null, table.name),
              {
              ResultSet rs ->
              def pkColumnName = rs.getString('COLUMN_NAME')
              def column = table.columns[pkColumnName]
              table.primaryKeys << column
              column.primaryKey = true
              })
    }
  }

  private def loadColumns(DatabaseMetaData md,String tableName)
  {
    /**
     * Each column description has the following columns:
   1. TABLE_CAT String => table catalog (may be null)
   2. TABLE_SCHEM String => table schema (may be null)
   3. TABLE_NAME String => table name
   4. COLUMN_NAME String => column name
   5. DATA_TYPE int => SQL type from java.sql.Types
   6. TYPE_NAME String => Data source dependent type name, for a UDT the type name is fully qualified
   7. COLUMN_SIZE int => column size.
   8. BUFFER_LENGTH is not used.
   9. DECIMAL_DIGITS int => the number of fractional digits. Null is returned for data types where DECIMAL_DIGITS is not applicable.
  10. NUM_PREC_RADIX int => Radix (typically either 10 or 2)
  11. NULLABLE int => is NULL allowed.
          * columnNoNulls - might not allow NULL values
          * columnNullable - definitely allows NULL values
          * columnNullableUnknown - nullability unknown
  12. REMARKS String => comment describing column (may be null)
  13. COLUMN_DEF String => default value for the column, which should be interpreted as a string when the value is enclosed in single quotes (may be null)
  14. SQL_DATA_TYPE int => unused
  15. SQL_DATETIME_SUB int => unused
  16. CHAR_OCTET_LENGTH int => for char types the maximum number of bytes in the column
  17. ORDINAL_POSITION int => index of column in table (starting at 1)
  18. IS_NULLABLE String => ISO rules are used to determine the nullability for a column.
          * YES --- if the parameter can include NULLs
          * NO --- if the parameter cannot include NULLs
          * empty string --- if the nullability for the parameter is unknown
  19. SCOPE_CATLOG String => catalog of table that is the scope of a reference attribute (null if DATA_TYPE isn't REF)
  20. SCOPE_SCHEMA String => schema of table that is the scope of a reference attribute (null if the DATA_TYPE isn't REF)
  21. SCOPE_TABLE String => table name that this the scope of a reference attribure (null if the DATA_TYPE isn't REF)
  22. SOURCE_DATA_TYPE short => source type of a distinct type or user-generated Ref type, SQL type from java.sql.Types (null if DATA_TYPE isn't DISTINCT or user-generated REF)
  23. IS_AUTOINCREMENT String => Indicates whether this column is auto incremented
          * YES --- if the column is auto incremented
          * NO --- if the column is not auto incremented
          * empty string --- if it cannot be determined whether the column is auto incremented parameter is unknown

    The COLUMN_SIZE column the specified column size for the given column.
     For numeric data, this is the maximum precision.
     For character data, this is the length in characters.
     For datetime datatypes, this is the length in characters of the String representation (assuming the maximum allowed precision of the fractional seconds component).
     For binary data, this is the length in bytes.
     For the ROWID datatype, this is the length in bytes.
     Null is returned for data types where the column size is not applicable.
     */

    def columnResultSet = md.getColumns(null, null, tableName, null)
    JdbcUtil.iterate(
            columnResultSet,
            {
            ResultSet rs ->
            def table = tables[rs.getString("TABLE_NAME")]
            def type = types[rs.getString('TYPE_NAME')]
            def size = rs.getInt('COLUMN_SIZE')
            def column = new Column(table: table,
                                    name: rs.getString('COLUMN_NAME'),
                                    type: type,
                                    size: size,
                                    ord: rs.getInt('ORDINAL_POSITION'),
                                    nullable: rs.getString("IS_NULLABLE") == "YES"
                                    // Primary key will be initialized later.
            )
            table.columns[column.name] = column
            })
  }

  private def loadTables(DatabaseMetaData md,String tableName)
  {
    JdbcUtil.iterate(
            md.getTables(null, null, tableName),
            {
            ResultSet rs ->
            def name = rs.getString("TABLE_NAME")
            tables[name] = new Table(name: name)
            })
  }


  public String toString()
  {
    return "*** TABLES ***\n${tables.entrySet().join('\n')}--- END OF TABLES --";
  }

  boolean tableExists(String tableName)
  {
    return tables.containsKey(tableName)
  }
}
