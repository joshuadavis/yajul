/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jul 4, 2002
 * Time: 10:16:22 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.yajul.empire;

public class Unit
{
    private Cell cell;
    private int id;
    private UnitType unitType;

    public Unit(Cell c,UnitType ut)
    {
        this.cell = c;
        this.unitType = ut;
        this.id = cell.getWorld().nextUnitId();
        cell.getWorld().addUnit(this);
    }

    public int getId()
    {
        return id;
    }

    public UnitType getUnitType()
    {
        return unitType;
    }

    public Cell getCell()
    {
        return cell;
    }

    public int getX()
    {
        return cell.getX();
    }

    public int getY()
    {
        return cell.getY();
    }
}
