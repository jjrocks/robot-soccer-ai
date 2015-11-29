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

        FullstateInfo fullstateInfo = new FullstateInfo("Wooooo", new Player(0, 0, 0, 0, true, 0, 0, 0), null);
        fullstateInfo.calculatePosition("f t l 50", 40, -90);
        Vector2D vector2D = fullstateInfo.calculatePosition("f t r 30", 100.5, 36);
        vector2D = fullstateInfo.calculatePosition("f p b c", 20, 50);
        vector2D = fullstateInfo.calculatePosition("g l", 20, 9);
        vector2D = fullstateInfo.calculatePosition("f t l 10", 20, -37);
        vector2D = fullstateInfo.calculatePosition("f c b", 5, 0);
        vector2D = fullstateInfo.calculatePosition("f b r 40", 64.1, 22);
        System.out.println(vector2D);

    }
}