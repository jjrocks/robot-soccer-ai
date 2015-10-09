package sebbot.strategy;
import sebbot.FullstateInfo;
import sebbot.Player;
import sebbot.RobocupClient;


/**
 * @author Sebastien Lentz
 *
 */
public interface Strategy
{
    public void doAction(RobocupClient rcClient, FullstateInfo fsi, Player player);
}
