package sebbot;

import java.lang.Math;
import java.util.ArrayDeque;
import sebbot.strategy.Strategy;

/**
 * 
 * 
 * @author Sebastien Lentz
 *
 */
public class Brain implements Runnable
{
    /*
     * Private members.
     */
    private RobocupClient            robocupClient; // For communicating with the server 
    private FullstateInfo            fullstateInfo; // Contains all info about the
                                                    //   current state of the game
    private Player                   player;       // The player this brain controls
    private Strategy                 strategy;     // Strategy used by this brain
    private ArrayDeque<PlayerAction> actionsQueue; // Contains the actions to be executed.

    /*
     * =========================================================================
     * 
     *                     Constructors and destructors
     * 
     * =========================================================================
     */
    /**
     * Constructor.
     * 
     * @param robocupClient
     * @param teamSide
     * @param playerNumber
     * @param strategy
     */
    public Brain(RobocupClient robocupClient, boolean leftSide,
            int playerNumber, Strategy strategy)
    {
        this.robocupClient = robocupClient;
        this.fullstateInfo = new FullstateInfo("");
        this.player = leftSide ? fullstateInfo.getLeftTeam()[playerNumber - 1]
                : fullstateInfo.getRightTeam()[playerNumber - 1];
        this.strategy = strategy;
        this.actionsQueue = new ArrayDeque<PlayerAction>();
    }

    /*
     * =========================================================================
     * 
     *                      Getters and Setters
     * 
     * =========================================================================
     */
    /**
     * @return the robocupClient
     */
    public RobocupClient getRobocupClient()
    {
        return robocupClient;
    }

    /**
     * @param robocupClient the robocupClient to set
     */
    public void setRobocupClient(RobocupClient robocupClient)
    {
        this.robocupClient = robocupClient;
    }

    /**
     * @return the player
     */
    public Player getPlayer()
    {
        return player;
    }

    /**
     * @param player the player to set
     */
    public void setPlayer(Player player)
    {
        this.player = player;
    }

    /**
     * @return the strategy
     */
    public Strategy getStrategy()
    {
        return strategy;
    }

    /**
     * @param strategy the strategy to set
     */
    public void setStrategy(Strategy strategy)
    {
        this.strategy = strategy;
    }

    /**
     * @return the actionsQueue
     */
    public ArrayDeque<PlayerAction> getActionsQueue()
    {
        return actionsQueue;
    }

    /**
     * @param actionsQueue the actionsQueue to set
     */
    public void setActionsQueue(ArrayDeque<PlayerAction> actionsQueue)
    {
        this.actionsQueue = actionsQueue;
    }

    /**
     * @return the fullstateInfo
     */
    public FullstateInfo getFullstateInfo()
    {
        return fullstateInfo;
    }

    /**
     * @param fullstateInfo
     *            the fullstateInfo to set
     */
    public void setFullstateInfo(FullstateInfo fullstateInfo)
    {
        this.fullstateInfo = fullstateInfo;
    }

    /*
     * =========================================================================
     * 
     *                          Other methods
     * 
     * =========================================================================
     */
    /**
     * This is the main function of the Brain.
     */
    public void run()
    {
        // Before kick off, position the player somewhere in his side.
        robocupClient.move(-Math.random() * 52.5, Math.random() * 34.0);
        
        int lastTimeStep = 0;
        int currentTimeStep = 0;
        while (true) // TODO: change according to the play mode.
        {
            lastTimeStep = currentTimeStep;
            currentTimeStep = fullstateInfo.getTimeStep();
            if (currentTimeStep == lastTimeStep + 1)
            {
                if (actionsQueue.isEmpty())
                { // The queue is empty, check if we need to add an action.
                    strategy.doAction(robocupClient, fullstateInfo, player);
                }
                
                if (!actionsQueue.isEmpty())
                { // An action needs to be executed at this time step, so do it.
                    actionsQueue.removeFirst().execute();
                }
                
//                System.out.println(fullstateInfo.getTimeStep() + ": " + player + " " + fullstateInfo.getBall());
//                System.out.println("Next position: " + player.nextPosition(100.0d));
//                System.out.println("Next velocity: " + player.nextVelocity(100.0d));

            }
            else if (currentTimeStep != lastTimeStep)
            {
                System.out.println("A time step has been skipped:");
                System.out.println("Last time step: " + lastTimeStep);
                System.out.println("Current time step: " + currentTimeStep);
            }
            
            // Wait for next cycle before sending another command.
            try
            {
                Thread.sleep(SoccerParams.SIMULATOR_STEP / 5);
            }
            catch (Exception e)
            {
                System.err.println(e);
            }
        }

    }
}
