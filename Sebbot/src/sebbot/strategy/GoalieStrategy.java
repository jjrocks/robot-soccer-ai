package sebbot.strategy;

import sebbot.FullstateInfo;
import sebbot.Player;
import sebbot.RobocupClient;

/**
 * Created by JJ on 10/22/2015.
 */
public class GoalieStrategy implements Strategy {
    @Override
    public void doAction(RobocupClient rcClient, FullstateInfo fsi, Player player) {
        System.out.println("This is player: " + player.getUniformNumber());
    }



    public void moveUpYAxis(Player player) {
        // The only thing this should do is move up the y axis
    }
}
