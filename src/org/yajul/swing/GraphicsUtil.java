/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Aug 31, 2002
 * Time: 2:21:22 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.yajul.swing;

import java.awt.Graphics;
import java.awt.Point;

/**
 * Utility functions for drawing with Graphics.
 */
public class GraphicsUtil
{
    /**
     * Draws a box around a point.
     */
    public final static void drawCenteredRect(Graphics g,int x,int y,int width,int height)
    {
        int dx = width / 2;
        int dy = height / 2;
        g.drawRect(x - dx,y - dy,width,height);
    }

    public static final Point midpoint(Point u, Point v)
    {
        int dx = v.x - u.x;                             // The change in x.
        int dy = v.y - u.y;                             // The change in y.
        return new Point(
                u.x + (dx / 2),                         // Add half the change.
                u.y + (dy / 2));                        // Add half the change.
    }

}
