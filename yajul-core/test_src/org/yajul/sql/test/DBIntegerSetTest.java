package org.yajul.sql.test;

import java.sql.Connection;
import java.sql.SQLException;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.log4j.Logger;
import org.yajul.log.LogUtil;
import org.yajul.sql.ConnectionHelper;
import org.yajul.sql.DBIntegerSet;

/**
 * TODO: Add javadoc
 * User: jdavis
 * Date: Aug 4, 2003
 * Time: 12:10:00 PM
 * @author jdavis
 */
public class DBIntegerSetTest extends TestCase
{
    /**
     * The logger for this class.
     */
    private static Logger log = LogUtil.getLogger(DBIntegerSetTest.class.getName());

    private Connection con = null;
    private static final String TABLE_NAME = "TEST_DBIS";
    private static final String KEY_COLUMN_NAME = "key_col";
    private static final String VALUE_COLUMN_NAME = "value_col";

    public DBIntegerSetTest(String name)
    {
        super(name);
    }

    /**
     * Performs any set up that is required by the test,
     * such as initializing instance variables of the test
     * case class, etc. Invoked before every test method.
     */
    protected void setUp()
    {
        try
        {
            con = HsqlDbTest.getTestConnection();
        }
        catch (ClassNotFoundException e)
        {
            LogUtil.unexpected(log,e);
        }
        catch (SQLException e)
        {
            LogUtil.unexpected(log,e);
        }
    }

    /**
     * Cleans up any state that needs to be undone after
     * the test has completed.
     */
    protected void tearDown()
    {
        try
        {
            con.close();
        }
        catch (SQLException e)
        {
            LogUtil.unexpected(log,e);
        }
        con = null;
    }

    /**
     * Perform basic select/insert/update/delete operations on
     * DBIntegerSet
     */
    public void testDBIntegerSet() throws Exception
    {
        ConnectionHelper helper = new ConnectionHelper(con);

        // Drop the table if it already exists.
        helper.dropTableIfExists(TABLE_NAME);

        // Create or re-create the table.
        helper.executeUpdate("CREATE TABLE " + TABLE_NAME + "( "
            + KEY_COLUMN_NAME + " INT NOT NULL, "
            + VALUE_COLUMN_NAME + " INT NOT NULL, "
            + " PRIMARY KEY ("
                + KEY_COLUMN_NAME + ", " + VALUE_COLUMN_NAME +") )");

        // Create a DBIntegerSet

        DBIntegerSet dbIntegerSet =
                new DBIntegerSet(
                        TABLE_NAME,
                        KEY_COLUMN_NAME,
                        VALUE_COLUMN_NAME,
                        con);
        Integer key1 = new Integer(1);
        Integer key2 = new Integer(2);

        int[] five = new int[] { 1, 2, 3, 4, 5 };
        int[] ten = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        int[] threeToSeven = new int[] {3, 4, 5, 6, 7  };
        int[] wackyDuplicates = new int[] { 3, 3, 3, 10 };

        dbIntegerSet.update(key1,five);
        dbIntegerSet.update(key2,five);
        dbIntegerSet.update(key1,ten);  // Should insert 6 -> 10
        dbIntegerSet.update(key1,five); // Should delete 6 -> 10
        dbIntegerSet.update(key1,threeToSeven);
        dbIntegerSet.update(key1,wackyDuplicates);

    }

    /**
     * Constructs a test suite for this test case, providing any required
     * Setup wrappers, or decorators as well.
     * @return Test - The test suite.
     */
    public static Test suite()
    {
        // Return the default test suite: No setup, all public methods with
        // no return value, no parameters, and names that begin with 'test'
        // are added to the suite.
        return new TestSuite(DBIntegerSetTest.class);
    }
}
