package sebbot;

import java.util.ArrayList;

/**
 * @author Sebastien Lentz
 *
 */
public class Player extends MobileObject
{
    /*
     * Private members.
     */
    private boolean leftSide;
    private int     playerType;
    private int     uniformNumber;
    private double  bodyDirection;
    private int playerNumber;

    /*
     * =========================================================================
     * 
     *                     Constructors and destructors
     * 
     * =========================================================================
     */
    /**
     * Constructor.
     * 
     * @param posX
     * @param posY
     * @param velX
     * @param velY
     * @param team
     * @param playerType
     * @param bodyDirection
     */
    public Player(double posX, double posY, double velX, double velY,
            boolean team, int playerType, double bodyDirection, int playerNumber)
    {
        super(posX, posY, velX, velY);
        this.leftSide = team;
        this.playerType = playerType;
        this.bodyDirection = bodyDirection;
        this.playerNumber = playerNumber;
    }

    /*
     * =========================================================================
     * 
     *                            Getters and Setters
     * 
     * =========================================================================
     */
    /**
     * @return the bodyDirection
     */
    public double getBodyDirection()
    {
        return bodyDirection;
    }

    /**
     * @param bodyDirection the bodyDirection to set
     */
    public void setBodyDirection(double bodyDirection)
    {
        this.bodyDirection = bodyDirection;
    }

    /**
     * @return the leftSideTeam
     */
    public boolean isLeftSide()
    {
        return leftSide;
    }

    /**
     * @param leftSide the leftSideTeam to set
     */
    public void setLeftSide(boolean leftSide)
    {
        this.leftSide = leftSide;
    }

    /**
     * @return the playerType
     */
    public int getPlayerType()
    {
        return playerType;
    }

    /**
     * @param playerType the playerType to set
     */
    public void setPlayerType(int playerType)
    {
        this.playerType = playerType;
    }

    /**
     * @return the uniformNumber
     */
    public int getUniformNumber()
    {
        return uniformNumber;
    }

    /**
     * @param uniformNumber the uniformNumber to set
     */
    public void setUniformNumber(int uniformNumber)
    {
        this.uniformNumber = uniformNumber;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    /*
     * =========================================================================
     * 
     *                            Movement methods
     * 
     * =========================================================================
     */
    /**
     * This methods computes the angle this player needs to turn in order
     * to face the point (x, y).
     * 
     * @param x
     *          the abscissa of the point.
     * @param y
     *          the ordinate of the point.
     * @return
     *          the angle to turn.
     */
    public double angleFromBody(double x, double y)
    {
        return MathTools.normalizeAngle(position.directionOf(x, y)
                - bodyDirection);
    }

    /**
     * This methods computes the angle this player needs to turn in order
     * to face the point p.
     * 
     * @param p
     *          the point.
     * @return
     *          the angle to turn.
     */
    public double angleFromBody(Vector2D p)
    {
        return MathTools
                .normalizeAngle(position.directionOf(p) - bodyDirection);
    }

    /**
     * This methods computes the angle this player needs to turn in order
     * to face the object o.
     * 
     * @param o
     *          the object.
     * @return
     *          the angle to turn.
     */
    public double angleFromBody(MobileObject o)
    {
        return angleFromBody(o.getPosition());
    }

    /*
     * =========================================================================
     * 
     *                     Movement extrapolation methods
     * 
     * =========================================================================
     */
    /**
     * @param initialPosition
     * @param initialVelocity
     * @param power
     * @return
     */
    public Vector2D nextPosition(Vector2D initialPosition,
            Vector2D initialVelocity, double power)
    {
        return super.nextPosition(initialPosition, initialVelocity,
                SoccerParams.PLAYER_SPEED_MAX, SoccerParams.PLAYER_ACCEL_MAX,
                SoccerParams.DASH_POWER_RATE, power, bodyDirection);
    }

    /**
     * @param power
     * @return
     */
    public Vector2D nextPosition(double power)
    {
        return this.nextPosition(this.position, this.velocity, power);
    }

    /* (non-Javadoc)
     * @see sebbot.MobileObject#nextVelocity(sebbot.Vector2D, double)
     */
    public Vector2D nextVelocity(Vector2D initialVelocity, double power)
    {
        return super.nextVelocity(initialVelocity, SoccerParams.PLAYER_DECAY,
                SoccerParams.PLAYER_SPEED_MAX, SoccerParams.PLAYER_ACCEL_MAX,
                SoccerParams.DASH_POWER_RATE, power, bodyDirection);
    }

    /* (non-Javadoc)
     * @see sebbot.MobileObject#nextVelocity(double)
     */
    public Vector2D nextVelocity(double power)
    {
        return this.nextVelocity(this.velocity, power);
    }

    /**
     * @param initialPosition
     * @param initialVelocity
     * @return
     */
    public ArrayList<Vector2D> trajectory(Vector2D initialPosition,
            Vector2D initialVelocity)
    {
        return super.trajectory(initialPosition, initialVelocity,
                SoccerParams.PLAYER_DECAY);
    }

    /**
     * @return
     */
    public ArrayList<Vector2D> trajectory()
    {
        return this.trajectory(this.position, this.velocity);
    }

    /*
     * =========================================================================
     * 
     *                          Other methods
     * 
     * =========================================================================
     */
    public String toString()
    {
        return "Player " + (isLeftSide() ? "left " : "right ") + uniformNumber
                + ": " + super.toString() + " - BodyDir: " + bodyDirection;
    }

}
