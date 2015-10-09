package sebbot.ballcapture;

import java.io.Serializable;
import java.text.DecimalFormat;

import sebbot.FullstateInfo;
import sebbot.MathTools;
import sebbot.Player;
import sebbot.SoccerParams;

public class State implements Serializable
{
    private static final long    serialVersionUID             = 6078896792558961445L;

    private static DecimalFormat decimalFormatter             = new DecimalFormat(
                                                                  "000.000");

    private static int           ballVelocityNormSteps        = 1;
    private static int           ballVelocityDirectionSteps   = 10;
    private static int           playerVelocityNormSteps      = 1;
    private static int           playerVelocityDirectionSteps = 10;
    private static int           playerBodyDirectionSteps     = 20;
    private static int           relativeDistanceSteps        = 200;
    private static int           relativeDirectionSteps       = 20;

    private float                ballVelocityNorm;
    private float                ballVelocityDirection;
    private float                playerVelocityNorm;
    private float                playerVelocityDirection;
    private float                playerBodyDirection;
    private float                relativeDistance;
    private float                relativeDirection;

    static
    {
        decimalFormatter.setPositivePrefix("+");
    }

    /*
     * =========================================================================
     * 
     *                     Constructors and destructors
     * 
     * =========================================================================
     */
    /**
     * @param ballVelocityNorm
     * @param ballVelocityDirection
     * @param playerVelocityNorm
     * @param playerVelocityDirection
     * @param playerBodyDirection
     * @param relativeDistance
     * @param relativeDirection
     * @param isTerminal
     */
    public State(float ballVelocityNorm, float ballVelocityDirection,
                 float playerVelocityNorm, float playerVelocityDirection,
                 float playerBodyDirection, float relativeDistance,
                 float relativeDirection)
    {
        this.ballVelocityNorm = ballVelocityNorm;
        this.ballVelocityDirection = ballVelocityDirection;
        this.playerVelocityNorm = playerVelocityNorm;
        this.playerVelocityDirection = playerVelocityDirection;
        this.playerBodyDirection = playerBodyDirection;
        this.relativeDistance = relativeDistance;
        this.relativeDirection = relativeDirection;
    }

    /**
     * 
     */
    public State()
    {
        this(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 99.0f, 0.0f);
    }

    /**
     * @param fsi
     */
    public State(FullstateInfo fsi, Player player)
    {
        this.ballVelocityNorm = ((float) fsi.getBall().getVelocity()
            .polarRadius());
        this.ballVelocityDirection = ((float) fsi.getBall().getVelocity()
            .polarAngle());
        this.playerVelocityNorm = ((float) player.getVelocity().polarRadius());
        this.playerVelocityDirection = ((float) player.getVelocity().polarAngle());
        this.playerBodyDirection = ((float) player.getBodyDirection());
        this.relativeDistance = ((float) player.distanceTo(fsi.getBall()));
        this.relativeDirection = ((float) player.angleFromBody(fsi.getBall()));
    }

    /*
     * =========================================================================
     * 
     *                     Getters and setters
     * 
     * =========================================================================
     */
    /**
     * @return the ballVelocityNormSteps
     */
    public static int getBallVelocityNormSteps()
    {
        return ballVelocityNormSteps;
    }

    /**
     * @param ballVelocityNormSteps the ballVelocityNormSteps to set
     */
    public static void setBallVelocityNormSteps(int ballVelocityNormSteps)
    {
        State.ballVelocityNormSteps = ballVelocityNormSteps;
    }

    /**
     * @return the ballVelocityDirectionSteps
     */
    public static int getBallVelocityDirectionSteps()
    {
        return ballVelocityDirectionSteps;
    }

    /**
     * @param ballVelocityDirectionSteps the ballVelocityDirectionSteps to set
     */
    public static void setBallVelocityDirectionSteps(
                                                     int ballVelocityDirectionSteps)
    {
        State.ballVelocityDirectionSteps = ballVelocityDirectionSteps;
    }

    /**
     * @return the playerVelocityNormSteps
     */
    public static int getPlayerVelocityNormSteps()
    {
        return playerVelocityNormSteps;
    }

    /**
     * @param playerVelocityNormSteps the playerVelocityNormSteps to set
     */
    public static void setPlayerVelocityNormSteps(int playerVelocityNormSteps)
    {
        State.playerVelocityNormSteps = playerVelocityNormSteps;
    }

    /**
     * @return the playerVelocityDirectionSteps
     */
    public static int getPlayerVelocityDirectionSteps()
    {
        return playerVelocityDirectionSteps;
    }

    /**
     * @param playerVelocityDirectionSteps the playerVelocityDirectionSteps to set
     */
    public static void setPlayerVelocityDirectionSteps(
                                                       int playerVelocityDirectionSteps)
    {
        State.playerVelocityDirectionSteps = playerVelocityDirectionSteps;
    }

    /**
     * @return the playerBodyDirectionSteps
     */
    public static int getPlayerBodyDirectionSteps()
    {
        return playerBodyDirectionSteps;
    }

    /**
     * @param playerBodyDirectionSteps the playerBodyDirectionSteps to set
     */
    public static void setPlayerBodyDirectionSteps(int playerBodyDirectionSteps)
    {
        State.playerBodyDirectionSteps = playerBodyDirectionSteps;
    }

    /**
     * @return the relativeDistanceSteps
     */
    public static int getRelativeDistanceSteps()
    {
        return relativeDistanceSteps;
    }

    /**
     * @param relativeDistanceSteps the relativeDistanceSteps to set
     */
    public static void setRelativeDistanceSteps(int relativeDistanceSteps)
    {
        State.relativeDistanceSteps = relativeDistanceSteps;
    }

    /**
     * @return the relativeDirectionSteps
     */
    public static int getRelativeDirectionSteps()
    {
        return relativeDirectionSteps;
    }

    /**
     * @param relativeDirectionSteps the relativeDirectionSteps to set
     */
    public static void setRelativeDirectionSteps(int relativeDirectionSteps)
    {
        State.relativeDirectionSteps = relativeDirectionSteps;
    }

    /**
     * @return the ballVelocityNorm
     */
    public float getBallVelocityNorm()
    {
        return ballVelocityNorm;
    }

    /**
     * @param ballVelocityNorm the ballVelocityNorm to set
     */
    public void setBallVelocityNorm(float ballVelocityNorm)
    {
        this.ballVelocityNorm = ballVelocityNorm;
    }

    /**
     * @return the ballVelocityDirection
     */
    public float getBallVelocityDirection()
    {
        return ballVelocityDirection;
    }

    /**
     * @param ballVelocityDirection the ballVelocityDirection to set
     */
    public void setBallVelocityDirection(float ballVelocityDirection)
    {
        this.ballVelocityDirection = ballVelocityDirection;
    }

    /**
     * @return the playerVelocityNorm
     */
    public float getPlayerVelocityNorm()
    {
        return playerVelocityNorm;
    }

    /**
     * @param playerVelocityNorm the playerVelocityNorm to set
     */
    public void setPlayerVelocityNorm(float playerVelocityNorm)
    {
        this.playerVelocityNorm = playerVelocityNorm;
    }

    /**
     * @return the playerVelocityDirection
     */
    public float getPlayerVelocityDirection()
    {
        return playerVelocityDirection;
    }

    /**
     * @param playerVelocityDirection the playerVelocityDirection to set
     */
    public void setPlayerVelocityDirection(float playerVelocityDirection)
    {
        this.playerVelocityDirection = playerVelocityDirection;
    }

    /**
     * @return the playerBodyDirection
     */
    public float getPlayerBodyDirection()
    {
        return playerBodyDirection;
    }

    /**
     * @param playerBodyDirection the playerBodyDirection to set
     */
    public void setPlayerBodyDirection(float playerBodyDirection)
    {
        this.playerBodyDirection = playerBodyDirection;
    }

    /**
     * @return the relativeDistance
     */
    public float getRelativeDistance()
    {
        return relativeDistance;
    }

    /**
     * @param relativeDistance the relativeDistance to set
     */
    public void setRelativeDistance(float relativeDistance)
    {
        this.relativeDistance = relativeDistance;
    }

    /**
     * @return the relativeDirection
     */
    public float getRelativeDirection()
    {
        return relativeDirection;
    }

    /**
     * @param relativeDirection the relativeDirection to set
     */
    public void setRelativeDirection(float relativeDirection)
    {
        this.relativeDirection = relativeDirection;
    }

    /**
     * @return the isTerminal
     */
    public boolean isTerminal()
    {
        return (relativeDistance < SoccerParams.KICKABLE_MARGIN ? true : false);
    }

    /*
     * =========================================================================
     * 
     *                          Main methods
     * 
     * =========================================================================
     */
    /**
     * @param ballVelocityNormSteps
     * @param ballVelocityDirectionSteps
     * @param playerVelocityNormSteps
     * @param playerVelocityDirectionSteps
     * @param playerBodyDirectionSteps
     * @param relativeDistanceSteps
     * @param relativeDirectionSteps
     * @return
     */
    public State discretize(int ballVelocityNormSteps,
                            int ballVelocityDirectionSteps,
                            int playerVelocityNormSteps,
                            int playerVelocityDirectionSteps,
                            int playerBodyDirectionSteps,
                            int relativeDistanceSteps,
                            int relativeDirectionSteps)

    {
        ballVelocityNorm = (float) MathTools.discretize(ballVelocityNorm, 0.0f,
            SoccerParams.BALL_SPEED_MAX, ballVelocityNormSteps, 0.0f);

        ballVelocityDirection = (float) MathTools.discretize(
            ballVelocityDirection, -180.0f, 180.0f, ballVelocityDirectionSteps,
            0.0f);

        playerVelocityNorm = (float) MathTools.discretize(playerVelocityNorm,
            0.0f, SoccerParams.PLAYER_SPEED_MAX, playerVelocityNormSteps, 0.0f);

        playerVelocityDirection = (float) MathTools.discretize(
            playerVelocityDirection, -180.0f, 180.0f,
            playerVelocityDirectionSteps, 0.0f);

        playerBodyDirection = (float) MathTools.discretize(playerBodyDirection,
            -180.0f, 180.0f, playerBodyDirectionSteps, 0.0f);

        relativeDistance = (float) MathTools.discretize(relativeDistance, 0.0f,
            125.0f, relativeDistanceSteps, 0.0f);

        relativeDirection = (float) MathTools.discretize(relativeDirection,
            -180.0f, 180.0f, relativeDirectionSteps, 0.0f);

        return this;
    }

    /**
     * @return
     */
    public State discretize()
    {
        discretize(ballVelocityNormSteps, ballVelocityDirectionSteps,
            playerVelocityNormSteps, playerVelocityDirectionSteps,
            playerBodyDirectionSteps, relativeDistanceSteps,
            relativeDirectionSteps);

        return this;
    }

    public String toString()
    {
        String str = "{";

        str += decimalFormatter.format(ballVelocityNorm) + ", ";
        str += decimalFormatter.format(ballVelocityDirection) + ", ";
        str += decimalFormatter.format(playerVelocityNorm) + ", ";
        str += decimalFormatter.format(playerVelocityDirection) + ", ";
        str += decimalFormatter.format(playerBodyDirection) + ", ";
        str += decimalFormatter.format(relativeDistance) + ", ";
        str += decimalFormatter.format(relativeDirection) + "}";

        return str;
    }
}
