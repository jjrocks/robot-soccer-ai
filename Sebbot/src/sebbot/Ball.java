package sebbot;

import java.util.ArrayList;

/**
 * @author Sebastien Lentz
 *
 */
public class Ball extends MobileObject
{

    /**
     * @param posX
     * @param posY
     * @param velX
     * @param velY
     */
    public Ball(double posX, double posY, double velX, double velY)
    {
        super(posX, posY, velX, velY);
    }

    public Vector2D nextPosition(Vector2D initialPosition,
            Vector2D initialVelocity, double power, double ballShootDirection,
            double playerBodyDirection, double playerDistanceToBall)
    {
        power *= (1.0d - 0.25d * Math.abs(ballShootDirection
                - playerBodyDirection) / 180 - 0.25d * playerDistanceToBall
                / SoccerParams.KICKABLE_MARGIN);
        double angle = MathTools.normalizeAngle(playerBodyDirection
                + ballShootDirection);

        return super.nextPosition(initialPosition, initialVelocity,
                SoccerParams.BALL_SPEED_MAX, SoccerParams.BALL_ACCEL_MAX,
                SoccerParams.KICK_POWER_RATE, power, angle);
    }
    
    public Vector2D nextPosition(double power, double ballShootDirection,
            double playerBodyDirection, double playerDistanceToBall)
    {
        return this.nextPosition(this.position, this.velocity, power,
                ballShootDirection, playerBodyDirection, playerDistanceToBall);
    }

    public Vector2D nextVelocity()
    {
        return super.nextVelocity(SoccerParams.BALL_DECAY);
    }

    public Vector2D nextVelocity(Vector2D initialVelocity)
    {
        return super.nextVelocity(initialVelocity, SoccerParams.BALL_DECAY);
    }

    public Vector2D nextVelocity(double power, double angle)
    {
        return super.nextVelocity(SoccerParams.BALL_DECAY,
                SoccerParams.BALL_SPEED_MAX, SoccerParams.BALL_ACCEL_MAX,
                SoccerParams.KICK_POWER_RATE, power, angle);
    }

    public Vector2D nextVelocity(Vector2D initialVelocity, double power,
            double angle)
    {
        return super.nextVelocity(initialVelocity, SoccerParams.BALL_DECAY,
                SoccerParams.BALL_SPEED_MAX, SoccerParams.BALL_ACCEL_MAX,
                SoccerParams.KICK_POWER_RATE, power, angle);
    }
    
    public ArrayList<Vector2D> trajectory(Vector2D initialPosition,
            Vector2D initialVelocity)
    {
        return super.trajectory(initialPosition, initialVelocity, SoccerParams.BALL_DECAY);
    }

    public ArrayList<Vector2D> trajectory()
    {
        return trajectory(this.position, this.velocity);
    }

}
