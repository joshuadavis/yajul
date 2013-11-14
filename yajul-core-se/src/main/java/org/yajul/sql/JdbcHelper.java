package org.yajul.sql;


import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static org.yajul.juli.LogHelper.unexpected;
import static org.yajul.collections.CollectionUtil.newArrayList;
import static org.yajul.collections.CollectionUtil.newHashMap;

/**
 * Utility methods for JDBC.
 * <br>User: Joshua Davis
 * Date: Sep 9, 2007
 * Time: 11:29:59 AM
 */
public class JdbcHelper {
    private static final Logger log = Logger.getLogger(JdbcHelper.class.getName());

    /**
     * Attempts to load the JDBC driver on the thread, current or system class
     * loaders
     *
     * @param driverClassName the fully qualified class name of the driver class
     * @throws ClassNotFoundException if the class cannot be found or loaded
     */
    public static void loadDriver(String driverClassName) throws ClassNotFoundException {
        // let's try the thread context class loader first
        // let's try to use the system class loader
        try {
            Class.forName(driverClassName);
        } catch (ClassNotFoundException e) {
            try {
                Thread.currentThread().getContextClassLoader().loadClass(driverClassName);
            } catch (ClassNotFoundException e2) {
                // now let's try the classloader which loaded us
                try {
                    JdbcHelper.class.getClassLoader().loadClass(driverClassName);
                } catch (ClassNotFoundException e3) {
                    throw e;
                }
            }
        }
    }


    /**
     * Get a JDBC connection with DriverManager.
     *
     * @param url             jdbc url
     * @param username        database user
     * @param password        database password
     * @param driverClassName JDBC driver class
     * @return a jdbc connection
     * @throws SQLException           if DriverManager can't make the connection
     * @throws ClassNotFoundException if the driver class could not be loaded
     */
    public static Connection getConnection(String url, String username, String password, String driverClassName) throws SQLException, ClassNotFoundException {
        loadDriver(driverClassName);
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Closes the result set, statement, and connection if
     * they are not null.  Exceptions will be logged, but ignored.
     * This is typically used in a <tt>finally</tt> block to
     * clean up after a JDBC connection is used.
     *
     * @param con  The connection.
     * @param stmt The statement.
     * @param rs   The result set.
     */
    public static void close(Connection con, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                unexpected(log, e);
            }
        }

        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                unexpected(log, e);
            }
        }

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                unexpected(log, e);
            }
        }
    }

    /**
     * Returns all table names as a list of strings.
     *
     * @param con a JDBC connection
     * @return all table names as a list of strings.
     * @throws java.sql.SQLException if something went wrong
     */
    public static List<String> getTableNames(Connection con) throws SQLException {
        ResultSet rs = null;
        List<String> list = newArrayList();
        try {
            DatabaseMetaData md = con.getMetaData();
            rs = md.getTables(null, null, null, null);
            while (rs.next()) {
                String name = rs.getString(MetaDataColumns.TABLE_NAME_INDEX);
                list.add(name);
            } // while
            return list;
        } catch (SQLException e) {
            throw e;
        } finally {
            close(null, null, rs);
        }
    }


    /**
     * Returns true if the specified table exists.
     *
     * @param con       The connection.
     * @param tableName The name of the table to look for.
     * @return boolean - True if the table exists, false if not or if there
     *         was a problem.
     */
    public static boolean tableExists(Connection con, String tableName) {
        if (con == null)
            throw new IllegalStateException("Connection cannot be null!");

        ResultSet rs = null;
        try {
            DatabaseMetaData md = con.getMetaData();
            rs = md.getTables(null, null, tableName, null);
            while (rs.next()) {
                String name = rs.getString(MetaDataColumns.TABLE_NAME_INDEX);
                if (name.equals(tableName))
                    return true;
            } // while
        } catch (SQLException e) {
            unexpected(log, e);
        } finally {
            close(null, null, rs);
        }
        return false;
    }

    /**
     * Returns all column names in a table as a list of strings.
     *
     * @param con       a JDBC connection
     * @param tableName The table name
     * @return all column names as a list of strings.
     * @throws java.sql.SQLException if something went wrong
     */
    public static List<String> getColumnNames(Connection con, String tableName) throws SQLException {
        ResultSet rs = null;
        List<String> list = newArrayList();
        try {
            DatabaseMetaData md = con.getMetaData();
            rs = md.getColumns(null, null, tableName, null);
            while (rs.next()) {
                String name = rs.getString(MetaDataColumns.COLUMN_NAME_INDEX);
                list.add(name);
            } // while
            return list;
        } finally {
            close(null, null, rs);
        }
    }

    /**
     * Returns all the data types the database supports.
     *
     * @param con JDBC connection
     * @return a map of column types, by name
     * @throws SQLException if something goes wrong
     */
    public static Map<String, ColumnType> getColumnTypes(Connection con) throws SQLException {
        ResultSet rs = null;
        Map<String, ColumnType> types = newHashMap();
        try {
            DatabaseMetaData md = con.getMetaData();
            rs = md.getTypeInfo();
            while (rs.next()) {
                ColumnType t = ColumnType.create(rs);
                if (types.containsKey(t.getName()))
                    throw new SQLException("Duplicate type name '" + t.getName() + "'!");
                types.put(t.getName(), t);
            } // while
            return types;
        } finally {
            close(null, null, rs);
        }
    }

    public static Map<String, ColumnMetaData> getColumnsForTable(Connection con, String tableName) throws SQLException {
        Map<String, ColumnType> types = getColumnTypes(con); // First, get the types
        ResultSet rs = null;
        Map<String, ColumnMetaData> columns = newHashMap();
        try {
            DatabaseMetaData md = con.getMetaData();
            rs = md.getColumns(null, null, tableName, null);
            while (rs.next()) {
                ColumnMetaData cmd = ColumnMetaData.create(rs, types);
                if (columns.containsKey(cmd.getName()))
                    throw new SQLException("Duplicate column name '" + cmd.getName() + "'");
                columns.put(cmd.getName(), cmd);
            } // while
            return columns;
        } finally {
            close(null, null, rs);
        }
    }
}
