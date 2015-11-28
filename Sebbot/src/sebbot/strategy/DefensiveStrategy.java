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
 * 
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
			CommonStrategies.simpleGoTo(startPos, rcClient, fsi, player);
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
    	return ((player.getPosition().getX()>0==fsi.getBall().getPosition().getX()>0));
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
