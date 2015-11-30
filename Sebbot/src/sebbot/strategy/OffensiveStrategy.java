package sebbot.strategy;

import sebbot.*;
import sebbot.ballcapture.Action;
import sebbot.ballcapture.State;

/**
 * @author Sebastien Lentz
 *
 */
public class OffensiveStrategy implements Strategy
{
    
    private sebbot.ballcapture.Policy ballCaptureAlgorithm;
    private Vector2D startPos = new Vector2D(0, 0); // home place
    //private int homeY = 0; // the y coordinate we default to
    //private int homeX = 0;
    private int range = 15; // how far from the ball will the player react?

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
    public OffensiveStrategy(sebbot.ballcapture.Policy ballCaptureAlgorithm)
    {
        this.ballCaptureAlgorithm = ballCaptureAlgorithm;
    }
    
    /**
     * 
     */
    public OffensiveStrategy()
    {
        this(new sebbot.ballcapture.HandCodedPolicy());
    }
    
    /*
     * =========================================================================
     * 
     *                      Getters and Setters
     * 
     * =========================================================================
     */
    /**
     * @return the ballCaptureAlgorithm
     */
    public sebbot.ballcapture.Policy getBallCaptureAlgorithm()
    {
        return ballCaptureAlgorithm;
    }

    /**
     * @param ballCaptureAlgorithm the ballCaptureAlgorithm to set
     */
    public void setBallCaptureAlgorithm(sebbot.ballcapture.Policy ballCaptureAlgorithm)
    {
        this.ballCaptureAlgorithm = ballCaptureAlgorithm;
    }
    
    /*
     * =========================================================================
     * 
     *                      Main methods
     * 
     * =========================================================================
     */
    /* (non-Javadoc)
     * @see sebbot.strategy.Strategy#doAction(sebbot.RobocupClient, sebbot.FullstateInfo, sebbot.Player)
     */
    @Override
    public void doAction(RobocupClient rcClient, FullstateInfo fsi,
                         Player player)
    {
        if (!fsi.seesBall)
        {
            PlayerAction action = new PlayerAction(PlayerActionType.TURN, 0.0d, 20,rcClient);
            // If we can't see the ball clear all possible actions cause we first gotta find it.
            rcClient.getBrain().getActionsQueue().clear();
            rcClient.getBrain().getActionsQueue().addFirst(action);
            return;
        }
        if (checkY(rcClient,fsi,player)) {
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
			// go to position
			if(checkX(rcClient,fsi,player)){
				// go near goal
				Vector2D nearGoal = new Vector2D(startPos.getX()*2.5,startPos.getY()/2);
				CommonStrategies.simpleGoTo(nearGoal, rcClient, fsi, player);
			} else {
				// go home
				CommonStrategies.simpleGoTo(startPos, rcClient, fsi, player);
			}
		}
    }
    
    /** Checks to see if the ball's y coordinate is in a place where this player should go for it
     * @param rcClient
     * @param fsi
     * @param player
     * @return true if the player should move to it
     */
    public boolean checkY(RobocupClient rcClient, FullstateInfo fsi,
            Player player){
    	if(fsi.getBall().distanceTo(new Vector2D(fsi.getBall().getPosition().getX(),startPos.getY()))<range){
    		return true;
    	}
    	return false;
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
    	//startPos = new Vector2D(player.isLeftSide() ? -52.5d : 52.5d, player.getPosition().getY());
    	if(player.isLeftSide()){
    		if(fsi.getBall().getPosition().getX()>0){
    			return true;
    		}
    	} else {
    		if(fsi.getBall().getPosition().getX()<0){
    			return true;
    		}
    	}
    	
    	return false;
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
