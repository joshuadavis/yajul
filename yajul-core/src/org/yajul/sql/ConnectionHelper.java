package org.yajul.sql;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import org.yajul.log.LogUtil;
import org.apache.log4j.Logger;

/**
 * Provides helper methods that make using JDBC connections simpler.
 * User: jdavis
 * Date: Aug 4, 2003
 * Time: 1:16:01 PM
 * @author jdavis
 */
public class ConnectionHelper
{
    /**
     * The logger for this class.
     */
    private static Logger log = LogUtil.getLogger(ConnectionHelper.class.getName());

    private static final int TABLE_NAME_INDEX = 3;

    private static final int COLUMN_NAME_INDEX = 4;

    private Connection con;
    private Statement stmt;
    private DatabaseMetaData meta;
    private int rowsAffected;

    /**
     * Creates a connection helper for the given connection.
     * @param con The connection.
     */
    public ConnectionHelper(Connection con)
    {
        if (con == null)
            throw new IllegalArgumentException("Connection cannot be null!");

        this.con = con;
        this.rowsAffected = 0;
    }

    /**
     * Returns the underlying JDBC connection.
     * @return Connection - The underlying JDBC connection.
     */
    public Connection getConnection()
    {
        return con;
    }

    /**
     * Returns a generic statement, creating it if needed.
     * @return Statement - The generic statement.
     */
    public Statement getStatement()
    {
        if (stmt == null)
        {
            try
            {
                stmt = con.createStatement();
            }
            catch (SQLException e)
            {
                LogUtil.unexpected(log,e);
                stmt = null;
            }
        }
        return stmt;
    }

    /**
     * Returns the database meta-data for the connection.
     * @return DatabaseMetaData - The database meta-data.
     */
    public DatabaseMetaData getMetaData()
    {
        if (meta == null)
        {
            try
            {
                meta = con.getMetaData();
            }
            catch (SQLException e)
            {
                LogUtil.unexpected(log,e);
                meta = null;
            }
        }
        return meta;
    }

    /**
     * Returns the total number of rows updated by the executeUpdate() method.
     * @return int - The number of rows updated, inserted, or deleted.
     */
    public int getRowsAffected()
    {
        return rowsAffected;
    }

    /**
     * Returns the column meta data for the given column in the given table.
     * @param tableName The name of the table.
     * @param columnName The name of the column.
     * @return ColumnMetaData - The column meta data.
     */
    public ColumnMetaData getColumnMetaData(String tableName,String columnName)
    {
        DatabaseMetaData md = getMetaData();
        ResultSet rs = null;

        try
        {
            rs = md.getColumns(null,null,tableName,columnName);
            while (rs.next())
            {
                String table = rs.getString(TABLE_NAME_INDEX);
                String column = rs.getString(COLUMN_NAME_INDEX);
                if (tableName.equals(table) && columnName.equals(column))
                {
                    return new ColumnMetaData(column,table,rs);
                }
            } // while
        }
        catch (SQLException e)
        {
            LogUtil.unexpected(log,e);
        }
        finally
        {
            if (rs != null)
            {
                try
                {
                    rs.close();
                }
                catch (SQLException e)
                {
                    LogUtil.unexpected(log,e);
                }
            }
        }
        return null;
    }
    /**
     * Returns true if the specified table exists.
     * @param tableName The name of the table to look for.
     * @return boolean - True if the table exists, false if not or if there
     * was a problem.
     */
    public boolean tableExists(String tableName)
    {
        if (con == null)
            throw new IllegalStateException("Connection cannot be null!");

        DatabaseMetaData md = getMetaData();
        ResultSet rs = null;

        try
        {
            rs = md.getTables(null,null,tableName,null);
            while (rs.next())
            {
                String name = rs.getString(TABLE_NAME_INDEX);
                if (name.equals(tableName))
                    return true;
            } // while
        }
        catch (SQLException e)
        {
            LogUtil.unexpected(log,e);
        }
        finally
        {
            if (rs != null)
            {
                try
                {
                    rs.close();
                }
                catch (SQLException e)
                {
                    LogUtil.unexpected(log,e);
                }
            }
        }
        return false;
    }


    /**
     * Executes the given SQL as a query, returning a result set.
     * @param sql The SQL to execute.
     * @return ResultSet The result set for the query.
     */
    public ResultSet executeQuery(String sql)
    {
        Statement s = getStatement();
        if (log.isDebugEnabled())
            log.debug("executeQuery() : " + sql);
        try
        {
            return s.executeQuery(sql);
        }
        catch (SQLException e)
        {
            LogUtil.unexpected(log,e);
            return null;
        }
    }

    /**
     * Executes the given SQL as an update and returns the number of rows
     * affected by the update.
     * @param sql The SQL statement
     * @return int - Negative values indicate an error.
     */
    public int executeUpdate(String sql)
    {
        Statement s = getStatement();
        if (log.isDebugEnabled())
            log.debug("executeUpdate() : " + sql);

        int rc = -1;
        try
        {
            rc = s.executeUpdate(sql);
        }
        catch (SQLException e)
        {
            LogUtil.unexpected(log,e);
        }

        if (rc > 0)
            rowsAffected += rc;
        return rc;
    }

    /**
     * Drops the specified table if it exists.
     * @param tableName The name of the table.
     * @return boolean - True if the table was dropped, false if not.
     */
    public boolean dropTableIfExists(String tableName)
    {
        if (tableExists(tableName))
        {
            int rc = executeUpdate("DROP TABLE " + tableName);
            return rc >= 0;
        }
        else
        {
            if (log.isDebugEnabled())
                log.debug("dropTableIfExists() : Table " + tableName + " does not exist.");
            return false;
        }
    }

    /**
     * Releases any resources being held by this ConnectionHelper.
     */
    public void close()
    {
        if (stmt != null)
        {
            try
            {
                stmt.close();
            }
            catch (SQLException e)
            {
                LogUtil.unexpected(log,e);
            }
        }
        stmt = null;
        meta = null;
        con = null;
    }
}
