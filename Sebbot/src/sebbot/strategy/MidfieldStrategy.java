package sebbot.strategy;

import sebbot.FullstateInfo;
import sebbot.Player;
import sebbot.PlayerAction;
import sebbot.RobocupClient;
import sebbot.ballcapture.Action;
import sebbot.ballcapture.State;
import sebbot.Vector2D;

/**
 * @author Sebastien Lentz
 *
 */
public class MidfieldStrategy implements Strategy
{
    
    private sebbot.ballcapture.Policy ballCaptureAlgorithm;
    private int homeY = 0; // the y coordinate we default to
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
    public MidfieldStrategy(sebbot.ballcapture.Policy ballCaptureAlgorithm)
    {
        this.ballCaptureAlgorithm = ballCaptureAlgorithm;
    }
    
    /**
     * 
     */
    public MidfieldStrategy()
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
			CommonStrategies.simpleGoTo(new Vector2D(fsi.getBall().getPosition().getX(),homeY), rcClient, fsi, player);
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
    	if(fsi.getBall().distanceTo(new Vector2D(fsi.getBall().getPosition().getX(),homeY))<range){
    		return true;
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
	public void setStartPos(int pos) {
		homeY = pos;
	}
}
