/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Aug 31, 2002
 * Time: 5:48:08 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.yajul.swing;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class LineGraphModel
{
    private Set listeners = new HashSet();
    private ArrayList points = new ArrayList();
    private Point[] pointArray;
    private Comparator comparator = new PointComparator();
    private Dimension size = new Dimension();

    public LineGraphModel(Dimension size)
    {
        this.size = size;
    }

    public Dimension getSize()
    {
        return size;
    }

    protected void broadcastChange(ChangeEvent e)
    {
        Iterator iter = listeners.iterator();
        ChangeListener listener = null;
        while (iter.hasNext())
        {
            listener = (ChangeListener) iter.next();
            listener.stateChanged(e);
        }
    }

    protected void add(Point p)
    {
        pointArray = null;  // Invalidate the array of points.
        points.add(p);
        broadcastChange(new ChangeEvent(this));
    }

    public void addChangeListener(ChangeListener listener)
    {
        listeners.add(listener);
    }

    public void removeChangeListener(ChangeListener listener)
    {
        listeners.remove(listener);
    }

    public void setSize(Dimension size)
    {
        this.size = size;
    }

    public void clear()
    {
//        System.out.println("clear()");
        points = new ArrayList();
        pointArray = null;
        broadcastChange(new ChangeEvent(this));
    }

    public Point[] getPointArray()
    {
//        System.out.println("points.size() = " + points.size());
        if (pointArray == null)
            pointArray = new Point[points.size()];
        if (points.size() > 0)
        {
            Collections.sort(points, comparator);
            pointArray = (Point[]) points.toArray(pointArray);
        }
        return pointArray;
    }

    private static class PointComparator implements Comparator
    {
        public int compare(Object o1, Object o2)
        {
            if ((o1 == null) || (o2 == null))
                throw new IllegalArgumentException("Objects cannot be null!");
            if (!((o1 instanceof Point) && (o2 instanceof Point)))
                throw new IllegalArgumentException("Object is not a point!");
            Point p1 = (Point) o1;
            Point p2 = (Point) o2;
            return p1.x - p2.x;
        }
    }
}
