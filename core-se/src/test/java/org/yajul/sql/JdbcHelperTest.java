package org.yajul.sql;

import org.junit.*;
import org.yajul.liquibase.DbTestHelper;

import java.sql.Connection;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Tests for JdbcHelper
 * <br>
 * User: josh
 * Date: Mar 10, 2010
 * Time: 6:27:46 PM
 */
public class JdbcHelperTest {
    private static final Logger log = Logger.getLogger(JdbcHelperTest.class.getName());

    private Connection con;


    private static final String username = "sa";
    private static final String password = "";
    private static final String driverClassName = "org.hsqldb.jdbcDriver";
    private static final String driverProtocol = "jdbc:hsqldb:file:data/";
    private static final String driverOptions = "";


    @Before
    public void setUp() throws Exception {
        String dbname = "test";
        con = JdbcHelper.getConnection(driverProtocol + dbname + driverOptions,username,password,driverClassName);
        DbTestHelper.dbSetUp(con, "org/yajul/sql/JdbcHelperTest.xml", null);
    }

    @After
    public void tearDown() throws Exception {
        JdbcHelper.close(con, null, null);
    }

    @Test
    public void testColumnTypes() throws Exception {
        Map<String,ColumnType> types = JdbcHelper.getColumnTypes(con);
        for (ColumnType type : types.values()) {
            Assert.assertNotNull(type.getJdbcType());
        }
    }


}
