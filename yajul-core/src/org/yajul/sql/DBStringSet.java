package org.yajul.sql;

import org.apache.log4j.Logger;
import org.yajul.log.LogUtil;
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
public class DBStringSet extends DatabaseSet
{
    /**
     * The logger for this class.
     */
    private static Logger log = LogUtil.getLogger(DBStringSet.class.getName());

    /**
     * Creates a new DBStringSet
     * @param targetTableName The name of the table that will contain the
     * sets of strings.
     * @param keyColumnName The key column, which identifies a particular set
     * of integers.
     * @param valueColumnName The value column, which contains the integer
     * values in the sets.
     * @param con The database connection.
     */
    public DBStringSet(
            String targetTableName,
            String keyColumnName,
            String valueColumnName,
            Connection con)
    {
        super(targetTableName, keyColumnName, valueColumnName, con);
        // Ensure that the value column type is compatible with 'int'.
        ColumnMetaData md = getColumnMetadata(valueColumnName);
        if (!md.isStringType())
            throw new IllegalArgumentException(
                    "The type of column '" + valueColumnName
                    + "' in table '"
                    + targetTableName
                    + "' is not compatible with the 'String' data type!");
    }

    /**
     * Reads all of the members of the set specified by the key.
     * @param key The key (id) of the set.
     * @return String[] - The strings in the set.
     */
    public String[] select(Object key)
    {
        ArrayList list = selectArrayList(key);
        return (String[])list.toArray(new String[list.size()]);
    }

    protected void addElement(ArrayList list, ResultSet rs) throws SQLException
    {
        list.add(rs.getString(1));
    }

    /**
     * Updates the set at the specified key to be the values specified in the
     * array.
     * @param key The key (a.k.a. set id).
     * @param values The new values for the set.
     * @throws SQLException If something went wrong.
     */
    public void update(Object key, String[] values) throws SQLException
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
     * Sets the key into the prepared statement by invoking the appropriate setXxxx(int,...) method.
     * @param preparedStatement The prepared statement.
     * @param paramIndex The parameter index.
     * @param key The key object.
     * @throws SQLException if something goes wrong.
     */
    protected void setKey(PreparedStatement preparedStatement, int paramIndex, Object key)
            throws SQLException
    {
        preparedStatement.setInt(paramIndex, ((Integer) key).intValue());
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
        preparedStatement.setString(paramIndex,(String) value);
    }
}
