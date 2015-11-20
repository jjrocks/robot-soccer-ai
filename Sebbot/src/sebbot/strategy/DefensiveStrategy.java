package sebbot.strategy;

import sebbot.FullstateInfo;
import sebbot.Player;
import sebbot.RobocupClient;
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
			if (!CommonStrategies.shootToGoal(rcClient, fsi, player)) {
				State state = new State(fsi, player);
				Action action = ballCaptureAlgorithm.chooseAction(state);

				rcClient.getBrain().getActionsQueue()
						.addLast(new PlayerAction(action, rcClient));
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