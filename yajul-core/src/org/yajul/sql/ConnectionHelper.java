package org.yajul.sql;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import org.yajul.log.LogUtil;
import org.apache.log4j.Logger;

/**
 * TODO: Add javadoc
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

    private static final int TABLE_NAME_COLUMN_INDEX = 3;

    private Connection con;
    private Statement stmt;
    private DatabaseMetaData meta;
    private int rowsAffected;

    public ConnectionHelper(Connection con)
    {
        if (con == null)
            throw new IllegalArgumentException("Connection cannot be null!");

        this.con = con;
        this.rowsAffected = 0;
    }

    public Connection getCon()
    {
        return con;
    }

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

    public int getRowsAffected()
    {
        return rowsAffected;
    }

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
                String name = rs.getString(TABLE_NAME_COLUMN_INDEX);
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
