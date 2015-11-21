package sebbot.strategy;

import java.util.HashSet;

import sebbot.Ball;
import sebbot.FullstateInfo;
import sebbot.Player;
import sebbot.RobocupClient;
import sebbot.SoccerParams;
import sebbot.Vector2D;
import sebbot.ballcapture.Action;
import sebbot.ballcapture.State;
import sebbot.PlayerAction;

/**
 * Created by Bret Black 11/3/2015
 * @author Bret Black
 */
public class DefensiveStrategy implements Strategy {
	private int range = 30;
	private int maxDistanceToEnd = 52;
	private Vector2D startPos = new Vector2D(0, 0); // home place
	private sebbot.ballcapture.Policy ballCaptureAlgorithm;

	/*
	 * =========================================================================
	 * 
	 * Constructors and destructors
	 * 
	 * =========================================================================
	 */

	/**
	 * @param ballCaptureAlgorithm
	 */
	public DefensiveStrategy(sebbot.ballcapture.Policy ballCaptureAlgorithm) {
		this.ballCaptureAlgorithm = ballCaptureAlgorithm;
	}

	/**
     * 
     */
	public DefensiveStrategy() {
		this(new sebbot.ballcapture.HandCodedPolicy());
	}

	/*
	 * =========================================================================
	 * 
	 * Main methods
	 * 
	 * =========================================================================
	 */

	@Override
	public void doAction(RobocupClient rcClient, FullstateInfo fsi,
			Player player) {
		// save goal position to track if we are too far
		Vector2D endPos = new Vector2D(player.isLeftSide() ? -52.5d : 52.5d,
				player.getPosition().getY());
		//startPos = new Vector2D(player.isLeftSide() ? -52.5d : 52.5d, player
		//		.getPosition().getY());

		// kick the ball if it is in range
		// ball is NOT in range, try different strategy (taken from
		// GoToBallAndShoot)
		// check distance
		if (player.distanceTo(fsi.getBall()) <= range
				&& player.distanceTo(endPos) <= maxDistanceToEnd) {
			// if ball is close to player, move towards it and kick (code taken
			// from GoToBallAndShoot)
			if(!simplePass(rcClient,fsi,player)){
				if (!CommonStrategies.shootToGoal(rcClient, fsi, player)) {
					State state = new State(fsi, player);
					Action action = ballCaptureAlgorithm.chooseAction(state);
	
					rcClient.getBrain().getActionsQueue()
							.addLast(new PlayerAction(action, rcClient));
				}
			}
		} else {
			// if ball is far from player, return home
			//if (player.distanceTo(endPos) >= maxDistanceToEnd / 2) {
				// normalize startPos x
				startPos.setX(player.isLeftSide() ? -Math.abs(startPos.getX()) : Math.abs(startPos.getX()));
				
				// go to position
				CommonStrategies.simpleGoTo(startPos, rcClient, fsi, player);
			//}
		}
	}
	
	/** Pass to someone closer to the goal 
	 * @param c The client
	 * @param fsi The fullstateinfo
	 * @param p The Player
	 * @return True if successful, false if fails or the player is already closest to the goal
	 * */
	public boolean simplePass(RobocupClient c, FullstateInfo fsi, Player p)
    {
        Ball ball = fsi.getBall();
        Player[] team = p.isLeftSide() ? fsi.getLeftTeam() : fsi.getRightTeam();
        
        int numberOfPlayers = team.length;

        // goal coords
        Vector2D goalPos = new Vector2D( p.isLeftSide() ? 52.0d : -52.0d,0);
        /* Find which player in the team is the closest to the goal */
        Player closestToTheGoal = p;
        for (int i = 0; i < numberOfPlayers; i++)
        {
            if ((team[i] != p)
                    && (team[i].distanceTo(ball) < closestToTheGoal.distanceTo(goalPos)))
            {
                closestToTheGoal = team[i];
            }
        }

        /* The kick to the player closest to the goal */
        if (closestToTheGoal != p)
        {
            return CommonStrategies.shootToPos(c, fsi, p, closestToTheGoal.getPosition());
        }
        return false;

//        else if (numberOfPlayers == 5)
//        {
//            /* Find the width and length of the rectangle area to cover. */
//            double w = SoccerParams.FIELD_WIDTH;
//            double l = SoccerParams.FIELD_LENGTH / 2;
//            if (p.isLeftSide())
//            {
//                l += ball.getPosition().getX();
//            }
//            else
//            {
//                l -= ball.getPosition().getX();
//            }
//
//            /* 
//             * Compute the radius each player would have to cover if they were 
//             * either in a rectangle formation, either in a line formation.
//             */
//            double rectangleFormationMaxDist = Math.sqrt(Math.pow(w / 4, 2)
//                    + Math.pow(l / 4, 2));
//            double lineFormationMaxDist = Math.sqrt(Math.pow(w / 8, 2)
//                    + Math.pow(l / 2, 2));
//
//            /* 
//             * We now choose a formation (rectangle or line) and compute the 4
//             * positions the players should go to.
//             */
//            if (lineFormationMaxDist < rectangleFormationMaxDist)
//            { // We choose a line formation.
//                double lineAbscissa = p.isLeftSide() ? l / 2
//                        - SoccerParams.FIELD_LENGTH / 2
//                        : SoccerParams.FIELD_LENGTH / 2 - l / 2;
//
//                optimalPositions[0] = new Vector2D(lineAbscissa, -w * 3 / 8);
//                optimalPositions[1] = new Vector2D(lineAbscissa, -w * 1 / 8);
//                optimalPositions[2] = new Vector2D(lineAbscissa, w * 1 / 8);
//                optimalPositions[3] = new Vector2D(lineAbscissa, w * 3 / 8);
//            }
//
//            else
//            { // We choose a rectangle formation.
//                double sideAbscissa = p.isLeftSide() ? l / 4
//                        - SoccerParams.FIELD_LENGTH / 2
//                        : SoccerParams.FIELD_LENGTH / 2 - l / 4;
//
//                optimalPositions[0] = new Vector2D(sideAbscissa, -0.25d * w);
//                optimalPositions[1] = new Vector2D(sideAbscissa, 0.25d * w);
//
//                sideAbscissa = p.isLeftSide() ? l * 3 / 4
//                        - SoccerParams.FIELD_LENGTH / 2
//                        : SoccerParams.FIELD_LENGTH / 2 - l * 3 / 4;
//
//                optimalPositions[2] = new Vector2D(sideAbscissa, -0.25d * w);
//                optimalPositions[3] = new Vector2D(sideAbscissa, 0.25d * w);
//            }
//
//            /* 
//             * Each point will now be assigned to the player who is the closest 
//             * among those remaining. We stop searching as soon as we found
//             * the point for this player.
//             */
//            Vector2D targetPoint = null;
//            Player closestToTargetPoint = null;
//            HashSet<Player> playersSet = new HashSet<Player>();
//            for (int i = 0; i < numberOfPlayers; i++)
//            {
//                if (team[i] != closestToTheBall)
//                {
//                    playersSet.add(team[i]);
//                }
//            }
//            for (int i = 0; i < optimalPositions.length; i++)
//            {
//                targetPoint = optimalPositions[i];
//                closestToTargetPoint = playersSet.iterator().next();
//                for (Player p2 : playersSet)
//                {
//                    if (p2.distanceTo(targetPoint) < closestToTargetPoint
//                        .distanceTo(targetPoint))
//                    {
//                        closestToTargetPoint = p2;
//                    }
//                }
//
//                playersSet.remove(closestToTargetPoint);
//
//                if (closestToTargetPoint == p)
//                {
//                    break;
//                }
//            }
//
//            /* The player knows where he has to go and begins moving */
//            CommonStrategies.simpleGoTo(targetPoint, c, fsi, p);
//        }

    }
	/*
     * =========================================================================
     * 
     *                      SETTERS AND GETTERS
     * 
     * =========================================================================
     */
	
	/**
	 * 
	 * @param pos The start position
	 */
	public void setStartPos(Vector2D pos){
		startPos = pos;
	}
}
