/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Aug 31, 2002
 * Time: 6:10:59 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.yajul.swing;

import javax.swing.JPanel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Color;
import java.awt.Dimension;

public class LineGraphPanel extends JPanel implements ChangeListener
{
    protected static final Color BLACK = new Color(0,0,0);
    protected static final Color BLUE = new Color(0,0,255);
    private LineGraphModel model;
    private Dimension markerSize;
    private Color lineColor = BLUE;
    private Color markerColor = BLACK;

    public LineGraphPanel(LineGraphModel model)
    {
        this.model = model;
        setPreferredSize(model.getSize());
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        Point[] points = getModel().getPointArray();
        Point next = null;
        Point prev = null;
        g.setColor(lineColor);
//        System.out.println("paintComponent() : points.length = " + points.length);
        for (int i = 0; i < points.length ; i++)
        {
            next = points[i];
            if (prev != null)
            {
                // Draw the line from the previous point to the next.
                drawLine(g, prev, next);
                // If the next point is not the last, draw a marker.
                if ((i+1) < points.length)
                {
                    drawMarker(g, next);
                    g.setColor(lineColor);
                }
            }
            prev = next;
        }

    }

    protected void drawMarker(Graphics g, Point next)
    {
        g.setColor(markerColor);
        GraphicsUtil.drawCenteredRect(g,next.x,next.y,getMarkerSize().width,getMarkerSize().height);
    }

    protected void drawLine(Graphics g, Point prev, Point next)
    {
        g.drawLine(prev.x,prev.y,next.x,next.y);
    }

    public LineGraphModel getModel()
    {
        return model;
    }

    /**
     * Invoked when the target of the listener has changed its state.
     *
     * @param e  a ChangeEvent object
     */
    public void stateChanged(ChangeEvent e)
    {
        repaint();
    }

    public void setModel(LineGraphModel model)
    {
        this.model = model;
    }

    public Dimension getMarkerSize()
    {
        return markerSize;
    }

    public void setMarkerSize(Dimension markerSize)
    {
        this.markerSize = markerSize;
    }
}
