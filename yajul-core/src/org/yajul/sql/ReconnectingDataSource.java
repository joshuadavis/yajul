/*******************************************************************************
 * $Id$
 * $Author$
 * $Date$
 *
 * Copyright 2002-2003  YAJUL Developers, Joshua Davis, Kent Vogel.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 ******************************************************************************/
package org.yajul.sql;

import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Wraps an ordinary data source with the ability to re-connect if an exeption is thrown while obtaining a connection.
 * The properties (bean properties) control the time between attempts and the maximum number of attempts to make.
 * This class is thread safe, as it is typically used inside an application server.
 * <br>
 * Example spring bean configuration:
 * <pre>
 * &lt;!-- Look up the main data source in the JNDI tree. --&gt;
 * &lt;bean id="myDataSource" class="org.springframework.jndi.JndiObjectFactoryBean"&gt;
 *     &lt;property name="jndiName"&gt;&lt;value&gt;myDataSource&lt;/value&gt;&lt;/property&gt;
 * &lt;/bean&gt;
 * &lt;!--
 * Create a wrapper around the data source that will retry connections.
 * --&gt;
 * &lt;bean id="jdbcDataSource" class="org.yajul.sql.ReconnectingDataSource"&gt;
 *      &lt;property name="dataSource"&gt;
 *       &lt;ref bean="myDataSource"/&gt;
 *     &lt;/property&gt;
 * &lt;/bean&gt;
 * </pre>
 * @author josh Mar 10, 2004 7:56:48 AM
 */
public class ReconnectingDataSource implements DataSource
{
    /**
     * A logger for this class. *
     */
    private static Logger log = Logger.getLogger(ReconnectingDataSource.class);

    /**
     * The default timeout value (1000 msec). *
     */
    public static final int DEFAULT_TIMEOUT = 1000;
    /**
     * The default retry wait value (50 msec). *
     */
    public static final int DEFAULT_WAIT = 50;

    /**
     * The underlying data source. *
     */
    private DataSource dataSource;
    /**
     * The time limit for obtaining a connection.  If a connection is not obtained whithin this time, the last exception
     * will be re-thrown.
     */
    private long timeout;
    /**
     * The time to wait between retries. *
     */
    private long waitTime;
    /**
     * The number of timeouts. *
     */
    private int timeouts;
    /**
     * The number of exceptions. *
     */
    private int exceptions;

    /**
     * Empty (default) constructor.
     */
    public ReconnectingDataSource()
    {
        dataSource = null;
        timeout = DEFAULT_TIMEOUT;
        waitTime = DEFAULT_WAIT;
        initCounters();
    }

    public ReconnectingDataSource(DataSource dataSource, long timeout, long waitTime)
    {
        if (dataSource == null)
            throw new IllegalArgumentException("dataSource cannot be null!");
        if (timeout <= 0)
            throw new IllegalArgumentException("timeout must be > 0");
        if (waitTime <= 0)
            throw new IllegalArgumentException("waitTime must be > 0");
        this.dataSource = dataSource;
        this.timeout = timeout;
        this.waitTime = waitTime;
        initCounters();
    }

    /**
     * Returns the underlying data source.
     *
     * @return the underlying data source.
     */
    public DataSource getDataSource()
    {
        if (dataSource == null)
            throw new IllegalArgumentException("dataSource cannot be null!");
        synchronized (this)
        {
            return dataSource;
        }
    }

    /**
     * Sets the underlying data source.
     *
     * @param dataSource The data source.
     */
    public void setDataSource(DataSource dataSource)
    {
        synchronized (this)
        {
            this.dataSource = dataSource;
        }
    }

    /**
     * Returns the maximum time to wait for a connection, in milliseconds.
     * @return the maximum time to wait for a connection, in milliseconds.
     */
    public long getTimeout()
    {
        return timeout;
    }

    /**
     * Sets the maximum time to wait for a connection, in milliseconds.
     * @param timeout the maximum time to wait for a connection, in milliseconds.
     */
    public void setTimeout(long timeout)
    {
        if (timeout <= 0)
            throw new IllegalArgumentException("timeout must be > 0");
        this.timeout = timeout;
    }

    /**
     * Returns the retry wait time, in milliseconds.
     * @return the retry wait time, in milliseconds.
     */
    public long getWaitTime()
    {
        return waitTime;
    }

    /**
     * Sets the retry wait time, in milliseconds.
     * @param waitTime the retry wait time, in milliseconds.
     */
    public void setWaitTime(long waitTime)
    {
        if (waitTime <= 0)
            throw new IllegalArgumentException("waitTime must be > 0");
        this.waitTime = waitTime;
    }

    /**
     * Returns the number of timeouts so far.
     * @return the number of timeouts so far.
     */
    public int getTimeouts()
    {
        synchronized (this)
        {
            return timeouts;
        }
    }

    /**
     * Returns the number of exceptions handled so far.
     * @return the number of exceptions handled so far.
     */
    public int getExceptions()
    {
        synchronized (this)
        {
            return exceptions;
        }
    }

    // --- DataSource implementation ---

    public int getLoginTimeout() throws SQLException
    {
        return getDataSource().getLoginTimeout();
    }

    public void setLoginTimeout(int seconds) throws SQLException
    {
        getDataSource().setLoginTimeout(seconds);
    }

    public PrintWriter getLogWriter() throws SQLException
    {
        return getDataSource().getLogWriter();
    }

    public void setLogWriter(PrintWriter out) throws SQLException
    {
        getDataSource().setLogWriter(out);
    }

    public Connection getConnection() throws SQLException
    {
        return getConnection(new ConnectionCallback()
        {
            public Connection doGetConnection(DataSource dataSource) throws SQLException
            {
                return dataSource.getConnection();
            }
        });
    }

    public Connection getConnection(String username, String password) throws SQLException
    {
        // Copy the parameters into final local variables for the IoC callback implementation.
        // We may not want to alter the method prototype because it implements the
        // DataSource interface.
        final String u = username;
        final String p = password;
        return getConnection(new ConnectionCallback()
        {
            public Connection doGetConnection(DataSource dataSource) throws SQLException
            {
                return dataSource.getConnection(u, p);
            }
        });
    }

    // --- Implemnentation methods ---

    private void initCounters()
    {
        synchronized (this)
        {
            timeouts = 0;
            exceptions = 0;
        }
    }

    /**
     * IoC wrapper that retries the callback until a connection is made or the timeout expires.
     *
     * @param callback The IoC callback to the code that actually obtains the connection.
     * @return The connection
     * @throws SQLException if there was a problem.
     */
    private Connection getConnection(ConnectionCallback callback) throws SQLException
    {
        long startTime = System.currentTimeMillis();
        int attempts = 0;
        DataSource dataSource = getDataSource();
        if (dataSource == null)
            throw new SQLException("DataSource is null!  ReconnectingDataSource has not been properly initialized.");
        while (true)
        {
            try                         // Try to get a connection from the pool.
            {
                attempts++;
                Connection con = callback.doGetConnection(dataSource);
                if (con == null)
                    throw new SQLException("Null connnection returned!");
//                if (con.isClosed())
//                    throw new SQLException("Connection already closed!");
                if (attempts != 0)
                    log.info("Connection obtained after " + attempts + " attempt" + ((attempts > 1) ? "s" : ""));
                return con;
            }
            catch (SQLException sqle)       // The pool might be empty... retry if the timeout has not expired.
            {
                synchronized (this)
                {
                    exceptions++;
                }
                long elapsed = System.currentTimeMillis() - startTime;
                if (elapsed < (timeout))    // If the timeout has not expired, retry.
                {
                    log.warn("Retrying in " + waitTime + "msec, as a connection was not obtained due to: " + sqle.getMessage());
                    try
                    {
                        Thread.sleep(waitTime);
                    }
                    catch (InterruptedException ignore)
                    {
                    }
                    continue;               // Retry.
                }
                else
                {
                    log.error("No connections available, giving up after " + attempts + " attempts.", sqle);
                    synchronized (this)
                    {
                        timeouts++;
                    }
                    throw sqle;      // Re throw the ex0ception.
                }
            }
        } // while (this loops forever)
    }

    /**
     * IoC interface: Implementors will invoke what is necessary to get the connection.
     */
    interface ConnectionCallback
    {
        Connection doGetConnection(DataSource dataSource) throws SQLException;
    }
}
