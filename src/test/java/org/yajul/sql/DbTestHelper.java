package org.yajul.sql;

import liquibase.ClassLoaderFileOpener;
import liquibase.FileOpener;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

/**
 * Helper methods for database tests.
 * <br>
 * User: josh
 * Date: Mar 10, 2010
 * Time: 6:29:40 PM
 */
public class DbTestHelper {
    private static final Logger log = LoggerFactory.getLogger(DbTestHelper.class);

    public static void dbSetUp(Connection connection,String schemaFile,String contexts) throws Exception {
        DatabaseFactory factory = DatabaseFactory.getInstance();
        Database db = factory.findCorrectDatabaseImplementation(connection);
        FileOpener opener = new ClassLoaderFileOpener();
        Liquibase lb = new Liquibase(schemaFile, opener, db);
        log.info("Dropping all tables...");
        lb.dropAll();
        log.info("Validating change sets...");
        lb.validate();
        log.info("Applying change sets...");
        lb.update(contexts);
        log.info("Finished.");
    }
}
