package sebbot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is used to store all the game information given in a fullstate
 * message sent from the server. 
 * 
 * @author Sebastien Lentz
 * 
 */
public class FullstateInfo
{
    /*
     * Constants of the class.
     */
    final static String SERVER_PARAM_PATTERN = "\\(server_param (\\(.*\\))*\\)";
    final static String PLAYER_PARAM_PATTERN = "\\(player_param (\\(.*\\))*\\)";
    final static String PLAYER_TYPE_PATTERN = "\\(player_type (\\(.*\\))*\\)";
    final static String SEE_PATTERN = "\\(see \\d+ \\(";
    final static String SET_FLAG_PATTERN = "\\(\\((f (?:\\w )+\\d{1,2})\\) (\\-?\\d{1,3}(?:\\.\\d)? )+(\\-?\\d{1,3}(\\.\\d)?)\\)";
    final static String PLAYMODE_PATTERN  = "\\(pmode ([a-zA-Z_]*)\\)";
    final static String REAL_NB_PATTERN   = "((?:\\-)?[0-9]+(?:\\.[0-9]+(?:e(?:\\-)?[0-9]+)?)?)";
    final static String BALL_PATTERN      = "\\(\\(b\\) " + REAL_NB_PATTERN
                                                  + " " + REAL_NB_PATTERN + " "
                                                  + REAL_NB_PATTERN + " "
                                                  + REAL_NB_PATTERN + "\\)";
    final static String PLAYER_PATTERN    = "\\(\\(p ([lr]) ([0-9]{1,2}) ([g[0-9]+])\\) "
                                                  + REAL_NB_PATTERN
                                                  + " "
                                                  + REAL_NB_PATTERN
                                                  + " "
                                                  + REAL_NB_PATTERN
                                                  + " "
                                                  + REAL_NB_PATTERN
                                                  + " "
                                                  + REAL_NB_PATTERN
                                                  + " "
                                                  + REAL_NB_PATTERN;
    final static String TIME_STEP_PATTERN = "\\(fullstate ([0-9]+) \\(";

    /*
     * Members of the class.
     */
    private int         timeStep;     // The time step of the game
    private String      playMode;     // The play mode of the game
    private String      fullstateMsg; // The fullstate msg received from server
    private Ball        ball;         // The ball of the game
    private Player[]    leftTeam;     // Players of left and right team,
    private Player[]    rightTeam;    //  indexed by their uniform number - 1
    private int MAX_PLAYERS = 12;
    private boolean printedError = false;
    private int printCounter;
    private int playerNumber = 1;
    private Player player;

    /**
     * Constructor.
     * 
     * @param fullstateMsg
     */
    public FullstateInfo(String fullstateMsg)
    {
        player = new Player(0, 0, 0, 0, true, '0', 0, 1);
        this.fullstateMsg = fullstateMsg;
        this.ball = new Ball(0, 0, 0, 0);
        leftTeam = new Player[MAX_PLAYERS];
        rightTeam = new Player[MAX_PLAYERS];
        for (int i = 0; i < MAX_PLAYERS; i++)
        {
            leftTeam[i] = new Player(0, 0, 0, 0, true, '0', 0, i);
            rightTeam[i] = new Player(0, 0, 0, 0, false, '0', 0, i);
        }
    }

    /*
     * =========================================================================
     * 
     *                      Getters and Setters
     * 
     * =========================================================================
     */
    /**
     * @return the timeStep
     */
    public int getTimeStep()
    {
        return timeStep;
    }

    /**
     * @param timeStep the timeStep to set
     */
    public void setTimeStep(int timeStep)
    {
        this.timeStep = timeStep;
    }

    /**
     * @return the playMode
     */
    public String getPlayMode()
    {
        return playMode;
    }

    /**
     * @param playMode the playMode to set
     */
    public void setPlayMode(String playMode)
    {
        this.playMode = playMode;
    }

    /**
     * @return the ball
     */
    public Ball getBall()
    {
        return ball;
    }

    /**
     * @return the leftTeam
     */
    public Player[] getLeftTeam()
    {
        return leftTeam;
    }

    /**
     * @return the rightTeam
     */
    public Player[] getRightTeam()
    {
        return rightTeam;
    }

    /**
     * @return the fullstateMsg
     */
    public String getFullstateMsg()
    {
        return fullstateMsg;
    }

    /**
     * @param fullstateMsg
     *            the fullstateMsg to set
     */
    public void setFullstateMsg(String fullstateMsg)
    {
        this.fullstateMsg = fullstateMsg;
    }

    /*
     * =========================================================================
     * 
     *                          Main methods
     * 
     * =========================================================================
     */
    /**
     * This method parses the fullstateMsg string and updates the variables
     * consequently.
     */
    public void parse()
    {
//        System.out.println("Parsing data: " + fullstateMsg);
        // Gather playMode information.
        boolean skipStuff = false;
        Pattern pattern = Pattern.compile(SERVER_PARAM_PATTERN);
        Matcher matcher = pattern.matcher(fullstateMsg);
        if(matcher.find() && !skipStuff)
        {
            System.out.println("I found a server parameter");
            skipStuff = true;
        }
        else
        {
//            printErrorMessage("Server Mode");
        }

        pattern = Pattern.compile(PLAYER_PARAM_PATTERN);
        matcher = pattern.matcher(fullstateMsg);
        if(matcher.find())
        {
            System.out.println("I also found a player parameter");
            skipStuff = true;
        }
        else
        {
            if(!skipStuff)
            {
//                printErrorMessage("Player Param");
            }
        }

        // Player type repeats
        pattern = Pattern.compile(PLAYER_TYPE_PATTERN);
        matcher = pattern.matcher(fullstateMsg);
        if(matcher.find())
        {
            System.out.println("I also found the player type");
            skipStuff = true;
        }
        else
        {
            if(!skipStuff)
            {
                printErrorMessage("Player Type");
                skipStuff = true;
            }
        }

        // See Type
        pattern = Pattern.compile(SEE_PATTERN);
        matcher = pattern.matcher(fullstateMsg);
        if(matcher.find())
        {
            // So first we check to see if we know our own position
            if(true /*player.getPosition().getX() == 0*/)
            {
                pattern = Pattern.compile(SET_FLAG_PATTERN);
                matcher = pattern.matcher(fullstateMsg);
                if (matcher.find() && matcher.groupCount() == 4) {
                    // The x ranges from + or - 52.5
                    // The y ranges from + or - 34
                    System.out.println(matcher.group(0));
                    calculatePosition(matcher.group(1), Double.parseDouble(matcher.group(2)), Double.parseDouble(matcher.group(3)));
                }
            }
        }


        pattern = Pattern.compile(PLAYMODE_PATTERN);
        matcher = pattern.matcher(fullstateMsg);
        if (matcher.find())
        {
            System.out.println("Found the successful parsed data");
            this.playMode = matcher.group(1);
        }
        else
        {
            if (!skipStuff) {
                printErrorMessage("Play Mode");
            }
        }

        // Gather ball information.
        pattern = Pattern.compile(BALL_PATTERN);
        matcher = pattern.matcher(fullstateMsg);
        if (matcher.find())
        {
            ball.getPosition().setX(Double.valueOf(matcher.group(1)));
            ball.getPosition().setY(Double.valueOf(matcher.group(2)));
            ball.getVelocity().setX(Double.valueOf(matcher.group(3)));
            ball.getVelocity().setY(Double.valueOf(matcher.group(4)));
        }
        else
        {
//            System.err.println("Could not parse ball info: " + fullstateMsg);
        }

        // Get time step.
        pattern = Pattern.compile(TIME_STEP_PATTERN);
        matcher = pattern.matcher(fullstateMsg);
        if (matcher.find())
        {
            timeStep = Integer.valueOf(matcher.group(1));            
        }
        else
        {
//            System.err.println("Could not parse time step: " + fullstateMsg);
        }

        // Gather players information.
        pattern = Pattern.compile(PLAYER_PATTERN);
        matcher = pattern.matcher(fullstateMsg);
        Player[] team; // Team of the player currently being parsed.
        int playerNumber; // Number of the player currently being parsed.
        while (matcher.find())
        {
            if (matcher.group(1).compareToIgnoreCase("l") == 0)
            {
                team = this.leftTeam;
            }
            else
            {
                team = this.rightTeam;
            }

            playerNumber = Integer.valueOf(matcher.group(2));
//            System.out.println("Player Number: " + playerNumber);

            if (matcher.group(3).compareToIgnoreCase("g") == 0)
            {
                team[playerNumber - 1].setPlayerType(-1);
            }
            else
            {
                team[playerNumber - 1].setPlayerType(Integer.valueOf(matcher
                        .group(3)));
            }

            team[playerNumber - 1].setUniformNumber(playerNumber);
            team[playerNumber - 1].getPosition().setX(Double.valueOf(matcher.group(4)));
            team[playerNumber - 1].getPosition().setY(Double.valueOf(matcher.group(5)));
            team[playerNumber - 1].getVelocity().setX(Double.valueOf(matcher.group(6)));
            team[playerNumber - 1].getVelocity().setY(Double.valueOf(matcher.group(7)));
            team[playerNumber - 1].setBodyDirection(Double.valueOf(matcher
                    .group(8)));
        }
    }

    public void calculatePosition(String position, double distance, double degrees)
    {
        String[] positions = position.split(" ");
        System.out.println("Flag: " + position + " Distance: " + distance + " Degrees: " + degrees);
        if (positions.length < 4 || (distance == 0 && degrees == 0)) {
            return;
        }
        int positiveX = 1;
        int positiveY = 1;
        boolean relatesToX = false;
        double x = 0;
        double y = 0;

        // First position will handle the constant negative value
        switch (positions[1])
        {
            case "t":
                // Negative y value of 34
                y = -34;
                relatesToX = true;
                break;
            case "b":
                // Positive y value 34
                y = 34;
                relatesToX = true;
                break;
            case "l":
                x = -52.5;
                // Negative x value -52.5
                break;
            case "r":
                x = 52.5;
                // Positive x value 52.5
                break;
        }

        // Second position figures out if the value is positive or negative
        switch (positions[2])
        {
            case "l":
                positiveX = -1;
                break;
            case "t":
                positiveY = -1;
                break;
        }

        // So finally the third position relates to the location of the tag relative to the first position
        if (relatesToX)
            x = Double.parseDouble(positions[3]);
        else
            y = Double.parseDouble(positions[3]);

        // At this point we have both the x and y. Now we take the position relative to it and put that in too.
        Vector2D flagVector = new Vector2D(x*positiveX, y*positiveY);
        // We don't know the position of the player but we know the distance from the x and y
        Vector2D playerVector = new Vector2D(distance, degrees+180, true);

        Vector2D vector = flagVector.add(playerVector);
        System.out.println("The player position x is " + vector.getX() + " and y is " + vector.getY());
        player.setPosition(flagVector.add(playerVector));

    }

    public void printErrorMessage(String mode)
    {
        /*
        if (!printedError) {
            System.err.println("Could not parse " + mode + " info: \n" + fullstateMsg);
            printedError = true;
        }
        */
        if (printCounter < 2) {
            System.err.println("Could not parse " + mode + " info: \n" + fullstateMsg);
            printCounter++;
        }
    }
    
    public String toString()
    {
        String fs = "";
        fs += "------ " + System.currentTimeMillis() + " ---------\n";
        fs += "ball: " + ball + "\n";
        Player pi;
        for (int i = 0; i < MAX_PLAYERS; i++)
        {
            pi = leftTeam[i];
            fs += "Player " + i + " " + pi + "\n";
        }
        for (int i = 0; i < MAX_PLAYERS; i++)
        {
            pi = rightTeam[i];
            fs += "Player " + i + " " + pi + "\n";
        }
        
        return fs;
    }

}
