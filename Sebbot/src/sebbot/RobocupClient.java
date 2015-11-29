package sebbot;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.charset.Charset;

import sebbot.ballcapture.Qiteration;
import sebbot.strategy.GoToBallAndShoot;
import sebbot.strategy.Strategy;
import sebbot.strategy.UniformCover;

/**
 * This class implements the commands of the Robocup Soccer Simulation 2D
 * interface. It contains the client-server communication functions.
 * 
 * @author Sebastien Lentz
 * 
 */
public class RobocupClient implements Runnable
{

    private final int      MSG_SIZE = 4096; // Size of the socket buffer

    private DatagramSocket socket;         // Socket to communicate with the server
    private InetAddress    host;           // Server address
    private int            port;           // Server port
    private String         teamName;       // Team name
    private Brain          brain;          // Actions deciding module
    private double startX,startY; //starting coordinates

    /*
     * =========================================================================
     * 
     *                     Constructors and destructors
     * 
     * =========================================================================
     */
    /**
     * @param host
     * @param port
     * @param teamName
     * @throws SocketException
     */
    public RobocupClient(InetAddress host, int port, String teamName)
                                                                     throws SocketException
    {
        this.socket = new DatagramSocket();
        this.host = host;
        this.port = port;
        this.teamName = teamName;
        this.startX = -1;
        this.startY = -1;
    }
    
    /**
     * @param host
     * @param port
     * @param teamName
     * @throws SocketException
     */
    public RobocupClient(InetAddress host, int port, String teamName, double x, double y)
            throws SocketException
 {
		this.socket = new DatagramSocket();
		this.host = host;
		this.port = port;
		this.teamName = teamName;
		this.startX = x;
		this.startY = y;
	}

    /**
     * This destructor closes the communication socket.
     */
    public void finalize()
    {
        send("(bye)");
        socket.close();
    }

    /*
     * =========================================================================
     * 
     *                      Getters and Setters
     * 
     * =========================================================================
     */
    /**
     * @return the brain
     */
    public Brain getBrain()
    {
        return brain;
    }

    /**
     * @param brain the brain to set
     */
    public void setBrain(Brain brain)
    {
        this.brain = brain;
    }

    /*
     * =========================================================================
     * 
     *                          Socket communication
     * 
     * =========================================================================
     */
    /**
     * Send a message to the server.
     * 
     * @param message
     */
    private void send(String message)
    {
        System.out.println("Sending: " + message);

        byte[] buffer = message.getBytes(Charset.defaultCharset());

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, host,
            port);

        try
        {
            socket.send(packet);
        }
        catch (IOException e)
        {
            System.err.println("socket sending error " + e);
        }
    }

    /**
     * Wait for a new message from the server.
     */
    private String receive()
    {
        byte[] buffer = new byte[MSG_SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, MSG_SIZE);
        try
        {
            socket.receive(packet);
        }
        catch (IOException e)
        {
            System.err.println("socket receiving error " + e);
        }
        return new String(buffer, Charset.defaultCharset());
    }

    /*
     * =========================================================================
     * 
     *                  Implementation of client commands
     * 
     * =========================================================================
     */
    /**
     * @param x
     * @param y
     */
    public void move(double x, double y)
    {
        brain.getFullstateInfo().getPlayer().setPosition(new Vector2D(x, y));
        send("(move " + Double.toString(x) + " " + Double.toString(y) + ")");
    }

    /**
     * @param moment
     */
    public void turn(double moment)
    {
        Player player = brain.getFullstateInfo().getPlayer();
        double newAngle = ((player.getBodyDirection() + moment) % 360);
        player.setBodyDirection(newAngle);
        brain.getFullstateInfo().setPlayer(player);
        send("(turn " + Double.toString(moment) + ")");
    }

    /**
     * @param power
     */
    public void dash(double power)
    {
        Player player = brain.getFullstateInfo().getPlayer();
        player.setPosition(player.nextPosition(power));
        brain.getFullstateInfo().setPlayer(player);
        send("(dash " + Double.toString(power) + ")");
    }

    /**
     * @param power
     * @param direction
     */
    public void kick(double power, double direction)
    {
        send("(kick " + Double.toString(power) + " "
                + Double.toString(direction) + ")");
    }

    /*
     * =========================================================================
     * 
     *                      Message parsing methods
     * 
     * =========================================================================
     */
    /**
     * @param message
     */
    private void parseServerMsg(String message)
    {
        // Check the kind of information first
        brain.getFullstateInfo().setFullstateMsg(message);
        brain.getFullstateInfo().parse();
    }

    /**
     * This function sends the init message to the server and parse its answer.
     * Once the response of the server has been parsed, the brain is initialized
     * with the given strategy.
     * 
     * (init Side Unum PlayMode)
     * 
     * Side ::= l | r
     * Unum ::= 1 ~ 11
     * PlayMode ::= one of play modes
     * 
     * @param strategy the strategy the brain will use.
     * 
     * @throws IOException if the init message could not be parsed.
     */
    protected void init(Strategy strategy) throws IOException
    {
        byte[] buffer = new byte[MSG_SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, MSG_SIZE);

        // First we need to initialize the connection to the server
        send("(init " + teamName + " (version 14))");
        socket.receive(packet);
        port = packet.getPort();

        String initMsg = new String(buffer, Charset.defaultCharset());
        final String initPattern = "\\(init ([lr]) ([0-9]{1,2}) ([a-zA-Z_]+)\\)";

        Pattern pattern = Pattern.compile(initPattern);
        Matcher matcher = pattern.matcher(initMsg);
        if (matcher.find())
        {
            boolean leftTeam = matcher.group(1).charAt(0) == 'l';
            int playerNumber = Integer.valueOf(matcher.group(2));

            brain = new Brain(this, leftTeam, playerNumber,
                strategy,startX,startY);
            brain.getFullstateInfo().setPlayMode(matcher.group(3));
        }
        else
        {
            throw new IOException(initMsg);
        }

    }

    /** 
     * This methods will just keep waiting for server messages
     * to arrive on the socket then parse them.
     */
    public void run()
    {
        while (true)
        {
            parseServerMsg(receive());
        }
    }

    /**
     * Creates a strategy object based on its name.
     *
     * @param s the name of the strategy.
     * @return the created strategy.
     */
    protected Strategy stringToStrategy(String s)
    {
        Strategy s1;

        if (s.equalsIgnoreCase("UniformCover"))
        {
            s1 = new UniformCover(5);
        }
        else if (s.equalsIgnoreCase("GoToBallAndShoot"))
        {
            s1 = new GoToBallAndShoot();
        }
        else if (s.equalsIgnoreCase("QiterationGoTo"))
        {
            s1 = new GoToBallAndShoot(Qiteration.loadQl("backupQl.zip"));
        }
        else if (s.equalsIgnoreCase("DPSGoto"))
        {
            s1 = new GoToBallAndShoot(Qiteration.loadQl("savedBFs.zip"));
        }
        else
        {
            s1 = new GoToBallAndShoot();
        }

        return s1;
    }
}
