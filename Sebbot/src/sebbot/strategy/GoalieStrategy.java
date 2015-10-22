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
        if(player.getUniformNumber() == 10) {
            System.out.println("I am player 10");
        }
    }
}
