/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jul 4, 2002
 * Time: 3:15:57 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.yajul.empire;

import org.yajul.math.LinearMapper;

import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Font;
import java.awt.Point;

public class TerrainPanel extends JPanel
{
    private int cellSize;
    private TerrainModel model;

    public TerrainPanel(TerrainModel model,int cellSize)
    {
        Point panelSize = new Point(model.getWidth(),model.getHeight());
        this.cellSize = cellSize;
        panelSize.x *= cellSize;
        panelSize.y *= cellSize;
        setPreferredSize(new Dimension(panelSize.x,panelSize.y));
        this.model = model;
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        // Get the clip rectangle.
        Rectangle r = g.getClipBounds();
        // Draw the part of the world that is inside the clip bounds.
        float[][] values = model.getValues();
        int clipx = ( (r.x + r.width) / cellSize ) + 1;
        int clipy = ( (r.y + r.height) / cellSize ) + 1;
        int xmax = model.getWidth();
        int ymax = model.getHeight();
        int xlimit = Math.min(xmax-1, clipx);
        int ylimit = Math.min(ymax-1, clipy);
        int xstart = Math.max(0,(r.x / cellSize) - 1);
        int ystart = Math.max(0,(r.y / cellSize) - 1);
        float value;
        int x,y;
        Color c = null;
//        System.out.println("xstart = " + xstart + " xmax = " + xmax + " xlimit = " + xlimit);
//        System.out.println("ystart = " + ystart + " ymax = " + ymax + " ylimit = " + ylimit);
        for(x = xstart; x < xlimit ; x++)
        {
            for(y = ystart; y < ylimit ; y++)
            {
//                System.out.println("x = " + x + " y = " + y );
                value = values[x][y];
                c = new Color(value,value,value);
                g.setColor(c);
                g.fillRect(x * cellSize,y * cellSize,cellSize,cellSize);
            }
        }
    }
}
