/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Aug 16, 2002
 * Time: 9:27:14 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.yajul.empire;

import org.yajul.swing.ApplicationApplet;
import org.yajul.swing.LineGraphModel;
import org.yajul.swing.LineGraphPanel;
import org.yajul.math.RangeMapper;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

/**
 * The 'view' class for a tesselation model.
 * Since this is a view, it supports:
 * Tesseleation getTesselationModel()
 * void setTesselationModel(FractalModel)
 */
class FractalPane
        extends LineGraphPanel
{

    public FractalPane(FractalModel model)
    {
        super(model);
        setMarkerSize(new Dimension(2, 2));
    }
}

class FractalIterator
{
    private double factor;
    private double factor2;
    private Random random;
    private int i;
    private RangeMapper rangeMapper;

    FractalIterator(double factor,double min,double max,double factor2, long seed)
    {
        this.factor = factor;
        this.factor2 = factor2;
        this.random = new Random(seed);
        this.i = 1;
        this.rangeMapper = new RangeMapper(min,max);
    }

    double gamma(int i)
    {
        // Equivalent to iterating: g = g * factor , where factor is between 0 and 1
        return 1.0 / Math.pow(1.0/factor,i);
    }

    public void nextGamma()
    {
        i++;
    }

    public Point nextMidpoint(Point prev,Point next)
    {
        // Create a midpoint : Split each line segment into two
        double dx = next.x - prev.x;                    // The change in x.
        double dy = next.y - prev.y;                    // The change in y.
        double midx = (double)prev.x + (dx / 2.0);      // Midpoint x
        double midy = (double)prev.y + (dy / 2.0);      // Midpoint y

        // Calculate the new delta range (gamma) based on the current iteration (scale) and
        // the fractal scaling factor.
        double gamma = gamma(i);

        // If the range of delta will be do small, just return the midpoint.
        if (gamma <= 0.0)
            return new Point((int)midx,(int)midy);

        // Get the next random number, betweeen 0 and 1.
        double r = random.nextDouble();

        // Get the next random number in the range -1.0 to 1.0
        r = 2.0 * r - 1.0;

        // Compute delta: a new random value between -gamma and +gamma.
        double delta = gamma * r;

//        System.out.println("delta = " + delta + " gamma = " + gamma);

        double y = midy + rangeMapper.getRange() * delta;

//        y = factor2 * range * range * rangeMapper.getMax() + y;
        return new Point((int)midx,(int)y);
    }
}
/**
 * The model class, contains a set of points.  Knows how to tesselate.
 * Since this is a model class, it supports:
 * void addTesselationModelListener(TesselationModelListener)
 * void removeTesselationModelListener(TesselationModelListener)
 */
class FractalModel extends LineGraphModel
{
//    private int range;
    private int threshold;
    private BoundedRangeModel factor;
    private BoundedRangeModel factor2;
    private BoundedRangeModel seedModel;
    private FractalIterator iterator;

    public FractalModel(int xsize)
    {
        super(new Dimension(xsize, xsize));
        this.factor = new DefaultBoundedRangeModel(50, 0, 0, 100);
        this.factor2 = new DefaultBoundedRangeModel(0, 0, -100, 100);
        this.seedModel = new DefaultBoundedRangeModel(0, 0, 0, 1000);
        ChangeListener changeListener =
                new ChangeListener()
                {
                    /**
                     * Invoked when the target of the listener has changed its state.
                     *
                     * @param e  a ChangeEvent object
                     */
                    public void stateChanged(ChangeEvent e)
                    {
                        reset();
                        go(false);
                    }
                };
        // The tesselation model will want to know when the factor has changed.
        this.factor.addChangeListener(changeListener);
        this.factor2.addChangeListener(changeListener);
        this.seedModel.addChangeListener(changeListener);
        reset();
    }

    public void reset()
    {
        clear();                                        // Remove all points from the graph model.
        threshold = 3;
        iterator = new FractalIterator(
                getFactor(),
                0,
                getSize().height,
                getFactor2(),
                seedModel.getValue() * 31 + 115441
                );
        // Add the side points from the sequence.
        int y = getSize().height / 2;
        Point min = new Point(0,y);
        Point max = new Point(getSize().width,y);
        add(min);
        add(max);
    }

    public void go(boolean single)
    {
        boolean loop = true;
        double factor = getFactor();
        Point[] points = null;
        int limit = 0;
        Point next = null;
        Point prev = null;
        Point mid = null;
        int loopCount = 0;

        System.out.println("--- start ---");
//        FractalIterator fi = new FractalIterator(range,factor,seedModel.getValue());
//        fi.setFactor(factor);

        while (loop)
        {
            loopCount++;
            points = getPointArray();
            // Use an array instead of an iterator, as the viewer might open an iterator.
            limit = points.length;
            next = prev = mid = null;
//            range = fi.gamma(loopCount - 1);
            for (int i = 0; i < limit; i++)
            {
                next = points[i];
                if (prev != null)
                {
                    // If the x distance between the previous and next points is less than the threashold,
                    // then skip this point.
                    int dx = next.x - prev.x;
                    // System.out.println("dx = " + dx);
                    if (dx < threshold)
                    {
                        if (single && loopCount == 1)
                        {
                            System.out.println("resetting...");
                            reset();
                            return;         // Get out now!
                        }
                        else
                        {
                            System.out.println("that's all");
                            loop = false;
                        }
                        break;
                    } // if dx < threshold
                    mid = iterator.nextMidpoint(prev,next);
                    add(mid);
                }
                prev = next;
            }  // for

            iterator.nextGamma();

            // For a 'single iteration' run, stop now.
            if (single)
                loop = false;
        } // while (loop)
    }

    public double getFactor()
    {
        double f = (double) factor.getValue() / 100.0;
        // System.out.println("value = " + factor.getValue() + " f = " + f);
        return f;
    }

    public double getFactor2()
    {
        double f = (double) factor2.getValue() / 100.0;
        // System.out.println("value = " + factor.getValue() + " f = " + f);
        return f;
    }

    public BoundedRangeModel getFactorModel()
    {
        return factor;
    }

    public BoundedRangeModel getFactor2Model()
    {
        return factor2;
    }

    public BoundedRangeModel getSeedModel()
    {
        return seedModel;
    }
}


public class FractalApplet extends ApplicationApplet
{
    private LineGraphPanel pane;
    private FractalModel model;
    private JButton goButton;

    public void init()
    {
        Container contentPane = getContentPane();
        BorderLayout bl = new BorderLayout();
        contentPane.setLayout(bl);

        model = new FractalModel(600);
        pane = new FractalPane(model);

        JPanel buttonPanel = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        buttonPanel.setLayout(gridbag);


        goButton = new JButton("go");
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;            // change the weight.
        c.gridheight = GridBagConstraints.REMAINDER;           // 2 rows high.
        gridbag.setConstraints(goButton, c);
        buttonPanel.add(goButton);
        c.weighty = 0.0;            // reset to the default
        c.gridwidth = GridBagConstraints.REMAINDER; // end row
        c.gridheight = 1;           // reset to the default
        JSlider factorSlider = new JSlider(model.getFactorModel());
        gridbag.setConstraints(factorSlider, c);
        buttonPanel.add(factorSlider);
        JSlider factor2Slider = new JSlider(model.getFactor2Model());
        gridbag.setConstraints(factor2Slider, c);
        buttonPanel.add(factor2Slider);
        JSlider seedSlider = new JSlider(model.getSeedModel());
        gridbag.setConstraints(seedSlider, c);
        buttonPanel.add(seedSlider);

        contentPane.add(buttonPanel, BorderLayout.NORTH);

        JScrollPane scroller = new JScrollPane(pane);
        scroller.setPreferredSize(new Dimension(600, 600));
        contentPane.add(scroller, BorderLayout.CENTER);
        JLabel messageField = new JLabel("No message.                            ");
        contentPane.add(messageField, BorderLayout.SOUTH);

        goButton.addActionListener(
                new ActionListener()
                {
                    /**
                     * Invoked when an action occurs.
                     */
                    public void actionPerformed(ActionEvent e)
                    {
                        model.go(true);
                    }
                }
        );

        model.addChangeListener(pane);
        model.go(false);
    }


    public static void main(String[] args)
    {
        FractalApplet applet = new FractalApplet();
        applet.startAsApplication("Fractal");
    }
}
