/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Aug 3, 2002
 * Time: 8:04:02 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.yajul.empire;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Arrays;

public class TerrainModel
{
    private float[][] values;
    private Point size;

    public TerrainModel(int width,int height)
    {
        values = new float[width][height];
        for(int x = 0; x < width ; x++)
            Arrays.fill(values[x],(float)0.5);
        size = new Point(width,height);
    }

    public float[][] getValues()
    {
        return values;
    }

    public int getHeight()
    {
        return size.y;
    }

    public int getWidth()
    {
        return size.x;
    }


}
