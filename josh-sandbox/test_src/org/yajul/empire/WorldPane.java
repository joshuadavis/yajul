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

public class WorldPane extends JPanel
{
    private World world = null;
    private Font font = null;
    private FontMetrics fm = null;
    private int pad = 1;
    private int lineWidth = 1;
    private int refWidth;
    private int width;
    private int height;
    private int worldWidth;
    private int worldHeight;
    private Dimension preferredViewport;

    public WorldPane(World w)
    {
        world = w;
        font = new Font("SansSerif",Font.PLAIN,10);
        // The size of each 'cell' is:
        fm = getFontMetrics(font);
        refWidth = fm.charWidth('W');
        width = refWidth + pad;
        height = fm.getAscent() + pad;
        worldHeight = world.getYsize() * (height + lineWidth);
        worldWidth = world.getXsize() * (width + lineWidth);
        setPreferredSize(new Dimension(worldWidth,worldHeight));
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        // Get the clip rectangle.
        Rectangle r = g.getClipBounds();
/*
        System.out.println("Clip = " + r.toString());
*/
        // Draw the part of the world that is inside the clip bounds.
        Dimension s = getSize();    // Get the size of the frame.

        g.setFont(font);

        // Draw a vertical line every 'width + linewidth' for every cell in the world.
        char[] data = new char[2];

        int x = 0;
        int y = 0;
        Color red = new Color(128, 0, 0);
        Color black = new Color(0, 0, 0);
        Color green = new Color(18, 230, 10);
        Color burnt = new Color(100, 100, 10);
        Color background = new Color(0,0,0);

/*
        g.setColor(red);
        g.drawLine(0, 0, 10, 0);
        g.drawLine(0, 0, 0, 10);
        g.drawLine(1, 1, 10, 1);
        g.drawLine(1, 1, 1, 10);
*/
        // Convert the clipping rectangle into 'world' coordinates (inclusive).
        int xfactor = width + lineWidth;
        int yfactor = height + lineWidth;
        int minwx = (int)(Math.floor(r.getMinX() / xfactor));
        int minwy = (int)(Math.floor(r.getMinY() / yfactor));
        int maxwx = (int)(Math.ceil(r.getMaxX() / xfactor));
        int maxwy = (int)(Math.ceil(r.getMaxY() / yfactor));
/*
        System.out.println("minwx = " + minwx + " minwy = " + minwy +
                " maxwx = " + maxwx + " maxwy = " + maxwy);
*/
        g.setColor(burnt);
        int cw = 0;
        Cell cell = null;

        float u = 255;
        float v = 0;
        float q = world.getMaxHeight();
        float rr = world.getMinHeight();
        LinearMapper mapper = new LinearMapper(u,v,q,rr);

        int c = 0;
        for (int wx = minwx; wx <= maxwx; wx++)
        {
            x = wx * (width + lineWidth);
            g.drawLine(x, (int)r.getMinY(), x, (int)r.getMaxY());
            for (int wy = minwy; wy <= maxwy; wy++)
            {
                y = wy * (height + lineWidth);
                g.drawLine((int)r.getMinX(), y, (int)r.getMaxX(), y);
                if (wx < world.getXsize() && wy < world.getYsize())
                {
                    cell = world.getCell(wx,wy);
                    c =  (int)mapper.convert((float)cell.getHeight());
                    background = new Color(c,c,c);
                    g.setColor(background);
                    g.fillRect(x + lineWidth, y + lineWidth, width, height);
                    data[0] = getUnitChar(wx,wy);
                    g.setColor(green);
                    cw = fm.charWidth(data[0]);
                    int diff = refWidth - cw;
                    int cx = x + pad + (diff / 2);
                    g.drawChars(data, 0, 1, cx, y + height - pad);
                    g.setColor(burnt);
                }
            }
        } // for
    }

    private char getUnitChar(int wx,int wy)
    {
        Unit u = world.getTopUnit(wx,wy);
        if (u == null)
        {
            // TODO: Have the cell get the char.
            // Return the terrain character.
            if (world.getCell(wx,wy).getHeight() > 0)
                return '+';
            else
                return '.';
        }
        else
            return u.getUnitType().getCharacter();
    }
}
