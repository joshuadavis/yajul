/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jul 28, 2002
 * Time: 7:11:31 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.yajul.empire;

import org.yajul.swing.ApplicationApplet;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.MouseInputListener;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class TerrainApplet extends ApplicationApplet
{
    private Dimension preferredSize = new Dimension(640, 480);
    private JMenuBar bar = null;
    private TerrainPanel terrainPane = null;

    class MouseMonitor implements MouseInputListener
    {
        public boolean mouseDown = false;

        public void mouseClicked(MouseEvent e)
        {
        }

        public void mouseEntered(MouseEvent e)
        {
        }

        public void mouseExited(MouseEvent e)
        {
        }

        public void mouseReleased(MouseEvent e)
        {
            System.out.println(e.toString());
        }

        public void mousePressed(MouseEvent e)
        {
            System.out.println(e.toString());
        }

        public void mouseDragged(MouseEvent e)
        {

        }

        public void mouseMoved(MouseEvent e)
        {
        }
    }

    public TerrainApplet() throws HeadlessException
    {
        super();
    }

    public Dimension getPreferredSize()
    {
        return preferredSize;
    }

    public void init()
    {
        Container contentPane = getContentPane();
        BorderLayout bl = new BorderLayout();
        contentPane.setLayout(bl);

        JMenu file = new JMenu("File");
        file.setMnemonic('f');
        JMenuItem exit = new JMenuItem("Exit");
        exit.setMnemonic('x');
        exit.addActionListener(
                new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        System.out.println("event = " + e.toString());
                    }
                }
        );
        file.add(exit);
        bar = new JMenuBar();
        bar.add(file);
        // setJMenuBar(bar);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setMinimumSize(new Dimension(100, 30));
        SpinnerNumberModel numberModel = new SpinnerNumberModel(123.4, 0.0, 1000, 1.0);
        JSpinner spinner = new JSpinner(numberModel);
        JSpinner.NumberEditor numberEditor = new JSpinner.NumberEditor(spinner, "###0.0#");
        contentPane.add(spinner, BorderLayout.NORTH);
        TerrainModel model = new TerrainModel(40,30);
        terrainPane = new TerrainPanel(model,10);
        MouseMonitor mouseMonitor = new MouseMonitor();
        terrainPane.addMouseListener(mouseMonitor);
        terrainPane.addMouseMotionListener(mouseMonitor);
        JScrollPane scroller = new JScrollPane(terrainPane);
        scroller.setPreferredSize(new Dimension(200, 200));
        contentPane.add(scroller, BorderLayout.CENTER);
        JLabel messageField = new JLabel("No message.                            ");
        contentPane.add(messageField, BorderLayout.SOUTH);
    }

    public void start()
    {
    }

    public void stop()
    {
    }

    public void destroy()
    {
    }

    public static void main(String[] args)
    {
        TerrainApplet applet = new TerrainApplet();
        applet.startAsApplication("Terrain");
    }
}
