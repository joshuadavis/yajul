package org.yajul.sql;

import org.yajul.util.ResourceUtil;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Provides a non-JNDI DataSource so that the DAOs can be used by JVMs outside of the container.
 * This will be used by unit tests and by database initialization 'actors'.
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Sep 20, 2003
 * Time: 6:55:14 PM
 */
public class SimpleDataSource implements DataSource
{
    private static final String RESOURCE_NAME = "SimpleDataSource.properties";

    private String driverClass;
    private String jdbcURL;
    private String defaultUsername;
    private String defaultPassword;

    public SimpleDataSource(String driverClass, String jdbcURL, String defaultUsername, String defaultPassword)
    {
        initialize(driverClass, jdbcURL, defaultUsername, defaultPassword);
    }

    public SimpleDataSource() throws IOException
    {
        Properties properties = ResourceUtil.loadProperties(RESOURCE_NAME);
        initialize(properties);
    }

    public SimpleDataSource(Properties properties)
    {
        initialize(properties);
    }

    /**
     * <p>Attempts to establish a connection with the data source that
     * this <code>DataSource</code> object represents.
     *
     * @return  a connection to the data source
     * @exception java.sql.SQLException if a database access error occurs
     */
    public Connection getConnection() throws SQLException
    {
        return getConnection(defaultUsername,defaultPassword);
    }

    /**
     * <p>Attempts to establish a connection with the data source that
     * this <code>DataSource</code> object represents.
     *
     * @param username the database user on whose behalf the connection is
     *  being made
     * @param password the user's password
     * @return  a connection to the data source
     * @exception java.sql.SQLException if a database access error occurs
     */
    public Connection getConnection(String username, String password)
            throws SQLException
    {
        try
        {
            if (driverClass == null || driverClass.length() == 0)
                throw new SQLException("JDBC driver class not specified.");
            Class.forName(driverClass);
        }
        catch (ClassNotFoundException e)
        {
            throw new SQLException("JDBC driver class '" + driverClass + "' was not found!");
        }

        return DriverManager.getConnection(jdbcURL,username,password);
    }

    /**
     * <p>Retrieves the log writer for this <code>DataSource</code>
     * object.
     *
     * <p>The log writer is a character output stream to which all logging
     * and tracing messages for this data source will be
     * printed.  This includes messages printed by the methods of this
     * object, messages printed by methods of other objects manufactured
     * by this object, and so on.  Messages printed to a data source
     * specific log writer are not printed to the log writer associated
     * with the <code>java.sql.Drivermanager</code> class.  When a
     * <code>DataSource</code> object is
     * created, the log writer is initially null; in other words, the
     * default is for logging to be disabled.
     *
     * @return the log writer for this data source or null if
     *        logging is disabled
     * @exception java.sql.SQLException if a database access error occurs
     * @see #setLogWriter
     */
    public PrintWriter getLogWriter() throws SQLException
    {
        return null;
    }

    /**
     * <p>Sets the log writer for this <code>DataSource</code>
     * object to the given <code>java.io.PrintWriter</code> object.
     *
     * <p>The log writer is a character output stream to which all logging
     * and tracing messages for this data source will be
     * printed.  This includes messages printed by the methods of this
     * object, messages printed by methods of other objects manufactured
     * by this object, and so on.  Messages printed to a data source-
     * specific log writer are not printed to the log writer associated
     * with the <code>java.sql.Drivermanager</code> class. When a
     * <code>DataSource</code> object is created the log writer is
     * initially null; in other words, the default is for logging to be
     * disabled.
     *
     * @param out the new log writer; to disable logging, set to null
     * @exception java.sql.SQLException if a database access error occurs
     * @see #getLogWriter
     */
    public void setLogWriter(PrintWriter out) throws SQLException
    {
    }

    /**
     * <p>Sets the maximum time in seconds that this data source will wait
     * while attempting to connect to a database.  A value of zero
     * specifies that the timeout is the default system timeout
     * if there is one; otherwise, it specifies that there is no timeout.
     * When a <code>DataSource</code> object is created, the login timeout is
     * initially zero.
     *
     * @param seconds the data source login time limit
     * @exception java.sql.SQLException if a database access error occurs.
     * @see #getLoginTimeout
     */
    public void setLoginTimeout(int seconds) throws SQLException
    {
    }

    /**
     * Gets the maximum time in seconds that this data source can wait
     * while attempting to connect to a database.  A value of zero
     * means that the timeout is the default system timeout
     * if there is one; otherwise, it means that there is no timeout.
     * When a <code>DataSource</code> object is created, the login timeout is
     * initially zero.
     *
     * @return the data source login time limit
     * @exception java.sql.SQLException if a database access error occurs.
     * @see #setLoginTimeout
     */
    public int getLoginTimeout() throws SQLException
    {
        return 0;
    }

    private void initialize(String driverClass, String jdbcURL, String defaultUsername, String defaultPassword)
    {
        this.driverClass = driverClass;
        this.jdbcURL = jdbcURL;
        this.defaultUsername = defaultUsername;
        this.defaultPassword = defaultPassword;
    }

    private void initialize(Properties properties)
    {
        initialize(properties.getProperty("driverClass"),properties.getProperty("url"),
                properties.getProperty("username"),
                properties.getProperty("password"));
    }
}
