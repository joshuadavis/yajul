package org.yajul.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yajul.log.LogUtil;
import org.yajul.util.ArrayUtil;

/**
 * Manages a set of sets of integers stored in a relational database.
 * User: jdavis
 * Date: Aug 1, 2003
 * Time: 5:14:45 PM
 * @author jdavis
 */
public class DBIntegerSet
{
    /**
     * The logger for this class.
     */
    private static Logger log = LogUtil.getLogger(DBIntegerSet.class.getName());

    // Assumes the target table has primary key that contains the
    // foreign key of the container object, and the value itself.

    private String targetTableName;
    private String keyColumnName;
    private String valueColumnName;
    private ConnectionHelper helper;

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
        this.targetTableName = targetTableName;
        this.keyColumnName = keyColumnName;
        this.valueColumnName = valueColumnName;
        this.helper = new ConnectionHelper(con);
        // Ensure that the value column type is compatible with 'int'.
        ColumnMetaData md = helper.getColumnMetaData(targetTableName,valueColumnName);
        if (!md.isIntType())
           throw new IllegalArgumentException(
                   "The type of column '" + valueColumnName
                   + "' in table '"
                   + targetTableName
                   + "' is not compatible with the 'int' data type!");
        // TODO: Determine the type of the key column.
    }

    /**
     * Reads all of the members of the set specified by the key.
     * @param key The key (id) of the set.
     * @return int[] - The integers in the set.
     */
    public int[] select(Object key)
    {
        ArrayList list = selectArrayList(key);
        int[] rv = new int[list.size()];
        Iterator iterator = list.iterator();
        for (int i = 0; i < rv.length; i++)
        {
            Integer integer = (Integer) iterator.next();
            rv[i] = integer.intValue();
        }
        return rv;
    }

    private ArrayList selectArrayList(Object key)
    {
        ResultSet rs = null;
        ArrayList list = new ArrayList();
        try
        {
            // SELECT foreignKeyFieldName,valueFieldName
            // FROM targetTableName
            // WHERE targetTableName.foreignKeyField = key
            // TODO: For performance, make this a prepared statement.
            String sql = "SELECT " + valueColumnName
                    + " FROM " + targetTableName
                    + " WHERE " + keyColumnName + " = " + key
                    + " ORDER BY " + valueColumnName;
            rs = helper.executeQuery(sql);
            // Accumulate values and return as an array.
            int counter = 0;
            while (rs.next())
            {
                list.add(new Integer(rs.getInt(1)));
                counter++;
            }
            if (log.isDebugEnabled())
                log.debug("selectArrayList() : " + counter + " values retrieved.");
        }
        catch (SQLException e)
        {
            if (log.isDebugEnabled())
                log.debug("selectArrayList() : Unexpected exception "
                        + e.getMessage(), e);
        }
        finally
        {
            try
            {
                if (rs != null) rs.close();
            }
            catch (SQLException ignore)
            {
            }
        }
        return list;
    }

    /**
     * Updates the set at the specified key to be the values specified in the
     * array.
     * @param key The key (a.k.a. set id).
     * @param values The new values for the set.
     */
    public void update(Object key, int[] values)
    {
        // Put the input values in a set.
        Set input = ArrayUtil.addToSet(values, new HashSet());

        if (input.size() != values.length)
        {
            // Issue a warning.
            log.warn("update() : input contains duplicate values!");
        }

        // Put the database values in a set.
        Set reference = new HashSet(selectArrayList(key));

        if (log.isDebugEnabled())
            log.debug("update() : input.size() = " + input.size() + " reference.size() = " + reference.size());

        int rc = 0;
        int updates = 0;
        for (Iterator iterator = reference.iterator(); iterator.hasNext();)
        {
            Integer i = (Integer) iterator.next();
            // Exists in the database, but not in the new array: DELETE
            if (!input.contains(i))
            {
                // DELETE targetTableName WHERE foreignKeyFieldName = key AND valueFieldName = i
                String sql = "DELETE FROM " + targetTableName
                        + " WHERE " + keyColumnName + " =  " + key
                        + " AND " + valueColumnName + " = " + i;
                rc = helper.executeUpdate(sql);
                if (rc > 0)
                {
                    updates += rc;
                }
            }
        } // for

        for (Iterator iterator = input.iterator(); iterator.hasNext();)
        {
            Integer i = (Integer) iterator.next();
            // Exists in the new array, but not in the database: INSERT
            if (!reference.contains(i))
            {
                // INSERT foreignKeyFieldName,valueFieldName INTO targetTableName VALUES(key,i)
                String sql = "INSERT INTO " + targetTableName
                        + " (" + keyColumnName
                        + "," + valueColumnName + ")"
                        + " VALUES (" + key + "," + i + ")";
                rc = helper.executeUpdate(sql);
                if (rc > 0)
                {
                    updates += rc;
                }
            }
        } // for
        if (log.isDebugEnabled())
            log.debug("update() : " + updates + " rows updated.");
    }

    /**
     * Deletes all values for the specified set.
     * @param key The key (the id of the set).
     */
    public void delete(Object key)
    {
        // DELETE targetTableName WHERE primaryKeyFieldName = primaryKey
        String sql = "DELETE " + targetTableName
                + " WHERE " + keyColumnName + " =  " + key;
        helper.executeUpdate(sql);
    }

    /**
     * Releases all resources held by this object.
     */
    public void close()
    {
        helper.close();
        helper = null;
    }
}
