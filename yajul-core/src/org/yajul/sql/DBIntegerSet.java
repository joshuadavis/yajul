package org.yajul.sql;

import org.apache.log4j.Logger;
import org.yajul.util.ArrayUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Manages a set of sets of integers stored in a relational database.
 * User: jdavis
 * Date: Aug 1, 2003
 * Time: 5:14:45 PM
 * @author jdavis
 */
public class DBIntegerSet extends DatabaseSet
{
    /**
     * The logger for this class.
     */
    private static Logger log = Logger.getLogger(DBIntegerSet.class.getName());

    // Assumes the target table has primary key that contains the
    // foreign key of the container object, and the value itself.

    /**
     * Creates a new DBInteger set
     * @param targetTableName The name of the table that will contain the
     * sets of integers.
     * @param keyColumnName The key column, which identifies a particular set
     * of integers.
     * @param valueColumnName The value column, which contains the integer
     * values in the sets.
     * @param con The database connection.
     */
    public DBIntegerSet(
            String targetTableName,
            String keyColumnName,
            String valueColumnName,
            Connection con)
    {
        super(targetTableName, keyColumnName, valueColumnName, con);
    }

    /**
     * Reads all of the members of the set specified by the key.
     * @param key The key (id) of the set.
     * @return int[] - The integers in the set.
     */
    public int[] select(Object key) throws SQLException
    {
        ArrayList list = selectArrayList(key);
        return ArrayUtil.toIntArray(list);
    }

    protected void addElement(ArrayList list, ResultSet rs) throws SQLException
    {
        list.add(new Integer(rs.getInt(1)));
    }

    /**
     * Check the metadata to make sure that it is compatible with
     * the desired key/value types.
     */
    public void checkMetadata() throws MetaDataException
    {
        // Ensure that the value column type is compatible with 'int'.
        ColumnMetaData md = getColumnMetadata(getValueColumnName());
        if (!md.isIntType())
            throw new MetaDataException(
                    "The type of column '" + getValueColumnName()
                    + "' in table '"
                    + getTargetTableName()
                    + "' is not compatible with the 'int' data type!");
    }

    /**
     * Updates the set at the specified key to be the values specified in the
     * array.
     * @param key The key (a.k.a. set id).
     * @param values The new values for the set.
     * @throws SQLException If something went wrong.
     */
    public void update(Object key, int[] values) throws SQLException
    {
        // Put the input values in a set.
        Set input = ArrayUtil.addToSet(values, new HashSet());

        if (input.size() != values.length)
        {
            // Issue a warning.
            log.warn("update() : input contains duplicate values!");
        }
        performUpdates(key, input);
    }

    /**
     * Sets the value into the prepared statement by invoking the appropriate setXxxx(int,...) method.
     * @param preparedStatement The prepared statement.
     * @param paramIndex The parameter index.
     * @param value The value object.
     * @throws SQLException if something goes wrong.
     */
    protected void setValue(PreparedStatement preparedStatement, int paramIndex, Object value)
            throws SQLException
    {
        preparedStatement.setInt(paramIndex, ((Integer) value).intValue());
    }
}
