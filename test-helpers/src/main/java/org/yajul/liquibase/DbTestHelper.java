package org.yajul.liquibase;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;

import java.sql.Connection;
import java.util.logging.Logger;

/**
 * Helper methods for database tests.
 * <br>
 * User: josh
 * Date: Mar 10, 2010
 * Time: 6:29:40 PM
 */
public class DbTestHelper {
    private static final Logger log = Logger.getLogger(DbTestHelper.class.getName());

    public static void dbSetUp(Connection connection,String schemaFile,String contexts) throws Exception {
        DatabaseFactory factory = DatabaseFactory.getInstance();
        Database db = factory.findCorrectDatabaseImplementation(new JdbcConnection(connection));
        ResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor();
        Liquibase lb = new Liquibase(schemaFile, resourceAccessor, db);
        log.info("Dropping all tables...");
        lb.dropAll();
        log.info("Validating change sets...");
        lb.validate();
        log.info("Applying change sets...");
        lb.update(contexts);
        log.info("Finished.");
    }
}
