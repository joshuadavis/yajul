/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jul 4, 2002
 * Time: 9:09:33 AM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.yajul.empire;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.event.MenuListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuDragMouseListener;
import javax.swing.event.MenuDragMouseEvent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;

public class EmpireApplet extends javax.swing.JApplet
{
    private Dimension preferredSize = new Dimension(350,200);
    private JFrame appFrame = null;
    private JMenuBar bar = null;


    private World world = null;

    public EmpireApplet() throws HeadlessException
    {
        super();
    }

    public void startAsApplication()
    {
        JFrame f = new JFrame("Empire");
        WindowListener wl = new WindowAdapter()
        {
            public void windowCloseing(WindowEvent e)
            {
                ((Window) e.getSource()).dispose();
                System.exit(0);
            }
        };
        f.addWindowListener(wl);
        f.getContentPane().add(this);
        init();
        f.pack();
        f.setVisible(true);
        appFrame = f;

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
        setJMenuBar(bar);
        world = new World(80,25);
        JScrollPane scroller = new JScrollPane(new WorldPane(world));
        scroller.setPreferredSize(new Dimension(200,200));
        contentPane.add(scroller);
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
        EmpireApplet applet = new EmpireApplet();
        applet.startAsApplication();
    }
}
