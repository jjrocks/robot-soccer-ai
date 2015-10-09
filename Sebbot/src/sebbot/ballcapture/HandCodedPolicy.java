package sebbot.ballcapture;

/**
 * @author Sebastien Lentz
 *
 */
public class HandCodedPolicy implements Policy
{
    /**
     * 
     */
    public HandCodedPolicy()
    {
        
    }

    /* (non-Javadoc)
     * @see sebbot.learning.Policy#chooseAction(sebbot.learning.State)
     */
    @Override
    public Action chooseAction(State s)
    {
        Action action;
        if (Math.abs(s.getRelativeDirection()) > 36f)
        {
            action = new Action(s.getRelativeDirection(),true);
        }
        else
        {
            action = new Action(100f,false);
        }
        
        return action;
    }

    /* (non-Javadoc)
     * @see sebbot.learning.Policy#getName()
     */
    @Override
    public String getName()
    {
        return "Hand-coded simple go to ball";
    }

}
