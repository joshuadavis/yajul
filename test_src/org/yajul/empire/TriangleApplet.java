/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Sep 2, 2002
 * Time: 12:36:18 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.yajul.empire;

import org.yajul.swing.ApplicationApplet;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

public class TriangleApplet extends ApplicationApplet
{
    private Dimension preferredSize = new Dimension(640, 480);

    class TrianglePanel extends JPanel
    {
        protected static final Color BLACK = new Color(0, 0, 0);
        protected static final Color BLUE = new Color(0, 0, 255);
        protected static final Color RED = new Color(255, 0, 0);

        private Point point;
        private Dimension size;
        private int circleSize = 2;
        private int circleOffset = circleSize / 2;
        private int threshold = 30;
        private boolean full = false;

        public TrianglePanel(int width, int height)
        {
            size = new Dimension(width, height);
            setPreferredSize(size);
            setMaximumSize(size);
            setMinimumSize(size);
            point = new Point(width / 2, height / 2);
        }

        /**
         * Calls the UI delegate's paint method, if the UI delegate
         * is non-<code>null</code>.  We pass the delegate a copy of the
         * <code>Graphics</code> object to protect the rest of the
         * paint code from irrevocable changes
         * (for example, <code>Graphics.translate</code>).
         * <p>
         * If you override this in a subclass you should not make permanent
         * changes to the passed in <code>Graphics</code>. For example, you
         * should not alter the clip <code>Rectangle</code> or modify the
         * transform. If you need to do these operations you may find it
         * easier to create a new <code>Graphics</code> from the passed in
         * <code>Graphics</code> and manipulate it. Further, if you do not
         * invoker super's implementation you must honor the opaque property,
         * that is
         * if this component is opaque, you must completely fill in the background
         * in a non-opaque color. If you do not honor the opaque property you
         * will likely see visual artifacts.
         *
         * @param g the <code>Graphics</code> object to protect
         * @see #paint
         * @see ComponentUI
         */
        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);

            int circleOffset = circleSize / 2;
            // Draw a cirle around the main point.
            g.drawOval(point.x - circleOffset, point.y - circleOffset, circleSize, circleSize);
            // Select the 'a' point.
            Point a = new Point(point.x, point.y);
            Point b = new Point(size.width, size.height);
            Point c = new Point(0, size.height);
            Point d = new Point(0,0);
            Point e = new Point(size.width,0);

            g.drawLine(a.x, a.y, b.x, b.y);
            g.drawLine(b.x, b.y, c.x, c.y);
            g.drawLine(c.x, c.y, a.x, a.y);

            tesselate(g, a.x, a.y, b.x, b.y, c.x, c.y);
            if (full)
            {
                tesselate(g, a.x, a.y, c.x, c.y, d.x, d.y);
                tesselate(g, a.x, a.y, d.x, d.y, e.x, e.y);
                tesselate(g, a.x, a.y, e.x, e.y, b.x, b.y);
            }
        }

        private void tesselate(Graphics g, int ax, int ay, int bx, int by, int cx, int cy)
        {
            int dx = 0;
            int dy = 0;
            int ncx = 0;
            int ncy = 0;
            int length = 0;
            g.setColor(BLACK);
            g.drawLine(ax, ay, bx, by);
            g.drawLine(bx, by, cx, cy);

            // Draw a red dot on A.
            g.setColor(RED);
            g.drawOval(ax - circleOffset, ay - circleOffset, circleSize, circleSize);


            // Midpoint on line BC becomes the new C for the triangle
            dx = cx - bx;
            dy = cy - by;

            length = (int) Math.sqrt((double) (dx * dx + dy * dy));
            if ((length < threshold))
                return;

            ncx = bx + (dx / 2);
            ncy = by + (dy / 2);
            g.setColor(BLUE);
            g.drawLine(ncx, ncy, ax, ay);

            tesselate(g, ncx, ncy, cx, cy, ax, ay);
            tesselate(g, ncx, ncy, ax, ay, bx, by);
        }
    }

    /**
     * Called by the browser or applet viewer to inform
     * this applet that it has been loaded into the system. It is always
     * called before the first time that the <code>start</code> method is
     * called.
     * <p>
     * A subclass of <code>Applet</code> should override this method if
     * it has initialization to perform. For example, an applet with
     * threads would use the <code>init</code> method to create the
     * threads and the <code>destroy</code> method to kill them.
     * <p>
     * The implementation of this method provided by the
     * <code>Applet</code> class does nothing.
     *
     * @see     java.applet.Applet#destroy()
     * @see     java.applet.Applet#start()
     * @see     java.applet.Applet#stop()
     */
    public void init()
    {
        super.init();
        Container contentPane = getContentPane();
        BorderLayout bl = new BorderLayout();
        contentPane.setLayout(bl);
        TrianglePanel trianglePanel = new TrianglePanel((int) preferredSize.getWidth(), (int) preferredSize.getHeight());
        contentPane.add(trianglePanel, BorderLayout.CENTER);
    }

    public static void main(String[] args)
    {
        TriangleApplet applet = new TriangleApplet();
        applet.startAsApplication("Triangle");
    }
}
