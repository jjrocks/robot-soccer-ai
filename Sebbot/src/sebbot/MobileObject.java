package sebbot;

import java.util.ArrayList;

/**
 * @author Sebastien Lentz
 *
 */
public abstract class MobileObject
{
    protected Vector2D position;
    protected Vector2D velocity;

    /**
     * Constructor.
     * 
     * @param posX
     * @param posY
     * @param velX
     * @param velY
     */
    public MobileObject(double posX, double posY, double velX, double velY)
    {
        position = new Vector2D(posX, posY);
        velocity = new Vector2D(velX, velY);
    }

    /*
     * =========================================================================
     * 
     *                      Getters and Setters
     * 
     * =========================================================================
     */
    /**
     * @return the position
     */
    public Vector2D getPosition()
    {
        return position;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(Vector2D position)
    {
        this.position = position;
    }

    /**
     * @return the velocity
     */
    public Vector2D getVelocity()
    {
        return velocity;
    }

    /**
     * @param velocity the velocity to set
     */
    public void setVelocity(Vector2D velocity)
    {
        this.velocity = velocity;
    }

    /*
     * =========================================================================
     * 
     *                          Movements methods
     * 
     * =========================================================================
     */
    public double distanceTo(Vector2D v)
    {
        return position.distanceTo(v);
    }

    public double distanceTo(MobileObject o)
    {
        return distanceTo(o.getPosition());
    }

    protected Vector2D velocity(Vector2D initialVelocity, double maxVelocity,
            double maxAccel, double powerRate, double power, double angle)
    {
        Vector2D v = MathTools.toCartesianCoordinates(power * powerRate, angle);
        v.normalize(maxAccel);
        v = v.add(initialVelocity);
        v.normalize(maxVelocity);

        return v;
    }

    protected Vector2D velocity(double maxVelocity, double maxAccel,
            double powerRate, double power, double angle)
    {
        return velocity(this.velocity, maxVelocity, maxAccel, powerRate, power,
                angle);
    }

    public Vector2D nextPosition(Vector2D initialPosition,
            Vector2D initialVelocity)
    {
        return initialPosition.add(initialVelocity);
    }

    public Vector2D nextPosition()
    {
        return nextPosition(position, velocity);
    }

    protected Vector2D nextPosition(Vector2D initialPosition,
            Vector2D initialVelocity, double maxVelocity, double maxAccel,
            double powerRate, double power, double angle)
    {
        return velocity(initialVelocity, maxVelocity, maxAccel, powerRate,
                power, angle).add(initialPosition);
    }

    protected Vector2D nextPosition(double maxVelocity, double maxAccel,
            double powerRate, double power, double angle)
    {
        return nextPosition(this.position, this.velocity, maxVelocity,
                maxAccel, powerRate, power, angle);
    }

    public Vector2D nextVelocity(Vector2D initialVelocity, double decay)
    {
        return initialVelocity.multiply(decay);
    }

    public Vector2D nextVelocity(double decay)
    {
        return nextVelocity(this.velocity, decay);
    }

    protected Vector2D nextVelocity(Vector2D initialVelocity, double decay,
            double maxVelocity, double maxAccel, double powerRate,
            double power, double angle)
    {
        return velocity(initialVelocity, maxVelocity, maxAccel, powerRate,
                power, angle).multiply(decay);
    }

    protected Vector2D nextVelocity(double decay, double maxVelocity,
            double maxAccel, double powerRate, double power, double angle)
    {
        return nextVelocity(this.velocity, decay, maxVelocity, maxAccel,
                powerRate, power, angle);
    }

    public ArrayList<Vector2D> trajectory(Vector2D initialPosition,
            Vector2D initialVelocity, double decay)
    {
        Vector2D oldPosition = initialPosition;
        Vector2D oldVelocity = initialVelocity;
        ArrayList<Vector2D> trajectory = new ArrayList<Vector2D>();
        trajectory.add(oldPosition);
        Vector2D newPosition = nextPosition(oldPosition, oldVelocity);

        while (newPosition.distanceTo(oldPosition) > SoccerParams.KICKABLE_MARGIN/5.0d)
        {
            trajectory.add(newPosition);
            oldPosition = newPosition;
            newPosition = nextPosition(oldPosition, oldVelocity);
            oldVelocity = nextVelocity(oldVelocity, decay);
        }

        return trajectory;
    }

    public ArrayList<Vector2D> trajectory(double decay)
    {
        return trajectory(this.position, this.velocity, decay);
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
        return "Pos: " + position + " - Vel: " + velocity;
    }

}