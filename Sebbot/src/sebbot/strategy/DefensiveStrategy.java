package sebbot.strategy;

import java.util.HashSet;

import sebbot.*;
import sebbot.ballcapture.Action;
import sebbot.ballcapture.State;

/**
 * Created by Bret Black 11/3/2015
 * 
 * @author Bret Black
 */
public class DefensiveStrategy implements Strategy {
	private int range = 10; // point at which defenders consider the ball (dist from center)
	private int attackRange = 15; // the point at which the defenders swarm the ball (dist from player)
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
		//Vector2D endPos = new Vector2D(player.isLeftSide() ? -52.5d : 52.5d,
		//		player.getPosition().getY());
		// startPos = new Vector2D(player.isLeftSide() ? -52.5d : 52.5d, player
		// .getPosition().getY());

		// ball position
		//Vector2D ballPos = fsi.getBall().getPosition();

		// kick the ball if it is in range
		// ball is NOT in range, try different strategy (inspired by
		// GoToBallAndShoot)
		if (checkX(rcClient, fsi, player)/*fsi.getBall().distanceTo(startPos) <= range*/) {
			// if ball is close to player, move towards it and kick (code taken
			// from GoToBallAndShoot)
			if (!CommonStrategies.simplePass(rcClient, fsi, player)) {
				if (!CommonStrategies.shootToGoal(rcClient, fsi, player)) {
					State state = new State(fsi, player);
					Action action = ballCaptureAlgorithm.chooseAction(state);

					rcClient.getBrain().getActionsQueue()
							.addLast(new PlayerAction(action, rcClient));
				}
			}
		} else {
			// normalize startPos x
			startPos.setX(player.isLeftSide() ? -Math.abs(startPos.getX())
					: Math.abs(startPos.getX()));

			// go to position
			if(CommonStrategies.simpleGoTo(startPos, rcClient, fsi, player, 4)) {
				// Survey the land for a little bit.
				PlayerAction playerAction = new PlayerAction(PlayerActionType.TURN, 0.0d, 25, rcClient);
				rcClient.getBrain().getActionsQueue().addLast(playerAction);
			}
		}
	}
	
	/** Checks to see if the ball is on the correct half of the field
     * 
     * @param rcClient
     * @param fsi
     * @param player
     * @return
     */
    public boolean checkX(RobocupClient rcClient,FullstateInfo fsi,
            Player player){
		boolean correctSide = ((player.getPosition().getX()>0==fsi.getBall().getPosition().getX()>0));
		boolean inRange = Math.abs(fsi.getBall().getPosition().getX()) > range;
		boolean attack = (int)player.distanceTo(fsi.getBall()) < attackRange;
		
		// consider all cases
    	return (correctSide && inRange && closestToBall(rcClient,fsi,player)) || (attack && correctSide);
    	//startPos = new Vector2D(player.isLeftSide() ? -52.5d : 52.5d, player.getPosition().getY());
    	/*if(player.isLeftSide()){
    		if(fsi.getBall().getPosition().getX()<0){
    			return true;
    		}
    	} else {
    		if(fsi.getBall().getPosition().getX()>0){
    			return true;
    		}
    	}
    	
    	return false;*/
    }
    
    public boolean closestToBall(RobocupClient rcClient, FullstateInfo fsi, Player p){
    	Ball ball = fsi.getBall();
		Player[] team = p.isLeftSide() ? fsi.getLeftTeam() : fsi.getRightTeam();

		int numberOfPlayers = team.length;

		// goal coords
		Vector2D goalPos = new Vector2D(p.isLeftSide() ? 52.0d : -52.0d, 0);
		/* Find which player in the team is the closest to the goal */
		Player closestToTheBall = p;
		for (int i = 0; i < numberOfPlayers; i++) {
			if ((team[i] != p)
					&& (team[i].distanceTo(ball.getPosition()) < closestToTheBall
							.distanceTo(ball.getPosition()))) {
				closestToTheBall = team[i];
			}
		}
		
		return (closestToTheBall.getPlayerNumber() == p.getPlayerNumber());
    }

	/*
	 * =========================================================================
	 * 
	 * SETTERS AND GETTERS
	 * 
	 * =========================================================================
	 */

	/**
	 * 
	 * @param pos
	 *            The start position
	 */
	public void setStartPos(Vector2D pos) {
		startPos = pos;
	}
}
