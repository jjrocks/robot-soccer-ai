package sebbot;

import java.security.SecureRandom;
import java.util.Random;

import sebbot.ballcapture.State;

/**
 * @author Sebastien Lentz
 *
 */
public class MathTools
{
    static SecureRandom random = new SecureRandom();

    /**
     * Normalize the input angle so that it belongs to the interval [-180, 180].
     * 
     * @param a
     *          the input angle to normalize.
     * @return
     *          the normalized angle.
     */
    public static double normalizeAngle(double a)
    {
        if (Math.abs(a) > 360.0D)
        {
            a %= (360.0D);
        }
        if (a > 180.0D)
        {
            a -= 360.0D;
        }
        if (a < -180.0D)
        {
            a += 360.0D;
        }

        return a;
    }

    /**
     * Quantize the input number according to the input step.
     * 
     * @param nb
     *          the number to quantize.
     * @param step
     *          the quantization step.
     * @return
     *          the quantized number.
     */
    public static double quantize(double nb, double step)
    {
        return Math.rint(nb / step) * step;
    }

    /**
     * This function returns the number of the interval that contains the input
     * value according to the number of the steps, the min and max values of
     * the continuous range that is being considered.
     * 
     * @param value the input value to index.
     * @param minValue the min value of the continuous range.
     * @param maxValue the max value of the continuous range.
     * @param nbOfSteps the number of intervals in the continuous range.
     * @return
     */
    public static int valueToIndex(double value, double minValue,
                                   double maxValue, int nbOfSteps)
    {
        int index = (int) Math.rint((value - minValue) / (maxValue - minValue)
                * nbOfSteps);

        return index >= nbOfSteps ? nbOfSteps - 1 : index;
    }

    /**
     * This function returns a representative value of the the interval indexed
     * by the input integer.
     * 
     * @param index the index of the interval in the continuous range.
     * @param minValue the min value of the continuous range.
     * @param maxValue the max value of the continuous range.
     * @param nbOfSteps the number of intervals in the continuous range.
     * @param intervalPosition the position of the representative value inside
     *                         the interval. 0<=intervalPosition<=1.
     * @return
     */
    public static double indexToValue(int index, double minValue,
                                      double maxValue, int nbOfSteps,
                                      float intervalPosition)
    {
        if (intervalPosition > 1.0f || intervalPosition < 0.0f)
        { // Invalid intervalPosition
            intervalPosition = 0.5f;
        }

        double intervalLength = Math.abs((maxValue - minValue) / nbOfSteps);

        return minValue + intervalLength * (intervalPosition + index);
    }

    /**
     * This function discretizes the input value to the middle value of an
     * interval.
     * 
     * @param value the input value to discretize.
     * @param minValue the min value of the continuous range.
     * @param maxValue the max value of the continuous range.
     * @param nbOfSteps the number of intervals in the continuous range.
     * @param intervalPosition the position of the representative value inside
     *                         the interval. 0<=intervalPosition<=1.
     * @return
     */
    public static double discretize(double value, double minValue,
                                    double maxValue, int nbOfSteps,
                                    float intervalPosition)
    {
        return indexToValue(valueToIndex(value, minValue, maxValue, nbOfSteps),
            minValue, maxValue, nbOfSteps, intervalPosition);
    }

    /**
     * Converts polar coordinates to Cartesian ones.
     * 
     * @param radius
     * @param angle
     * @return
     */
    public static Vector2D toCartesianCoordinates(double radius, double angle)
    {
        return new Vector2D(radius * Math.cos(Math.toRadians(angle)), radius
                * Math.sin(Math.toRadians(angle)));
    }

    public static float min(float[] f)
    {
        float min = f[0];

        for (int i = 1; i < f.length; i++)
        {
            if (f[i] < min)
            {
                min = f[i];
            }
        }

        return min;
    }
    
    public static float max(float[] f)
    {
        float max = f[0];

        for (int i = 1; i < f.length; i++)
        {
            if (f[i] > max)
            {
                max = f[i];
            }
        }

        return max;
    }

    public static float mean(float[] f)
    {
        float total = 0.0f;
        for (int i = 0; i < f.length; i++)
        {
            total += f[i];
        }

        return total / f.length;
    }

    public static float mean(boolean[] b)
    {
        float nbOfTrue = 0.0f;
        for (int i = 0; i < b.length; i++)
        {
            if (b[i])
            {
                nbOfTrue += 1.0f;
            }
        }

        return nbOfTrue / ((float) b.length);
    }

    public static float stdDev(float[] f, float mean)
    {
        float stdDev = 0.0f;
        for (int i = 0; i < f.length; i++)
        {
            stdDev += (f[i] - mean) * (f[i] - mean);
        }

        stdDev = (float) Math.sqrt(stdDev / f.length);

        return stdDev;
    }

    public static float nextGaussian(float mean, float stdDev)
    {
        return (float) (mean + random.nextGaussian() * stdDev);
    }

    public static boolean nextBernoulli(float mean)
    {
        return random.nextFloat() < mean ? true : false;
    }

    public static int toDecimal(boolean[] b)
    {
        int d = 0;
        int pow = 1;

        for (int i = 0; i < b.length; i++)
        {
            if (b[i])
            {
                d += pow;
            }
            pow *= 2;
        }

        return d;
    }

    public static double radialGaussian(State s, float[] centers, float[] radii)
    {
        double[] x = new double[7];
        x[0] = s.getBallVelocityNorm();
        x[1] = s.getBallVelocityDirection();
        x[2] = s.getPlayerVelocityNorm();
        x[3] = s.getPlayerVelocityDirection();
        x[4] = s.getPlayerBodyDirection();
        x[5] = s.getRelativeDistance();
        x[6] = s.getRelativeDirection();

        double argExp = 0;
        for (int i = 0; i < 7; i++)
        {
            argExp += Math.pow((x[i] - centers[i]) / radii[i], 2.0d);
        }

        return Math.exp(-argExp);
    }

}
