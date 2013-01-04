package org.yajul.sql;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.yajul.sql.MetaDataColumns.COLUMN_NAME_INDEX;
import static org.yajul.sql.MetaDataColumns.COLUMN_TABLE_NAME_INDEX;

/**
 * Bean that encapsulates database column metadata.
 * <br>
 * User: jdavis
 * Date: Aug 4, 2003
 * Time: 6:05:02 PM
 *
 * @author jdavis
 */
public class ColumnMetaData implements Serializable {
    private static final Logger log = Logger.getLogger(ColumnMetaData.class.getName());

    private String name;
    private String tableName;
    private short dataType;
    private String dataTypeName;
    private int columnSize;
    private int nullable;


    public ColumnMetaData(String name, String tableName, short dataType, String dataTypeName, int columnSize, int nullable) {
        this.name = name;
        this.tableName = tableName;
        this.dataType = dataType;
        this.dataTypeName = dataTypeName;
        this.columnSize = columnSize;
        this.nullable = nullable;
    }

    /**
     * Returns the name of the column.
     *
     * @return String - The name of the column.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the table name that the column is in.
     *
     * @return String - The name of the table.
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Returns the column data type (see java.sql.Types).
     *
     * @return int - The column data type.
     * @see java.sql.Types
     */
    public short getDataType() {
        return dataType;
    }

    /**
     * Returns the name of the data type.
     *
     * @return Sring - The name of the datatype.
     */
    public String getDataTypeName() {
        return dataTypeName;
    }

    /**
     * Returns the column size.
     *
     * @return int - The number of bytes in the column.
     */
    public int getColumnSize() {
        return columnSize;
    }

    /**
     * Returns the nullalbe attribute of the column.
     *
     * @return int - Nullable attribute.
     */
    public int getNullable() {
        return nullable;
    }

    /**
     * Returns true if the column is a type compatible with 'int'.
     *
     * @return boolean - True if the column is an int-compatible column.
     */
    public boolean isIntType() {
        return isIntType(dataType);
    }

    /**
     * Returns true if the column is a type compatible with 'String'.
     *
     * @return boolean - True if the column is a String-compatible column.
     */
    public boolean isStringType() {
        return isStringType(dataType);
    }

    /**
     * Returns true if the column is a type compatible with 'int'.
     *
     * @param dataType The data type (see java.sql.Types)
     * @return boolean - True if the column is an int-compatible column.
     * @see java.sql.Types
     */
    public static boolean isIntType(int dataType) {
        if (log.isLoggable(Level.FINE))
            log.log(Level.FINE,"isIntType() : dataType = " + dataType);
        switch (dataType) {
            // --- Lossless conversion ---

            // These types will always convert properly into an int.
            case Types.INTEGER:
                return true;
            case Types.SMALLINT:
                return true;
            case Types.TINYINT:
                return true;

                // --- Potential conversion loss ---

                // BIGINT values may lose data on conversion.
                // TODO: Add a flag to allow/dissalow this.
            case Types.BIGINT:
                return true;
                // NUMERIC values may lose data on conversion (depending on the
                // size of the numeric value).
                // TODO: Add a flag to allow/disallow this.
            case Types.NUMERIC:
                return true;
                // DECIMAL values may lose data on conversion (depending on the
                // size of the numeric value).
                // TODO: Add a flag to allow/disallow this.
            case Types.DECIMAL:
                return true;

                // --- All other types ---
                // All other types are incompatible.
            default:
                return false;
        }
    }

    /**
     * Returns true if the column is a type compatible with 'String'.
     *
     * @param dataType The data type (see java.sql.Types)
     * @return boolean - True if the column is a String-compatible column.
     * @see java.sql.Types
     */
    public static boolean isStringType(int dataType) {
        if (log.isLoggable(Level.FINE))
            log.log(Level.FINE,"isStringType() : dataType = " + dataType);
        switch (dataType) {
            // --- Lossless conversion ---

            case Types.CHAR:
                return true;
            case Types.VARCHAR:
                return true;

                // --- All other types ---
                // All other types are incompatible.
            default:
                return false;
        }
    }

    public static ColumnMetaData create(ResultSet rs, Map<String, ColumnType> types) throws SQLException {
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
        String name = rs.getString(COLUMN_NAME_INDEX);
        String tableName = rs.getString(COLUMN_TABLE_NAME_INDEX);
        short dataType = rs.getShort(5);
        String dataTypeName = rs.getString(6);
        int columnSize = rs.getInt(7);
        int nullable = rs.getInt(11);
        ColumnType columnType = types.get(dataTypeName);
        if (columnType == null)
            throw new SQLException("Type '" + dataTypeName + "' not found!");
        return new ColumnMetaData(name,tableName,dataType,dataTypeName,columnSize,nullable);

    }
}
