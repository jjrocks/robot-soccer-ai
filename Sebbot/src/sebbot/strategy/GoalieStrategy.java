package sebbot.strategy;

import sebbot.*;
import sebbot.ballcapture.Action;
import sebbot.ballcapture.State;


/**
 * Created by JJ on 10/22/2015.
 * Modified by Bret 10/28/2015 and 11/3/2015
 */
public class GoalieStrategy implements Strategy {
	private int goalieRange = 15;
	private int maxDistanceToGoal = 10;
	private sebbot.ballcapture.Policy ballCaptureAlgorithm;
    

    /*
     * =========================================================================
     * 
     *                     Constructors and destructors
     * 
     * =========================================================================
     */
	
	
	/**
     * @param ballCaptureAlgorithm
     */
    public GoalieStrategy(sebbot.ballcapture.Policy ballCaptureAlgorithm)
    {
        this.ballCaptureAlgorithm = ballCaptureAlgorithm;
    }
    
    /**
     * 
     */
    public GoalieStrategy()
    {
        this(new sebbot.ballcapture.HandCodedPolicy());
    }
    
    /*
     * =========================================================================
     * 
     *                      Main methods
     * 
     * =========================================================================
     */

    @Override
    public void doAction(RobocupClient rcClient, FullstateInfo fsi, Player player) {
	// save goal position
	Vector2D goalPos = new Vector2D(player.isLeftSide() ? -52.5d : 52.5d,0);
		if (fsi.noFlags)
		{
			PlayerAction playerAction = new PlayerAction(PlayerActionType.TURN, 0.0d, 45, rcClient);
			rcClient.getBrain().getActionsQueue().addFirst(playerAction);
		}

	// kick the ball if it is in range
	// ball is NOT in range, try different strategy (taken from GoToBallAndShoot)
	// check distance
	if (player.distanceTo(fsi.getBall()) <= goalieRange && player.distanceTo(goalPos) <= maxDistanceToGoal){
		// if ball is close to player, move towards it and kick (code taken from GoToBallAndShoot)
		if (!CommonStrategies.shootToGoal(rcClient, fsi, player))
		{
			State state = new State(fsi, player);
			Action action = ballCaptureAlgorithm.chooseAction(state);

			rcClient.getBrain().getActionsQueue().addLast(
			    new PlayerAction(action, rcClient));
		}
	} else {
		// if ball is far from player, move to goal
		CommonStrategies.simpleGoTo(goalPos, rcClient, fsi, player);
	}
    }
}
