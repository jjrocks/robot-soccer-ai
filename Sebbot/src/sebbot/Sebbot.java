/**
 * 
 */
package sebbot;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

import sebbot.ballcapture.DirectPolicySearch;
import sebbot.ballcapture.PolicyPerformance;
import sebbot.ballcapture.Qiteration;
import sebbot.strategy.GoToBallAndShoot;
import sebbot.strategy.GoalieStrategy;
import sebbot.strategy.Strategy;
import sebbot.strategy.UniformCover;

/**
 * @author Sebastien Lentz
 *
 */
public class Sebbot
{

    /**
     * This is the entry point of the application.
     * Launch the soccer client using command line:
     * 
     * Sebbot [-parameter value]
     * 
     * Parameters:
     * 
     * host (default "localhost"):
     * The host name can either be a machine name, such as "java.sun.com"
     * or a string representing its IP address, such as "206.26.48.100."
     *
     * port (default 6000):
     * Port number for the communication with the server
     *
     * team (default Team1):
     * Team name. This name can not contain spaces.
     *
     * 
     * @param args
     * @throws SocketException
     * @throws IOException
     */
    public static void main(String args[]) throws SocketException, IOException
    {
        startAgents(args);
//        dpsComputation();
//        qitComputation();
        //performanceTest();

//                DirectPolicySearch dps = DirectPolicySearch.load("DPS_18_1152_100_50.zip");
//                RadialGaussian[] rgs = dps.getBasicFunctions();
//                
//                for (int i =0; i< rgs.length; i++)
//                {
//                    System.out.println(rgs[i].getDiscreteActionNb());
//                }

    }

    public static void startAgents(String args[]) throws IOException
    {
        String hostname = "localhost";
        int port = 6000;
        String team = "team1";
        String strategy = "Default";

        try
        {
            // First look for parameters
            for (int i = 0; i < args.length; i += 2)
            {
                if (args[i].compareTo("-host") == 0)
                {
                    hostname = args[i + 1];
                }
                else if (args[i].compareTo("-port") == 0)
                {
                    port = Integer.parseInt(args[i + 1]);
                }
                else if (args[i].compareTo("-team") == 0)
                {
                    team = args[i + 1];
                }
                else if (args[i].compareTo("-strategy") == 0)
                {
                    strategy = args[i + 1];
                }
                else
                {
                    throw new InvalidArgumentException(args[i]);
                }
            }
        }
        catch (InvalidArgumentException e)
        {
            System.err.println("");
            System.err.println("USAGE: Sebbot [-parameter value]");
            System.err.println("");
            System.err.println("    Parameters  value          default");
            System.err.println("   ------------------------------------");
            System.err.println("    host        host name      localhost");
            System.err.println("    port        port number    6000");
            System.err.println("    team        team name      team1");
            System.err.println("    strategy    strategy name  Default");
            System.err.println("");
            return;
        }

        RobocupClient client;
        Brain brain;
        int numOfPlayers = 9;

        String curDir = System.getProperty("user.dir");
        System.out.println("Current sys dir: " + curDir);
        /*
        DirectPolicySearch dps = DirectPolicySearch.load("savedDPS2.zip");
        Strategy dpsGoToBall = new GoToBallAndShoot(dps);
        for (int i = 0; i < nbOfPlayers; i++)
        {
            client = new RobocupClient(InetAddress.getByName(hostname), port,
                team);
            client.init("");

            brain = client.getBrain();
            brain.setStrategy(dpsGoToBall);

            new Thread(client).start();
            new Thread(brain).start();
        }
*/
//                dps = DirectPolicySearch.load("30_1920_30.zip");
//                dpsGoto = new DPSGoTo(dps);
//        Qiteration qit = Qiteration.loadQl("Qit_1_1_1_1_50_178_50_0-9_183.zip");
        GoToBallAndShoot qitGotoBall = new GoToBallAndShoot();
        
//        UniformCover.setGoToBallStrategy(qitGotoBall);
//        Strategy uniformCover = new UniformCover(5);

        for (int i = 0; i < numOfPlayers; i++)
        {
            client = new RobocupClient(InetAddress.getByName(hostname), port,
                "team2");
            client.init(qitGotoBall);

            brain = client.getBrain();
            brain.setStrategy(qitGotoBall);

            new Thread(client).start();
            new Thread(brain).start();
        }

        client = new RobocupClient(InetAddress.getByName(hostname), port,
                "team2");
        client.init(new GoalieStrategy());

        brain = client.getBrain();
        brain.setStrategy(new GoalieStrategy());

        new Thread(client).start();
        new Thread(brain).start();



    }

    public static void dpsComputation()
    {
        DirectPolicySearch dps;
        int nbOfBFs = 12;
        for (int i = 0; i < 5; i++)
        {
            dps = new DirectPolicySearch(nbOfBFs, 1, 100);
            dps.run();
            dps = new DirectPolicySearch(nbOfBFs, 2, 100);
            dps.run();
            dps = new DirectPolicySearch(nbOfBFs, 3, 100);
            dps.run();
            nbOfBFs += 4;
        }
    }
    
    private static void qitComputation()
    {
        Qiteration qit;
        
        for (float g = 0.5f; g < 0.9f; g += 0.05f)
        {
            qit = new Qiteration(1, 1, 1, 1, 50, 400, 50, 10, 2, g);
            qit.run();
            qit = null;
        }
        
        for (float g = 0.9f; g < 1f; g += 0.02f)
        {
            qit = new Qiteration(1, 1, 1, 1, 50, 400, 50, 10, 2, g);
            qit.run();
            qit = null;
        }
        
        for (float g = 0.5f; g < 0.9f; g += 0.05f)
        {
            qit = new Qiteration(4, 8, 1, 1, 20, 200, 20, 10, 2, g);
            qit.run();
            qit = null;
        }
        
        for (float g = 0.9f; g < 1f; g += 0.02f)
        {
            qit = new Qiteration(4, 8, 1, 1, 20, 200, 20, 10, 2, g);
            qit.run();
            qit = null;
        }
        
    }

    public static void performanceTest()
    {
        //DirectPolicySearch dps = DirectPolicySearch.load("DPS_30_2880_100_50.zip");        
        Qiteration qit = Qiteration.loadQl("Qit_4_8_1_1_20_200_20_0-9_197.zip");
        //PolicyPerformance.testAllDps();
        PolicyPerformance.logPerformances(qit, false);
    }
}
