package sebbot;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by JJ on 11/9/2015.
 */
public class FullstateInfoTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testCalculatePosition() throws Exception {

        Player player = new Player(-21.8454, 6.6832, 0, 0, true, 0, -49.6, 0);
        System.out.println(player.getPosition().add(new Vector2D(22.32, 32, true)));
        FullstateInfo fullstateInfo = new FullstateInfo("Wooooo", new Player(0, 0, 0, 0, true, 0, 0, 0), null);
//        fullstateInfo.calculatePosition("f t l 50", 40, -90);
        Vector2D vector2D = fullstateInfo.calculateFlag("f r b 20", 66, -6);
//        vector2D = fullstateInfo.calculatePosition("f p b c", 20, 50);
//        vector2D = fullstateInfo.calculatePosition("g l", 20, 9);
//        vector2D = fullstateInfo.calculatePosition("f t l 10", 20, -37);
//        vector2D = fullstateInfo.calculatePosition("f c b", 5, 0);
//        vector2D = fullstateInfo.calculatePosition("f b r 40", 64.1, 22);


    }
}