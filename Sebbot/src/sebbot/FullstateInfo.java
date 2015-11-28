package sebbot;

import java.util.HashMap;
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
    final static String SET_FLAG_PATTERN = 	"\\(\\(((?:f|g)(?: \\w)+(?: \\d{1,2})*)\\) (\\-?\\d{1,3}(?:\\.\\d)?) (\\-?\\d{1,3}(?:\\.\\d)?)(?: \\-?\\d{1,3}(\\.\\d)?)*\\)";
    final static String PLAYMODE_PATTERN  = "\\(pmode ([a-zA-Z_]*)\\)";
    final static String REAL_NB_PATTERN   = "((?:\\-)?[0-9]+(?:\\.[0-9]+(?:e(?:\\-)?[0-9]+)?)?)";
    final static String BALL_PATTERN      = "\\(\\(b\\) (\\-?\\d{1,3}(?:\\.\\d)?) (\\-?\\d{1,3}(\\.\\d)?)" +
            "(?: \\-?\\d{1,3}(?:\\.\\d)?)*\\)";
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
    public boolean noFlags = true;
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
    private HashMap<String, Vector2D> flagPositions = new HashMap<String, Vector2D>();
    private RobocupClient robocupClient;

    /**
     * Constructor.
     * 
     * @param fullstateMsg
     */
    public FullstateInfo(String fullstateMsg, Player player, RobocupClient robocupClient)
    {
        this.robocupClient = robocupClient;
        this.player = player;
        this.fullstateMsg = fullstateMsg;
        this.ball = new Ball(0, 0, 0, 0);
        leftTeam = new Player[MAX_PLAYERS];
        rightTeam = new Player[MAX_PLAYERS];
        for (int i = 0; i < MAX_PLAYERS; i++)
        {
            leftTeam[i] = new Player(0, 0, 0, 0, true, '0', 0, i);
            rightTeam[i] = new Player(0, 0, 0, 0, false, '0', 0, i);
        }

        flagPositions.put("f t 0", new Vector2D(0, -39));
        flagPositions.put("f b 0", new Vector2D(0, 39));
        flagPositions.put("f l 0", new Vector2D(-57.5, 0));
        flagPositions.put("f r 0", new Vector2D(57.5, 0));
        flagPositions.put("f l t", new Vector2D(-52.5, -34));
        flagPositions.put("f r t", new Vector2D(52.5, -34));
        flagPositions.put("f l b", new Vector2D(-52.5, 34));
        flagPositions.put("f r b", new Vector2D(52.5, 34));
        flagPositions.put("f c", new Vector2D(0, 0));
        flagPositions.put("f c b", new Vector2D(0, 34));
        flagPositions.put("f c t", new Vector2D(0, -34));
        flagPositions.put("g l", new Vector2D(-52.5, 0));
        flagPositions.put("g r", new Vector2D(52.5, 0));
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

    public Player getPlayer() {
        return player;
    }

    /**
     * This method parses the fullstateMsg string and updates the variables
     * consequently.
     */
    public void parse()
    {
//        System.out.println("Parsing data: " + fullstateMsg);
        // Gather playMode information.
        boolean skipStuff = false;
        noFlags = true;
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
            pattern = Pattern.compile("\\d+");
            matcher = pattern.matcher(fullstateMsg);
            if (matcher.find()) {
//                System.out.println(fullstateMsg);
                timeStep = Integer.parseInt(matcher.group(0));
                System.out.println("Current timestep: " + timeStep);
            }
            if (true /*player.getPosition().getX() == 0*/) {
                pattern = Pattern.compile(SET_FLAG_PATTERN);
                matcher = pattern.matcher(fullstateMsg);
                double averageX = 0.0;
                double averageY = 0.0;
                int groupCount = 0;
                // We go through all the flags to calculate the average position
                while(matcher.find()) {
                    if (matcher.find() && matcher.groupCount() == 4) {
                        System.out.println("Matcher says this: " + matcher.group(0));
                        // We grab the flags data. group 0 is the entire flag data ((f t l 10) 10 32) and group 1
                        // is the flag id "f t l 10" group 2 is the distance 10 group 3 is the degrees
                        Vector2D currVector = calculatePosition(matcher.group(1), Double.parseDouble(matcher.group(2)),
                                Double.parseDouble(matcher.group(3)));
                        // If the vector has a flag we can parse, we'll use that vector to average the data
                        if (currVector != null) {
                            System.out.println("Current Vector: " + currVector);
                            averageX += currVector.getX();
                            averageY += currVector.getY();
                            groupCount += 1;
                        }
                    }
                }
                // If there are any flags in the area we'll go ahead an create the average x and y
                if (groupCount != 0) {
                    Vector2D playerVector = new Vector2D(averageX/groupCount, averageY/groupCount);
                    System.out.println("Player Vector: " + playerVector.toString());
                    System.out.println("Player Body direction: " + player.getBodyDirection());
                    player.setPosition(playerVector);
                    noFlags = false;
                }
                // Gather ball information.
                pattern = Pattern.compile(BALL_PATTERN);
                matcher = pattern.matcher(fullstateMsg);
                if (matcher.find())
                {
                    double ballDistance = Double.parseDouble(matcher.group(1));
                    double ballDegrees = Double.parseDouble(matcher.group(2));
                    Vector2D ballVector = new Vector2D(ballDistance, ballDegrees, true).add(player.getPosition());
                    ball.setPosition(ballVector);
                    System.out.println("Found the ball! " + ballVector.toString());
                }
                else
                {
//            System.err.println("Could not parse ball info: " + fullstateMsg);
                }
                new PlayerAction(PlayerActionType.TURN, 0.0d, 5, robocupClient).execute();
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

    public Vector2D calculateFlag(String position, double distance, double degrees) {
        System.out.println("Flag: " + position + " Distance: " + distance + " Degrees: " + degrees);
        // THe data gets massivly unreliable the further they are away and the more to the perephrial they are.
        if ((distance == 0 && degrees == 0) || distance > 70 || degrees > 35 || degrees < -35) {
            return null;
        }
        // There are about 30 flags that can be calculated for everything else we have a dictionary we initialize at
        // the begining.
        if (flagPositions.containsKey(position)) {
            return flagPositions.get(position);
        }

        String[] positions = position.split(" ");
        // At this point we are parsing for the edge flags so all others can return null
        if (positions.length < 4 || !positions[3].matches("\\d+"))
            return null;

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
                y = -39;
                relatesToX = true;
                break;
            case "b":
                // Positive y value 34
                y = 39;
                relatesToX = true;
                break;
            case "l":
                x = -57.5;
                // Negative x value -57.5
                break;
            case "r":
                x = 57.5;
                // Positive x value 57.5
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

        Vector2D flagVector = new Vector2D(x*positiveX, y*positiveY);
        return flagVector;
    }

    /**
     * Calculates a weird but properly functioning postion that is accurate up to about .01
     * @param position
     * @param distance
     * @param degrees
     * @return
     */
    public Vector2D calculatePosition(String position, double distance, double degrees)
    {

        // At this point we have both the x and y. Now we take the position relative to it and put that in too.
        Vector2D flagVector = calculateFlag(position, distance, degrees);
        if (flagVector == null) {
            return null;
        }
        // We don't know the position of the player but we know the distance from the x and y and the relative degrees
        // The equation in 4.3.2 (equation 4.6) shows what it will take to calculate it. The body direction starts at 0
        // where 0 is as if you were on the center of the field (or anywhere in the x axis) pointing to the goal on
        // the right. So we take that, then turn it around (which is done by either adding or subtracting 180 degrees
        // then we add the players direction to shift it to the absolute angle.
        Vector2D playerVector = new Vector2D(distance, degrees+180+player.getBodyDirection(), true);

        return flagVector.add(playerVector);
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

    public void setPlayer(Player player) {
        this.player = player;
    }
}
