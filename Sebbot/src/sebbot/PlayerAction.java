package sebbot;

/**
 * @author Sebastien Lentz
 *
 */
public class PlayerAction
{
    /*
     * Private members
     */
    private PlayerActionType actionType; // "DASH", "KICK" or "TURN".
    private double           power;     // The power arg of a KICK or DASH cmd.
    private double           direction; // The direction arg of a KICK or TURN cmd.
    private RobocupClient    client;    // The client to send actions to the server.

    private static int       turnCount = 0;
    private static int       dashCount = 0;
    private static int       kickCount = 0;

    /*
     * =========================================================================
     * 
     *                     Constructors and destructors
     * 
     * =========================================================================
     */
    /**
     * @param actionType
     * @param power
     * @param direction
     * @param client
     */
    public PlayerAction(PlayerActionType actionType, double power,
            double direction, RobocupClient client)
    {
        this.actionType = actionType;
        this.power = power;
        this.direction = direction;
        this.client = client;
    }
    
    /**
     * @param action
     * @param client
     */
    public PlayerAction(sebbot.ballcapture.Action action, RobocupClient client)
    {
        this.actionType = action.isTurn() ? actionType.TURN : actionType.DASH;
        this.power = action.isTurn() ? 0f : action.getValue();
        this.direction = action.isTurn() ? action.getValue() : 0f;
        this.client = client;
    }    
    

    /*
     * =========================================================================
     * 
     *                      Getters and Setters
     * 
     * =========================================================================
     */
    /**
     * @return the turnCount
     */
    public static int getTurnCount()
    {
        return turnCount;
    }

    /**
     * @return the dashCount
     */
    public static int getDashCount()
    {
        return dashCount;
    }

    /**
     * @return the kickCount
     */
    public static int getKickCount()
    {
        return kickCount;
    }
    
    /**
     * @return the actionType
     */
    public PlayerActionType getActionType()
    {
        return actionType;
    }

    /**
     * @param actionType the actionType to set
     */
    public void setActionType(PlayerActionType actionType)
    {
        this.actionType = actionType;
    }

    /**
     * @return the power
     */
    public double getPower()
    {
        return power;
    }

    /**
     * @param power the power to set
     */
    public void setPower(double power)
    {
        this.power = power;
    }

    /**
     * @return the direction
     */
    public double getDirection()
    {
        return direction;
    }

    /**
     * @param direction the direction to set
     */
    public void setDirection(double direction)
    {
        this.direction = direction;
    }

    /*
     * =========================================================================
     * 
     *                          Other methods
     * 
     * =========================================================================
     */
    /**
     * This method sends this action to the server.
     */
    public void execute()
    {
        switch (actionType)
        {
        case DASH:
            client.dash(power);
            dashCount++;
            break;
        case KICK:
            client.kick(power, direction);
            kickCount++;
            break;
        case TURN:
            client.turn(direction);
            turnCount++;
            break;
        default:
            break;
        }
    }
}
