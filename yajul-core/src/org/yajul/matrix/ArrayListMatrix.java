// $Id$
package org.yajul.matrix;

import java.util.ArrayList;

/**
 * Provides an orthogonal matrix collection backed by array lists.
 * @author josh Sep 4, 2004 3:00:57 PM
 */
public class ArrayListMatrix extends AbstractMatrix implements Matrix
{
    private ArrayList arrayList;

    public ArrayListMatrix()
    {
        arrayList = new ArrayList();
    }

    public void setSize(int[] sizes)
    {
        if (sizes.length > this.sizes.length)
        {
            // For each coordinate in the last dimension (sizes.length - 1), move the element into an array list.
            int[] coord = new int[this.sizes.length];
            do
            {
                ArrayList list = getList(coord);
                for (int j = 0; j < list.size() ; j++)
                {
                    Object o = list.get(j);
                    ArrayList newList = new ArrayList();
                    newList.add(o);
                    list.set(j,newList);
                }
            } while (MatrixUtil.increment(coord,this.sizes));
        }
        super.setSize(sizes);
    }

    public void put(int[] coords,Object o)
    {
        beforePut(coords);
        ArrayList list = getList(coords);
        int coord = coords[coords.length - 1];
        if (list.size() == coord)
            list.add(o);
        else if (list.size() < coord)
        {
            for(int i = list.size() ; i < coord ; i++)
                list.add(null);
            list.add(o);
        }
        else
            list.set(coord, o);
    }

    public Object get(int[] coords)
    {
        ArrayList list = getList(coords);
        int coord = coords[coords.length - 1];
        if (list.size() <= coord)
            return null;
        else
            return list.get(coord);
    }

    private ArrayList getList(int[] coords)
    {
        if (!canGrow)
            checkCoords(coords);

        ArrayList list = arrayList;
        int limit = coords.length - 1;
        for (int i = 0; i < limit ; i++)
        {
            int coord = coords[i];
            if (list.size() == coord)
                list.add(new ArrayList(sizes[i]));
            else // if (list.size() <= coord)
            {
                for (int j = list.size() ; j <= coord ; j++)
                    list.add(new ArrayList(sizes[i]));
            }
            list = (ArrayList) list.get(coord);
        }
        return list;
    }

}
