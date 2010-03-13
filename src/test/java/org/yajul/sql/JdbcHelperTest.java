package org.yajul.sql;

import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * Tests for JdbcHelper
 * <br>
 * User: josh
 * Date: Mar 10, 2010
 * Time: 6:27:46 PM
 */
public class JdbcHelperTest extends TestCase {
    private Connection con;

    private static final Logger log = LoggerFactory.getLogger(JdbcHelperTest.class);

    private static final String username = "sa";
    private static final String password = "";
    private static final String driverClassName = "org.hsqldb.jdbcDriver";
    private static final String driverProtocol = "jdbc:hsqldb:file:data/";
    private static final String driverOptions = "";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        String dbname = "test";
        con = JdbcHelper.getConnection(driverProtocol + dbname + driverOptions,username,password,driverClassName);
        DbTestHelper.dbSetUp(con,"org/yajul/sql/JdbcHelperTest.xml",null);
    }

    @Override
    protected void tearDown() throws Exception {
        JdbcHelper.close(con,null,null);
        super.tearDown();
    }

    public void testColumnTypes() throws Exception {
        Map<String,ColumnType> types = JdbcHelper.getColumnTypes(con);
        for (ColumnType type : types.values()) {
            assertNotNull(type.getJdbcType());
        }
    }


}
