package org.yajul.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.yajul.sql.MetaDataColumns.TYPE_JDBC_TYPE_INDEX;
import static org.yajul.sql.MetaDataColumns.TYPE_NAME_INDEX;

public class ColumnType {
    private String name;
    private JdbcType jdbcType;

    public static ColumnType create(ResultSet rs) throws SQLException {
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
        String name = rs.getString(TYPE_NAME_INDEX);
        int type = rs.getInt(TYPE_JDBC_TYPE_INDEX);
        return new ColumnType(name,type);
    }

    public ColumnType(String name, int t) {
        this.name = name;
        this.jdbcType = JdbcType.get(t);
    }

    public String getName() {
        return name;
    }

    public JdbcType getJdbcType() {
        return jdbcType;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ColumnType");
        sb.append("{name='").append(name).append('\'');
        sb.append(", jdbcType=").append(jdbcType);
        sb.append('}');
        return sb.toString();
    }
}
