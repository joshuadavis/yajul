package org.yajul.sql.test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

/**
 * Tests the embedded HSQL database that the other JDBC facilities will use.
 * User: jdavis
 * Date: Aug 1, 2003
 * Time: 4:56:59 PM
 * @author jdavis
 */
public class HsqlDbTest extends TestCase
{
    /**
     * The logger for this class.
     */
    private static Logger log = Logger.getLogger(HsqlDbTest.class.getName());

    public HsqlDbTest(String s)
    {
        super(s);
    }

    public void testConnect() throws Exception
    {
        Connection con = getTestConnection();
        con.close();
    }

    public static Connection getTestConnection()
            throws ClassNotFoundException, SQLException
    {
        // Get the directory where HSQLDB database files will be placed.
        // Defaults to the current directory.
        String dbdir = System.getProperty("org.yajul.sql.test.dbdir",
                "./build/databases");

        // Get the name of the test database.   Defaults to 'test'.
        String dbname = System.getProperty("org.yajul.sql.test.dbname","test");

        String dburl = "jdbc:hsqldb:"
                + new File(dbdir).getAbsolutePath() + File.separator + dbname;


        if (log.isDebugEnabled())
            log.debug("getTestConnection() : dburl = "
                    + dburl + " connecting...");

        Connection con = connect(dburl);

        if (log.isDebugEnabled())
            log.debug("getTestConnection() : connected. dburl = " + dburl);

        return con;
    }

    private static Connection connect(String dburl)
            throws ClassNotFoundException, SQLException
    {
        // Load the HSQL Database Engine JDBC driver
        // hsqldb.jar should be in the class path or made part of the
        // current jar
        Class.forName("org.hsqldb.jdbcDriver");

        // connect to the database.   This will load the db files and start the
        // database if it is not alread running.
        // db_file_name_prefix is used to open or create files that
        // holds the state of the db.
        // It can contain directory names relative to the
        // current working directory
        Connection con = null;
        con = DriverManager.getConnection(dburl,                    // filenames
                                           "sa",                    // username
                                           "");                     // password
        return con;
    }
}
