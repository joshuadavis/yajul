/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jul 10, 2002
 * Time: 8:33:19 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.yajul.empire;

import java.util.ArrayList;
import java.util.List;

public class Cell extends WorldComponent
{
    /** The units currently in this cell. */
    private ArrayList units;
    private int x;
    private int y;
    private int height;

    Cell(World world,int x,int y)
    {
        super(world);
        this.x = x;
        this.y = y;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    /**
     * Returns the list of units at this cell.
     * @return List - The list of units.
     */
    public List getUnitList()
    {
        return units;
    }

    public void remove(Unit u)
    {
        units.remove(u);
    }

    public void add(Unit u)
    {
        units.add(u);
    }

    public int getHeight()
    {
        return height;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }
}
