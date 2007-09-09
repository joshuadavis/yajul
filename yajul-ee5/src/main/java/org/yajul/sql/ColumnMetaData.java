package org.yajul.sql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

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
    private static final Logger log = LoggerFactory.getLogger(ColumnMetaData.class);

    private String name;
    private String tableName;
    private short dataType;
    private String dataTypeName;
    private int columnSize;
    private int nullable;

    /**
     * Creates column meta data from the column name, table name, and result
     * set positioned at the proper column.
     *
     * @param name      The name of the column.
     * @param tableName The name of the table the column is in.
     * @param rs        The result set containing the rest of the information.
     * @throws SQLException If there is a problem reading the
     *                      meta data result set.
     */
    public ColumnMetaData(String name, String tableName, ResultSet rs)
            throws SQLException {
        this.name = name;
        this.tableName = tableName;
/*
1.  TABLE_CAT String => table catalog (may be null)
2.  TABLE_SCHEM String => table schema (may be null)
3.  TABLE_NAME String => table name
4.  COLUMN_NAME String => column name
5.  DATA_TYPE short => SQL type from java.sql.Types
6.  TYPE_NAME String => Data source dependent type name, for a UDT the type name
    is fully qualified
7.  COLUMN_SIZE int => column size. For char or date types this is the maximum
    number of characters, for numeric or decimal types this is precision.
8.  BUFFER_LENGTH is not used.
9.  DECIMAL_DIGITS int => the number of fractional digits
10. NUM_PREC_RADIX int => Radix (typically either 10 or 2)
11. NULLABLE int => is NULL allowed?
        columnNoNulls - might not allow NULL values
        columnNullable - definitely allows NULL values
        columnNullableUnknown - nullability unknown
12. REMARKS String => comment describing column (may be null)
13. COLUMN_DEF String => default value (may be null)
14. SQL_DATA_TYPE int => unused
15. SQL_DATETIME_SUB int => unused
16. CHAR_OCTET_LENGTH int => for char types the maximum number of
    bytes in the column
17. ORDINAL_POSITION int => index of column in table (starting at 1)
18. IS_NULLABLE String => "NO" means column definitely does not allow
    NULL values; "YES" means the column might allow NULL values. An empty
    string means nobody knows.
*/

        this.dataType = rs.getShort(5);
        this.dataTypeName = rs.getString(6);
        this.columnSize = rs.getInt(7);
        this.nullable = rs.getInt(11);
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
        if (log.isDebugEnabled())
            log.debug("isIntType() : dataType = " + dataType);
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
        if (log.isDebugEnabled())
            log.debug("isStringType() : dataType = " + dataType);
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
}
