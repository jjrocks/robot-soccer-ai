package sebbot.ballcapture;

import java.io.Serializable;

import sebbot.SoccerParams;

/**
 * @author Sebastien Lentz
 *
 */
public class RadialGaussian implements Serializable
{
    private static final long serialVersionUID = -4676316425662558431L;
    
    float[] centers;
    float[] radii;
    int     discreteActionNb; // The discrete action this function is linked to.

    public RadialGaussian(int discreteActionNb)
    {
        this();
        this.discreteActionNb = discreteActionNb;
    }
    
    public RadialGaussian()
    {
        this.discreteActionNb = (int) Math.floor(Math.random()
                * (Action.getDashSteps() + Action.getTurnSteps()));

        this.centers = new float[7];
        this.radii = new float[7];

        centers[0] = (float) (SoccerParams.BALL_SPEED_MAX * Math.random());
        radii[0] = (float) (SoccerParams.BALL_SPEED_MAX / 2.0d * Math.random());

        centers[1] = (float) (360.0d * Math.random() - 180.0d);
        radii[1] = (float) (180.0d * Math.random());

        centers[2] = (float) (SoccerParams.PLAYER_SPEED_MAX * Math.random());
        radii[2] = (float) (SoccerParams.PLAYER_SPEED_MAX / 2.0d * Math
            .random());

        centers[3] = (float) (360.0d * Math.random() - 180.0d);
        radii[3] = (float) (180.0d * Math.random());

        centers[4] = (float) (360.0d * Math.random() - 180.0d);
        radii[4] = (float) (180.0d * Math.random());

        centers[5] = (float) (125.0d * Math.random());
        radii[5] = (float) (125.0d / 2.0d * Math.random());

        centers[6] = (float) (360.0d * Math.random() - 180.0d);
        radii[6] = (float) (180.0d * Math.random());

        for (int i = 0; i < 7; i++)
        {
            if (radii[i] == 0.0d)
            {
                radii[i] = 0.001f;
            }
        }
    }

    /**
     * @return the discreteActionNb
     */
    public int getDiscreteActionNb()
    {
        return discreteActionNb;
    }

    /**
     * @param discreteAction the discreteActionNb to set
     */
    public void setDiscreteActionNb(int discreteAction)
    {
        this.discreteActionNb = discreteAction;
    }

    /**
     * @return the centers
     */
    public float[] getCenters()
    {
        return centers;
    }

    /**
     * @param centers the centers to set
     */
    public void setCenters(float[] centers)
    {
        this.centers = centers;
    }

    /**
     * @return the radii
     */
    public float[] getRadii()
    {
        return radii;
    }

    /**
     * @param radii the radii to set
     */
    public void setRadii(float[] radii)
    {
        this.radii = radii;
    }

    public double f(State s)
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
    
    public String toString()
    {
        String str = "{RG: ";
        
        for (int i = 0; i < 6 ; i++)
        {
            str += "(" + i + ", " + centers[i] + ", " + radii[i] + ") ";
        }
        
        str += "(" + 6 + ", " + centers[6] + ", " + radii[6] + ")}";
        
        return str;
    }

}
