/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jul 28, 2002
 * Time: 6:26:21 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.yajul.math;

/**
 *
 */
public class QuadraticInterpolator
{
    private float quadraticCoefficient;
    private float linearCoefficient;
    private float constantCoefficient;
    private float epsilon = (float) .01;
    private float[] xcoord;
    private float[] ycoord;

    public QuadraticInterpolator(float[] xcoord, float[] ycoord)
    {
        computeCoefficients(xcoord,ycoord);
    }

    private void computeCoefficients(float[] xcoord,float[] ycoord)
    {
      // Remember the coordinates used.
      this.xcoord = xcoord;
      this.ycoord = ycoord;

      // Watch out for division by zero
      float denom2 = (xcoord[2] - xcoord[1])*(xcoord[2] - xcoord[0]);
      float denom1 = (xcoord[1] - xcoord[0])*(xcoord[1] - xcoord[2]);
      float denom0 = (xcoord[0] - xcoord[1])*(xcoord[0] - xcoord[2]);

      if (Math.abs(denom0) < epsilon || Math.abs(denom1) < epsilon || Math.abs(denom2) < epsilon )
      {
      } // Do nothing if the numbers are too close together...
      else
      {
        quadraticCoefficient = ycoord[0]/denom0 + ycoord[1]/denom1 +  ycoord[2]/denom2;
        linearCoefficient = - ( ycoord[0] * (xcoord[1] + xcoord[2])/denom0 +
                    ycoord[1] * (xcoord[0] + xcoord[2])/denom1 +
                    ycoord[2] * (xcoord[0] + xcoord[1])/denom2 );
        constantCoefficient = ycoord[0] * xcoord[1] * xcoord[2]/denom0 +
                    ycoord[1] * xcoord[0] * xcoord[2]/denom1 +
                    ycoord[2] * xcoord[0] * xcoord[1]/denom2 ;
      } // recompute coefficients only if denominators are not ridiculously small
    }

    public float interpolate(float x)
    {
        float rv = x * quadraticCoefficient + linearCoefficient;
        rv = rv * x + constantCoefficient;
        return rv;
    }

    public float getEpsilon()
    {
        return epsilon;
    }

    public void setEpsilon(float epsilon)
    {
        this.epsilon = epsilon;
        computeCoefficients(xcoord,ycoord);
    }
}
