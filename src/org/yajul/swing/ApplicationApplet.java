/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Aug 3, 2002
 * Time: 8:09:05 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.yajul.swing;

import javax.swing.JApplet;
import javax.swing.JFrame;
import java.awt.event.WindowListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Window;

public class ApplicationApplet extends JApplet
{
    private JFrame appFrame = null;

    public void startAsApplication(String name)
    {
        JFrame f = new JFrame(name);
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
}
