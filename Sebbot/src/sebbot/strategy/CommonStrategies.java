package sebbot.strategy;

import sebbot.Ball;
import sebbot.FullstateInfo;
import sebbot.MobileObject;
import sebbot.Player;
import sebbot.PlayerAction;
import sebbot.PlayerActionType;
import sebbot.RobocupClient;
import sebbot.SoccerParams;
import sebbot.Vector2D;

/**
 * @author Sebastien Lentz
 *
 */
public class CommonStrategies
{
    /**
     * @param rcClient
     * @param fsi
     * @param player
     * @return
     */
    public static boolean shootToGoal(RobocupClient rcClient,
                                      FullstateInfo fsi, Player player)
    {
        if (player.distanceTo(fsi.getBall()) <= SoccerParams.KICKABLE_MARGIN)
        { // The ball is in the kickable margin => kick it towards the goal!
            double goalPosX = player.isLeftSide() ? 52.5d : -52.5d;
            
            // if goal is reachable, kick full strength
            double kickVelocity = 0;
            if(player.distanceTo(new Vector2D(goalPosX,0))<30){
            	kickVelocity = 100d;
            } else {
            	// else dribble
            	kickVelocity = 10d;
            }
            PlayerAction action = new PlayerAction(PlayerActionType.KICK,
                kickVelocity, player.angleFromBody(goalPosX, 0.0d), rcClient);

            rcClient.getBrain().getActionsQueue().addLast(action);

            return true; // Strategy succeeded.
        }
        else
        {
            return false; // Strategy failed.
        }
    }
    
    /**
     * @param rcClient
     * @param fsi
     * @param player
     * @param position
     * @return
     */
    public static boolean shootToPos(RobocupClient rcClient,
                                      FullstateInfo fsi, Player player, Vector2D position)
    {
        if (player.distanceTo(fsi.getBall()) <= SoccerParams.KICKABLE_MARGIN)
        { // The ball is in the kickable margin => kick it towards the goal!
            //double goalPosX = player.isLeftSide() ? 52.5d : -52.5d;
        	
        	// get kick velocity
        	double velocity = Math.min(100d, Math.max(10d,player.distanceTo(fsi.getBall())));

            PlayerAction action = new PlayerAction(PlayerActionType.KICK,
                velocity, player.angleFromBody(position.getX(), position.getY()), rcClient);

            rcClient.getBrain().getActionsQueue().addLast(action);

            return true; // Strategy succeeded.
        }
        else
        {
            return false; // Strategy failed.
        }
    }

    /**
     * @param position
     * @param c
     * @param fsi
     * @param p
     * @return
     */
    public static boolean simpleGoTo(Vector2D position, RobocupClient c,
                                     FullstateInfo fsi, Player p)
    {
        if (p.distanceTo(position) > SoccerParams.KICKABLE_MARGIN)
        { // We are too far away from the position.           

            if (Math.abs(p.angleFromBody(position)) < 36.0d)
            { // The player is directed at the position.
                PlayerAction action = new PlayerAction(PlayerActionType.DASH,
                    100.0d, 0.0d, c);
                c.getBrain().getActionsQueue().addLast(action);
            }

            else
            { // The player needs to turn in the direction of the position.
                PlayerAction action = new PlayerAction(PlayerActionType.TURN,
                    0.0d, p.angleFromBody(position), c);
                c.getBrain().getActionsQueue().addLast(action);
            }

            return false; // Order is not yet accomplished.
        }
        else
        {
            return true; // Order is accomplished.
        }
    }

    /**
     * @param o
     * @param s
     * @param fsi
     * @param p
     * @return
     */
    public static boolean simpleGoTo(MobileObject o, RobocupClient s,
                                     FullstateInfo fsi, Player p)
    {
        return simpleGoTo(o.getPosition(), s, fsi, p);
    }
    
    /**
	 * Pass to someone closer to the goal
	 * 
	 * @param c
	 *            The client
	 * @param fsi
	 *            The fullstateinfo
	 * @param p
	 *            The Player
	 * @return True if successful, false if fails or the player is already
	 *         closest to the goal
	 * */
	public static boolean simplePass(RobocupClient c, FullstateInfo fsi, Player p) {
		Ball ball = fsi.getBall();
		Player[] team = p.isLeftSide() ? fsi.getLeftTeam() : fsi.getRightTeam();

		int numberOfPlayers = team.length;

		// goal coords
		Vector2D goalPos = new Vector2D(p.isLeftSide() ? 52.0d : -52.0d, 0);
		/* Find which player in the team is the closest to the goal */
		Player closestToTheGoal = p;
		for (int i = 0; i < numberOfPlayers; i++) {
			if ((team[i] != p)
					&& (team[i].distanceTo(ball) < closestToTheGoal
							.distanceTo(goalPos))) {
				closestToTheGoal = team[i];
			}
		}

		/* The kick to the player closest to the goal */
		if (closestToTheGoal != p) {
			return CommonStrategies.shootToPos(c, fsi, p,
					closestToTheGoal.getPosition());
		}
		return false;
	}
}
