// $Id$
package org.yajul.framework.jdbc;

import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * A prepared statement creator that knows how to bind simple parameters into the prepared statement.
 * Supported paramater types include:
 * <ul>
 * <li>java.lang.String</li>
 * <li>java.lang.Integer</li>
 * <li>java.lang.Long</li>
 * <li>java.sql.Date</li>
 * <li>java.sql.Timestamp</li>
 * <li>java.util.Date - as java.sql.Timestamp</li>
 * </ul>
 * 
 * @author josh Mar 21, 2004 9:05:28 AM
 */
public class SimplePreparedStatementCreator implements PreparedStatementCreator
{
    private String sql;
    private Object[] args;

    public SimplePreparedStatementCreator(String sql, Object[] args)
    {
        this.sql = sql;
        this.args = args;
    }

    public Object[] getArgs()
    {
        return args;
    }

    public void setArgs(Object[] args)
    {
        this.args = args;
    }

    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException
    {
        PreparedStatement ps = connection.prepareStatement(sql);
        for (int i = 0; i < args.length; i++)
        {
            Object arg = args[i];
            int index = i+1;
            if (arg instanceof String)
            {
                String s = (String) arg;
                ps.setString(index,s);
            }
            else if (arg instanceof Integer)
            {
                Integer integer = (Integer) arg;
                ps.setInt(index,integer.intValue());
            }
            else if (arg instanceof Long)
            {
                Long aLong = (Long) arg;
                ps.setLong(index,aLong.longValue());
            }
            else if (arg instanceof Date)
            {
                Date date = (Date)arg;
                ps.setDate(index,date);
            }
            else if (arg instanceof Timestamp)
            {
                Timestamp timestamp = (Timestamp) arg;
                ps.setTimestamp(index,timestamp);
            }
            else if (arg instanceof java.util.Date)
            {
                java.util.Date date = (java.util.Date)arg;
                ps.setTimestamp(index,new Timestamp(date.getTime()));
            }
        }
        return ps;
    }
}
