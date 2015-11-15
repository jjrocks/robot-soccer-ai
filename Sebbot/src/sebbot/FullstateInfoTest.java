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

        FullstateInfo fullstateInfo = new FullstateInfo("Wooooo");
        fullstateInfo.calculatePosition("f t l 50", 40, -90);

    }
}