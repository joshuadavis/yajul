package org.yajul.sql;

import org.apache.log4j.Logger;
import org.yajul.log.LogUtil;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

/**
 * Encapsulates common behavior for different types of database backed sets.
 * Assumes the target table has primary key that contains the
 * foreign key of the container object, and the value itself.
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Oct 14, 2003
 * Time: 6:54:03 AM
 */
public abstract class DatabaseSet
{
    private static Logger log = Logger.getLogger(DatabaseSet.class);


    private String targetTableName;
    private String keyColumnName;
    private String valueColumnName;
    private ConnectionHelper helper;

    /**
     * Creates a new DatabaseSet.
     * @param targetTableName The table name where the set will be persisted.
     * @param keyColumnName The key column, which will be used to determine the identity of the set.
     * @param valueColumnName The value column, which will be used to store the values of each set.
     * @param con The JDBC connection to use.
     */
    public DatabaseSet(String targetTableName, String keyColumnName, String valueColumnName, Connection con)
    {
        this.targetTableName = targetTableName;
        this.keyColumnName = keyColumnName;
        this.valueColumnName = valueColumnName;
        this.helper = new ConnectionHelper(con);
    }

    /**
     * Check the metadata to make sure that it is compatible with
     * the desired key/value types.
     */
    public abstract void checkMetadata() throws MetaDataException;

    /**
     * Adds the result from the result set to the list, performing any necessary data conversions.
     * @param list The list to add the result to.
     * @param rs The result set, positioned at the current result.
     * @throws SQLException if something goes wrong.
     */
    protected abstract void addElement(ArrayList list, ResultSet rs)
            throws SQLException;

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
        if (key == null)
        {
            throw new SQLException("Key cannot be null!");
        }
        if (key instanceof Integer)
        {
            Integer intKey = (Integer) key;
            preparedStatement.setInt(paramIndex, intKey.intValue());
        }
        else if (key instanceof Long)
        {
            Long longKey = (Long) key;
            preparedStatement.setLong(paramIndex, longKey.longValue());
        }
        else
            throw new SQLException("Unsupported key class: " + key.getClass().getName());
    }

    /**
     * Sets the value into the prepared statement by invoking the appropriate setXxxx(int,...) method.
     * @param preparedStatement The prepared statement.
     * @param paramIndex The parameter index.
     * @param value The value object.
     * @throws SQLException if something goes wrong.
     */
    protected abstract void setValue(PreparedStatement preparedStatement, int paramIndex, Object value)
        throws SQLException;

    /**
     * Selects all memebers of the set identified by the key into an ArrayList.
     * @param key The set id.
     * @return ArrayList - The members of the set specified by the key.
     */
    protected final ArrayList selectArrayList(Object key)
    {
        ResultSet rs = null;
        ArrayList list = new ArrayList();
        try
        {
            // SELECT foreignKeyFieldName,valueFieldName
            // FROM targetTableName
            // WHERE targetTableName.foreignKeyField = key
            String sql = "SELECT " + getValueColumnName()
                    + " FROM " + getTargetTableName()
                    + " WHERE " + getKeyColumnName() + " =  ? "
                    + " ORDER BY " + getValueColumnName();
            PreparedStatement ps = getPreparedStatement(sql);
            // Ask the implementation to set the key value into the prepared statement.
            setKey(ps,1,key);
            rs = ps.executeQuery();
            // Accumulate values and return as an array.
            int counter = 0;
            while (rs.next())
            {
                addElement(list, rs);
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
     * Deletes all values for the specified set.
     * @param key The key (the id of the set).
     * @throws SQLException if something goes wrong.
     */
    public final void delete(Object key) throws SQLException
    {
        // DELETE targetTableName WHERE primaryKeyFieldName = primaryKey
        String sql = "DELETE " + getTargetTableName()
                + " WHERE " + getKeyColumnName() + " =  ? ";
        PreparedStatement ps = getPreparedStatement(sql);
        setKey(ps,1,key);
        try
        {
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            LogUtil.unexpected(log,e);
        }
    }

    /**
     * Releases all resources held by this object.
     */
    public final void close()
    {
        helper.close();
        helper = null;
    }

    public final String getTargetTableName()
    {
        return targetTableName;
    }

    public final String getKeyColumnName()
    {
        return keyColumnName;
    }

    public final String getValueColumnName()
    {
        return valueColumnName;
    }

    protected final ColumnMetaData getColumnMetadata(String columnName)
            throws MetaDataException
    {
        String targetTableName = getTargetTableName();
        ColumnMetaData md = helper.getColumnMetaData(targetTableName,columnName);
        if (md == null)
            throw new MetaDataException(
                    "Column '" + columnName
                    + "' in table '"
                    + targetTableName
                    + "' not found!");
        return md;
    }

    /**
     * Performs updates that will result in the set being equal to the
     * supplied set.
     * @param key The id of the set to update.
     * @param input The new set of values.
     * @throws SQLException if something goes wrong.
     */
    protected void performUpdates(Object key, Set input) throws SQLException
    {
        // Put the database values in a set.
        Set reference = new HashSet(selectArrayList(key));

        if (log.isDebugEnabled())
            log.debug("update() : input.size() = " + input.size() + " reference.size() = " + reference.size());

        int rc = 0;
        int updates = 0;
        for (Iterator iterator = reference.iterator(); iterator.hasNext();)
        {
            Object o = iterator.next();
            // Exists in the database, but not in the new array: DELETE
            if (!input.contains(o))
            {
                // DELETE targetTableName WHERE foreignKeyFieldName = key AND valueFieldName = o
                String sql = "DELETE FROM " + getTargetTableName()
                        + " WHERE " + getKeyColumnName() + " =  ? "
                        + " AND " + getValueColumnName() + " =  ? ";
                rc = executeUpdate(sql, key, o);
                if (rc > 0)
                {
                    updates += rc;
                }
            }
        } // for

        for (Iterator iterator = input.iterator(); iterator.hasNext();)
        {
            Object o = iterator.next();
            // Exists in the new array, but not in the database: INSERT
            if (!reference.contains(o))
            {
                // INSERT foreignKeyFieldName,valueFieldName INTO targetTableName VALUES(key,o)
                String sql = "INSERT INTO " + getTargetTableName()
                        + " (" + getKeyColumnName()
                        + "," + getValueColumnName() + ")"
                        + " VALUES ( ? , ? )";
                rc = executeUpdate(sql, key, o);
                if (rc > 0)
                {
                    updates += rc;
                }
            }
        } // for
        if (log.isDebugEnabled())
            log.debug("update() : " + updates + " rows updated.");
    }

    private int executeUpdate(String sql, Object key, Object o) throws SQLException
    {
        PreparedStatement ps = getPreparedStatement(sql, key, o);
        int rc = ps.executeUpdate();
        return rc;
    }

    private PreparedStatement getPreparedStatement(String sql, Object key, Object value)
            throws SQLException
    {
        PreparedStatement ps = getPreparedStatement(sql);
        setKey(ps,1,key);
        setValue(ps,2,value);
        return ps;
    }

    private PreparedStatement getPreparedStatement(String sql)
    {
        return helper.getPreparedStatement(sql);
    }
}
