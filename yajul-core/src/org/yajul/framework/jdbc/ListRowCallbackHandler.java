// $Id$
package org.yajul.framework.jdbc;

import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.LinkedList;

/**
 *
 * @author josh Mar 21, 2004 8:51:28 AM
 */
public abstract class ListRowCallbackHandler implements RowCallbackHandler
{
    private int rows = 0;
    private List list = new LinkedList();

    public void processRow(ResultSet resultSet) throws SQLException
    {
        rows++;
        processRow(resultSet,list);
    }

    public int getRows()
    {
        return rows;
    }

    public List getList()
    {
        return list;
    }

    public abstract void processRow(ResultSet resultSet, List list) throws SQLException;
}
