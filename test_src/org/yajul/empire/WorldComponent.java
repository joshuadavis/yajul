/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jul 10, 2002
 * Time: 10:32:16 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.yajul.empire;

public abstract class WorldComponent
{
    /** The world that contains this cell. */
    private World world;

    public WorldComponent(World world)
    {
        this.world = world;
    }

    public World getWorld()
    {
        return world;
    }
}
