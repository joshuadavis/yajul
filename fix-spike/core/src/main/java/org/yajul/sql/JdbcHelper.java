package org.yajul.sql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods for JDBC.
 * <br>User: Joshua Davis
 * Date: Sep 9, 2007
 * Time: 11:29:59 AM
 */
public class JdbcHelper {
    private static final Logger log = LoggerFactory.getLogger(JdbcHelper.class);

    public static final int TABLE_NAME_INDEX = 3;

    public static final int COLUMN_NAME_INDEX = 4;

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
            }
            catch (SQLException e) {
                log.error(e.toString(), e);
            }
        }

        if (stmt != null) {
            try {
                stmt.close();
            }
            catch (SQLException e) {
                log.error(e.toString(), e);
            }
        }

        if (con != null) {
            try {
                con.close();
            }
            catch (SQLException e) {
                log.error(e.toString(), e);
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
        List<String> list = new ArrayList<String>();
        try {
            DatabaseMetaData md = con.getMetaData();
            rs = md.getTables(null, null, null, null);
            while (rs.next()) {
                String name = rs.getString(TABLE_NAME_INDEX);
                list.add(name);
            } // while
            return list;
        }
        catch (SQLException e) {
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
                String name = rs.getString(TABLE_NAME_INDEX);
                if (name.equals(tableName))
                    return true;
            } // while
        }
        catch (SQLException e) {
            log.error(e.toString(), e);
        }
        finally {
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
        List<String> list = new ArrayList<String>();
        try {
            DatabaseMetaData md = con.getMetaData();
            rs = md.getColumns(null, null, tableName, null);
            while (rs.next()) {
                String name = rs.getString(COLUMN_NAME_INDEX);
                list.add(name);
            } // while
            return list;
        }
        finally {
            close(null, null, rs);
        }
    }
}
