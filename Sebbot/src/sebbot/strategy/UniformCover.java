package sebbot.strategy;

import java.util.HashSet;

import sebbot.Ball;
import sebbot.FullstateInfo;
import sebbot.Player;
import sebbot.RobocupClient;
import sebbot.Vector2D;
import sebbot.SoccerParams;

/**
 * @author Sebastien Lentz
 *
 */
public class UniformCover implements Strategy
{
    static GoToBallAndShoot goToBallStrategy = new GoToBallAndShoot();

    protected int           numberOfPlayers;
    protected Vector2D[]    optimalPositions;

    /*
     * =========================================================================
     * 
     *                     Constructors and destructors
     * 
     * =========================================================================
     */
    /**
     * @param numberOfplayers
     */
    public UniformCover(int numberOfplayers)
    {
        this.numberOfPlayers = numberOfplayers;
        this.optimalPositions = new Vector2D[numberOfplayers - 1];
    }

    /*
     * =========================================================================
     * 
     *                      Getters and Setters
     * 
     * =========================================================================
     */
    /**
     * @return the goToBallStrategy
     */
    public static GoToBallAndShoot getGoToBallStrategy()
    {
        return goToBallStrategy;
    }

    /**
     * @param goToBallStrategy the goToBallStrategy to set
     */
    public static void setGoToBallStrategy(GoToBallAndShoot goToBallStrategy)
    {
        UniformCover.goToBallStrategy = goToBallStrategy;
    }

    /*
     * =========================================================================
     * 
     *                      Main methods
     * 
     * =========================================================================
     */
    public void doAction(RobocupClient c, FullstateInfo fsi, Player p)
    {
        Ball ball = fsi.getBall();
        Player[] team = p.isLeftSide() ? fsi.getLeftTeam() : fsi.getRightTeam();

        /* Find which player in the team is the closest to the ball */
        Player closestToTheBall = p;
        for (int i = 0; i < numberOfPlayers; i++)
        {
            if ((team[i] != p)
                    && (team[i].distanceTo(ball) < p.distanceTo(ball)))
            {
                closestToTheBall = team[i];
            }
        }

        /* The closest player goes to the ball go to the ball and kicks it. */
        if (closestToTheBall == p)
        {
            goToBallStrategy.doAction(c, fsi, p);
        }

        else if (numberOfPlayers == 5)
        {
            /* Find the width and length of the rectangle area to cover. */
            double w = SoccerParams.FIELD_WIDTH;
            double l = SoccerParams.FIELD_LENGTH / 2;
            if (p.isLeftSide())
            {
                l += ball.getPosition().getX();
            }
            else
            {
                l -= ball.getPosition().getX();
            }

            /* 
             * Compute the radius each player would have to cover if they were 
             * either in a rectangle formation, either in a line formation.
             */
            double rectangleFormationMaxDist = Math.sqrt(Math.pow(w / 4, 2)
                    + Math.pow(l / 4, 2));
            double lineFormationMaxDist = Math.sqrt(Math.pow(w / 8, 2)
                    + Math.pow(l / 2, 2));

            /* 
             * We now choose a formation (rectangle or line) and compute the 4
             * positions the players should go to.
             */
            if (lineFormationMaxDist < rectangleFormationMaxDist)
            { // We choose a line formation.
                double lineAbscissa = p.isLeftSide() ? l / 2
                        - SoccerParams.FIELD_LENGTH / 2
                        : SoccerParams.FIELD_LENGTH / 2 - l / 2;

                optimalPositions[0] = new Vector2D(lineAbscissa, -w * 3 / 8);
                optimalPositions[1] = new Vector2D(lineAbscissa, -w * 1 / 8);
                optimalPositions[2] = new Vector2D(lineAbscissa, w * 1 / 8);
                optimalPositions[3] = new Vector2D(lineAbscissa, w * 3 / 8);
            }

            else
            { // We choose a rectangle formation.
                double sideAbscissa = p.isLeftSide() ? l / 4
                        - SoccerParams.FIELD_LENGTH / 2
                        : SoccerParams.FIELD_LENGTH / 2 - l / 4;

                optimalPositions[0] = new Vector2D(sideAbscissa, -0.25d * w);
                optimalPositions[1] = new Vector2D(sideAbscissa, 0.25d * w);

                sideAbscissa = p.isLeftSide() ? l * 3 / 4
                        - SoccerParams.FIELD_LENGTH / 2
                        : SoccerParams.FIELD_LENGTH / 2 - l * 3 / 4;

                optimalPositions[2] = new Vector2D(sideAbscissa, -0.25d * w);
                optimalPositions[3] = new Vector2D(sideAbscissa, 0.25d * w);
            }

            /* 
             * Each point will now be assigned to the player who is the closest 
             * among those remaining. We stop searching as soon as we found
             * the point for this player.
             */
            Vector2D targetPoint = null;
            Player closestToTargetPoint = null;
            HashSet<Player> playersSet = new HashSet<Player>();
            for (int i = 0; i < numberOfPlayers; i++)
            {
                if (team[i] != closestToTheBall)
                {
                    playersSet.add(team[i]);
                }
            }
            for (int i = 0; i < optimalPositions.length; i++)
            {
                targetPoint = optimalPositions[i];
                closestToTargetPoint = playersSet.iterator().next();
                for (Player p2 : playersSet)
                {
                    if (p2.distanceTo(targetPoint) < closestToTargetPoint
                        .distanceTo(targetPoint))
                    {
                        closestToTargetPoint = p2;
                    }
                }

                playersSet.remove(closestToTargetPoint);

                if (closestToTargetPoint == p)
                {
                    break;
                }
            }

            /* The player knows where he has to go and begins moving */
            CommonStrategies.simpleGoTo(targetPoint, c, fsi, p);
        }

    }

}
