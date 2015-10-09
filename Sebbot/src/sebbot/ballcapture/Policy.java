package sebbot.ballcapture;

/**
 * @author Sebastien Lentz
 *
 */
public interface Policy
{
    public Action chooseAction(State s);
    public String getName();
}
