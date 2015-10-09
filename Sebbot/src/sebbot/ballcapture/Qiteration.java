package sebbot.ballcapture;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.Format;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import sebbot.MathTools;
import sebbot.SoccerParams;

/**
 * @author Sebastien Lentz
 *
 */
public class Qiteration implements Policy, Serializable, Runnable
{
    private static final long     serialVersionUID = -1245314286263462019L;

    int                           totalNbOfIterations;
    long                          totalComputationTime;

    private float[][][][][][][][] qTable;
    private float[][][][][][][][] oldQtable;

    private int                   ballVelocityNormSteps;
    private int                   ballVelocityDirectionSteps;
    private int                   playerVelocityNormSteps;
    private int                   playerVelocityDirectionSteps;
    private int                   playerBodyDirectionSteps;
    private int                   relativeDistanceSteps;
    private int                   relativeDirectionSteps;
    private int                   dashSteps;
    private int                   turnSteps;

    float                         discountFactor;

    /*
     * =========================================================================
     * 
     *                     Constructors and destructors
     * 
     * =========================================================================
     */
    /**
     * @param ballVelocityNormSteps
     * @param ballVelocityDirectionSteps
     * @param playerVelocityNormSteps
     * @param playerVelocityDirectionSteps
     * @param playerBodyDirectionSteps
     * @param relativeDistanceSteps
     * @param relativeDirectionSteps
     * @param turnSteps
     * @param dashSteps
     * @param discountFactor
     */
    public Qiteration(int ballVelocityNormSteps,
                      int ballVelocityDirectionSteps,
                      int playerVelocityNormSteps,
                      int playerVelocityDirectionSteps,
                      int playerBodyDirectionSteps, int relativeDistanceSteps,
                      int relativeDirectionSteps, int turnSteps, int dashSteps,
                      float discountFactor)
    {
        this.totalNbOfIterations = 0;
        this.totalComputationTime = 0;
        this.ballVelocityNormSteps = ballVelocityNormSteps;
        this.ballVelocityDirectionSteps = ballVelocityDirectionSteps;
        this.playerVelocityNormSteps = playerVelocityNormSteps;
        this.playerVelocityDirectionSteps = playerVelocityDirectionSteps;
        this.playerBodyDirectionSteps = playerBodyDirectionSteps;
        this.relativeDistanceSteps = relativeDistanceSteps;
        this.relativeDirectionSteps = relativeDirectionSteps;
        this.turnSteps = turnSteps;
        this.dashSteps = dashSteps;
        this.discountFactor = discountFactor;

        State.setBallVelocityNormSteps(ballVelocityNormSteps);
        State.setBallVelocityDirectionSteps(ballVelocityDirectionSteps);
        State.setPlayerVelocityNormSteps(playerVelocityNormSteps);
        State.setPlayerVelocityDirectionSteps(playerVelocityDirectionSteps);
        State.setPlayerBodyDirectionSteps(playerBodyDirectionSteps);
        State.setRelativeDistanceSteps(relativeDistanceSteps);
        State.setRelativeDirectionSteps(relativeDirectionSteps);

        Action.setTurnSteps(turnSteps);
        Action.setDashSteps(dashSteps);

        this.oldQtable = new float[ballVelocityNormSteps][ballVelocityDirectionSteps][playerVelocityNormSteps][playerVelocityDirectionSteps][playerBodyDirectionSteps][relativeDistanceSteps][relativeDirectionSteps][dashSteps
                + turnSteps];

        this.qTable = new float[ballVelocityNormSteps][ballVelocityDirectionSteps][playerVelocityNormSteps][playerVelocityDirectionSteps][playerBodyDirectionSteps][relativeDistanceSteps][relativeDirectionSteps][dashSteps
                + turnSteps];

        initQtables();

        //        State s;
        //        float reward;
        //        for (int i = 0; i < 10; i++)
        //        {
        //            s = new State((float) (Math.random() * 3.0D),
        //                (float) (Math.random() * 360D - 180D),
        //                (float) (Math.random() * 1.05D),
        //                (float) (Math.random() * 360D - 180D),
        //                (float) (Math.random() * 360D - 180D),
        //                (float) (Math.random() * 125D),
        //                (float) (Math.random() * 360D - 180D));
        //            
        //            reward = MarkovDecisionProcess.trajectoryReward(s, this, 200);
        //            
        //            System.out.println("init state: " + s);
        //            System.out.println("infinite reward: " + reward);
        //            
        //        }
        //        
        //        printQl();
        //        printQl();
        //        printQl();
        //        printQl();
        //        printQl();
        //        
        //        testQ();
    }

    /*
     * =========================================================================
     * 
     *                      Getters and Setters
     * 
     * =========================================================================
     */
    /**
     * @return the totalNbOfIterations
     */
    public int getTotalNbOfIterations()
    {
        return totalNbOfIterations;
    }

    /**
     * @return the totalComputationTime
     */
    public long getTotalComputationTime()
    {
        return totalComputationTime;
    }

    /**
     * @return the discountFactor
     */
    public float getDiscountFactor()
    {
        return discountFactor;
    }

    /**
     * @return the qTable
     */
    public float[][][][][][][][] getqTable()
    {
        return qTable;
    }

    /**
     * @return the oldQtable
     */
    public float[][][][][][][][] getOldQtable()
    {
        return oldQtable;
    }

    /**
     * @return the ballVelocityNormSteps
     */
    public int getBallVelocityNormSteps()
    {
        return ballVelocityNormSteps;
    }

    /**
     * @return the ballVelocityDirectionSteps
     */
    public int getBallVelocityDirectionSteps()
    {
        return ballVelocityDirectionSteps;
    }

    /**
     * @return the playerVelocityNormSteps
     */
    public int getPlayerVelocityNormSteps()
    {
        return playerVelocityNormSteps;
    }

    /**
     * @return the playerVelocityDirectionSteps
     */
    public int getPlayerVelocityDirectionSteps()
    {
        return playerVelocityDirectionSteps;
    }

    /**
     * @return the playerBodyDirectionSteps
     */
    public int getPlayerBodyDirectionSteps()
    {
        return playerBodyDirectionSteps;
    }

    /**
     * @return the relativeDistanceSteps
     */
    public int getRelativeDistanceSteps()
    {
        return relativeDistanceSteps;
    }

    /**
     * @return the relativeDirectionSteps
     */
    public int getRelativeDirectionSteps()
    {
        return relativeDirectionSteps;
    }

    /**
     * @return the dashSteps
     */
    public int getDashSteps()
    {
        return dashSteps;
    }

    /**
     * @return the turnSteps
     */
    public int getTurnSteps()
    {
        return turnSteps;
    }

    /*
     * =========================================================================
     * 
     *                            Main methods
     * 
     * =========================================================================
     */
    /**
     * 
     */
    public void computeQl()
    {
        long startTime;
        long finishTime;

        System.out.println("q iteration starting...");
        System.out.println(this);

        float tmp;

        int nbOfIterations = 0;
        State s = new State();
        Action a = new Action(0, false);

        do
        {
            startTime = new Date().getTime();
            nbOfIterations++;
            for (int bvn = 0; bvn < oldQtable.length; bvn++)
            {
                for (int bvd = 0; bvd < oldQtable[bvn].length; bvd++)
                {
                    for (int pvn = 0; pvn < oldQtable[bvn][bvd].length; pvn++)
                    {
                        for (int pvd = 0; pvd < oldQtable[bvn][bvd][pvn].length; pvd++)
                        {
                            for (int pbd = 0; pbd < oldQtable[bvn][bvd][pvn][pvd].length; pbd++)
                            {
                                for (int rdist = 0; rdist < oldQtable[bvn][bvd][pvn][pvd][pbd].length; rdist++)
                                {
                                    for (int rdir = 0; rdir < oldQtable[bvn][bvd][pvn][pvd][pbd][rdist].length; rdir++)
                                    {
                                        for (int act = 0; act < oldQtable[bvn][bvd][pvn][pvd][pbd][rdist][rdir].length; act++)
                                        {
                                            s
                                                .setBallVelocityNorm((float) MathTools
                                                    .indexToValue(
                                                        bvn,
                                                        0.0f,
                                                        SoccerParams.BALL_SPEED_MAX,
                                                        ballVelocityNormSteps,
                                                        0.0f));
                                            s
                                                .setBallVelocityDirection((float) MathTools
                                                    .indexToValue(
                                                        bvd,
                                                        -180.0f,
                                                        180.0f,
                                                        ballVelocityDirectionSteps,
                                                        0.0f));
                                            s
                                                .setPlayerVelocityNorm((float) MathTools
                                                    .indexToValue(
                                                        pvn,
                                                        0,
                                                        SoccerParams.PLAYER_SPEED_MAX,
                                                        playerVelocityNormSteps,
                                                        0.0f));
                                            s
                                                .setPlayerVelocityDirection((float) MathTools
                                                    .indexToValue(
                                                        pvd,
                                                        -180.0f,
                                                        180.0f,
                                                        playerVelocityDirectionSteps,
                                                        0.0f));
                                            s
                                                .setPlayerBodyDirection((float) MathTools
                                                    .indexToValue(
                                                        pbd,
                                                        -180.0f,
                                                        180.0f,
                                                        playerBodyDirectionSteps,
                                                        0.0f));
                                            s
                                                .setRelativeDistance((float) MathTools
                                                    .indexToValue(rdist, 0.0f,
                                                        125.0f,
                                                        relativeDistanceSteps,
                                                        0.0f));
                                            s
                                                .setRelativeDirection((float) MathTools
                                                    .indexToValue(rdir,
                                                        -180.0f, 180.0f,
                                                        relativeDirectionSteps,
                                                        0.0f));

                                            if (act >= turnSteps)
                                            {
                                                a.setTurn(false);
                                                a
                                                    .setValue((float) MathTools
                                                        .indexToValue(act
                                                                - turnSteps,
                                                            0.0f, 100.0f,
                                                            dashSteps, 1.0f));
                                            }
                                            else
                                            {
                                                a.setTurn(true);
                                                a
                                                    .setValue((float) MathTools
                                                        .indexToValue(act,
                                                            -180.0f, 180.0f,
                                                            turnSteps, 0.5f));
                                            }

                                            tmp = qTable[bvn][bvd][pvn][pvd][pbd][rdist][rdir][act];

                                            qTable[bvn][bvd][pvn][pvd][pbd][rdist][rdir][act] = MarkovDecisionProcess
                                                .reward(s, a, true)
                                                    + discountFactor
                                                    * maxUq(
                                                        MarkovDecisionProcess
                                                            .nextState(s, a,
                                                                true),
                                                        oldQtable);

                                            if (qTable[bvn][bvd][pvn][pvd][pbd][rdist][rdir][act] > 2f * 1000000f)
                                            {
                                                System.out
                                                    .println("prob: "
                                                            + qTable[bvn][bvd][pvn][pvd][pbd][rdist][rdir][act]
                                                            + " " + s + " " + a);
                                            }

                                            oldQtable[bvn][bvd][pvn][pvd][pbd][rdist][rdir][act] = tmp;

                                        }

                                    }

                                }

                            }

                        }

                    }

                }

            }

            //printQl();
            totalNbOfIterations++;
            finishTime = new Date().getTime();
            totalComputationTime += (finishTime - startTime);

            PolicyPerformance.logPerformances(this, false);

            if (nbOfIterations % 100 == 0)
            {
                save();
            }
        }
        while (!q0EqualsQ1(oldQtable, qTable) && nbOfIterations < 1000);

        System.out.println("nb of iterations: " + nbOfIterations);
        System.out.println("q iteration table computed.");

        save();
    }

    /**
     * @param s
     * @param a
     * @return
     */
    public float qFunction(State s, Action a)
    {
        int s0, s1, s2, s3, s4, s5, s6, s7;
        s0 = MathTools.valueToIndex(s.getBallVelocityNorm(), 0.0f,
            SoccerParams.BALL_SPEED_MAX, ballVelocityNormSteps);
        s1 = MathTools.valueToIndex(s.getBallVelocityDirection(), -180.0f,
            180.0f, ballVelocityDirectionSteps);
        s2 = MathTools.valueToIndex(s.getPlayerVelocityNorm(), 0.0f,
            SoccerParams.PLAYER_SPEED_MAX, playerVelocityNormSteps);
        s3 = MathTools.valueToIndex(s.getPlayerVelocityDirection(), -180.0f,
            180.0f, playerVelocityDirectionSteps);
        s4 = MathTools.valueToIndex(s.getPlayerBodyDirection(), -180.0f,
            180.0f, playerBodyDirectionSteps);
        s5 = MathTools.valueToIndex(s.getRelativeDistance(), 0.0f, 125.0f,
            relativeDistanceSteps);
        s6 = MathTools.valueToIndex(s.getRelativeDirection(), -180.0f, 180.0f,
            relativeDirectionSteps);

        if (a.isTurn())
        {
            s7 = MathTools.valueToIndex(a.getValue(), -180.0f, 180.0f,
                turnSteps);
        }
        else
        {
            s7 = turnSteps
                    + MathTools.valueToIndex(a.getValue(), 0.0f, 100.0f,
                        dashSteps);
        }

        return qTable[s0][s1][s2][s3][s4][s5][s6][s7];
    }

    /**
     * @param s
     * @param q0
     * @return
     */
    private float maxUq(State s, float[][][][][][][][] q0)
    {
        int s0, s1, s2, s3, s4, s5, s6;
        s0 = MathTools.valueToIndex(s.getBallVelocityNorm(), 0.0f,
            SoccerParams.BALL_SPEED_MAX, ballVelocityNormSteps);
        s1 = MathTools.valueToIndex(s.getBallVelocityDirection(), -180.0f,
            180.0f, ballVelocityDirectionSteps);
        s2 = MathTools.valueToIndex(s.getPlayerVelocityNorm(), 0.0f,
            SoccerParams.PLAYER_SPEED_MAX, playerVelocityNormSteps);
        s3 = MathTools.valueToIndex(s.getPlayerVelocityDirection(), -180.0f,
            180.0f, playerVelocityDirectionSteps);
        s4 = MathTools.valueToIndex(s.getPlayerBodyDirection(), -180.0f,
            180.0f, playerBodyDirectionSteps);
        s5 = MathTools.valueToIndex(s.getRelativeDistance(), 0.0f, 125.0f,
            relativeDistanceSteps);
        s6 = MathTools.valueToIndex(s.getRelativeDirection(), -180.0f, 180.0f,
            relativeDirectionSteps);

        float max = -1000000.0f;
        for (int i = 0; i < q0[s0][s1][s2][s3][s4][s5][s6].length; i++)
        {
            if (q0[s0][s1][s2][s3][s4][s5][s6][i] > max)
            {
                max = q0[s0][s1][s2][s3][s4][s5][s6][i];
            }

        }

        return max;
    }

    /* (non-Javadoc)
     * @see sebbot.learning.Policy#chooseAction(sebbot.learning.State)
     */
    public Action chooseAction(State s)
    {
        int s0, s1, s2, s3, s4, s5, s6;
        s0 = MathTools.valueToIndex(s.getBallVelocityNorm(), 0.0f,
            SoccerParams.BALL_SPEED_MAX, ballVelocityNormSteps);
        s1 = MathTools.valueToIndex(s.getBallVelocityDirection(), -180.0f,
            180.0f, ballVelocityDirectionSteps);
        s2 = MathTools.valueToIndex(s.getPlayerVelocityNorm(), 0.0f,
            SoccerParams.PLAYER_SPEED_MAX, playerVelocityNormSteps);
        s3 = MathTools.valueToIndex(s.getPlayerVelocityDirection(), -180.0f,
            180.0f, playerVelocityDirectionSteps);
        s4 = MathTools.valueToIndex(s.getPlayerBodyDirection(), -180.0f,
            180.0f, playerBodyDirectionSteps);
        s5 = MathTools.valueToIndex(s.getRelativeDistance(), 0.0f, 125.0f,
            relativeDistanceSteps);
        s6 = MathTools.valueToIndex(s.getRelativeDirection(), -180.0f, 180.0f,
            relativeDirectionSteps);

        float max = -1000000.0f;
        Action a = null;
        for (int i = 0; i < qTable[s0][s1][s2][s3][s4][s5][s6].length; i++)
        {
            if (qTable[s0][s1][s2][s3][s4][s5][s6][i] > max)
            {
                max = qTable[s0][s1][s2][s3][s4][s5][s6][i];

                if (i < turnSteps)
                {
                    a = new Action((float) MathTools.indexToValue(i, -180.0f,
                        180.0f, turnSteps, 0.5f), true);
                }
                else
                {
                    a = new Action((float) MathTools.indexToValue(
                        i - turnSteps, 0.0f, 100.0f, dashSteps, 1.0f), false);
                }
            }

        }

        return a;
    }

    /**
     * 
     */
    private void initQtables()
    {
        for (int i = 0; i < oldQtable.length; i++)
        {
            for (int j = 0; j < oldQtable[i].length; j++)
            {
                for (int k = 0; k < oldQtable[i][j].length; k++)
                {
                    for (int l = 0; l < oldQtable[i][j][k].length; l++)
                    {
                        for (int m = 0; m < oldQtable[i][j][k][l].length; m++)
                        {
                            for (int n = 0; n < oldQtable[i][j][k][l][m].length; n++)
                            {
                                for (int o = 0; o < oldQtable[i][j][k][l][m][n].length; o++)
                                {
                                    for (int p = 0; p < oldQtable[i][j][k][l][m][n][o].length; p++)
                                    {
                                        oldQtable[i][j][k][l][m][n][o][p] = 0.0f;
                                        qTable[i][j][k][l][m][n][o][p] = 0.0f;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @param q0
     * @param q1
     * @return
     */
    private boolean q0EqualsQ1(float[][][][][][][][] q0,
                               float[][][][][][][][] q1)
    {
        for (int i = 0; i < q0.length; i++)
        {
            for (int j = 0; j < q0[i].length; j++)
            {
                for (int k = 0; k < q0[i][j].length; k++)
                {
                    for (int l = 0; l < q0[i][j][k].length; l++)
                    {
                        for (int m = 0; m < q0[i][j][k][l].length; m++)
                        {
                            for (int n = 0; n < q0[i][j][k][l][m].length; n++)
                            {
                                for (int o = 0; o < q0[i][j][k][l][m][n].length; o++)
                                {
                                    for (int p = 0; p < q0[i][j][k][l][m][n][o].length; p++)
                                    {
                                        if (Math.abs(q0[i][j][k][l][m][n][o][p]
                                                - q1[i][j][k][l][m][n][o][p]) > 0.001f)
                                        {
                                            return false;
                                        }
                                    }

                                }

                            }

                        }

                    }

                }

            }

        }

        return true;

    }

    /**
     * 
     */
    private void testQ()
    {

        for (int i = 0; i < qTable.length; i++)
        {
            for (int j = 0; j < qTable[i].length; j++)
            {
                for (int k = 0; k < qTable[i][j].length; k++)
                {
                    for (int l = 0; l < qTable[i][j][k].length; l++)
                    {
                        for (int m = 0; m < qTable[i][j][k][l].length; m++)
                        {
                            for (int n = 0; n < qTable[i][j][k][l][m].length; n++)
                            {
                                for (int o = 0; o < qTable[i][j][k][l][m][n].length; o++)
                                {
                                    for (int p = 0; p < qTable[i][j][k][l][m][n][o].length; p++)
                                    {
                                        if (qTable[i][j][k][l][m][n][o][p] > 2f * MarkovDecisionProcess.BIG_REWARD)
                                        {
                                            System.out
                                                .println("prob: "
                                                        + qTable[i][j][k][l][m][n][o][p]);
                                        }
                                    }

                                }

                            }

                        }

                    }

                }

            }

        }

    }

    /**
     * @param filename
     */
    public void save(String filename)
    {
        try
        {
            FileOutputStream fos = new FileOutputStream(filename);
            GZIPOutputStream gzos = new GZIPOutputStream(fos);
            ObjectOutputStream out = new ObjectOutputStream(gzos);
            out.writeObject(this);
            out.flush();
            out.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    /**
     * 
     */
    public void save()
    {
        save(this.getName());
    }

    /**
     * @param filename
     * @return
     */
    public static synchronized Qiteration loadQl(String filename)
    {
        Qiteration q = null;
        try
        {
            System.out.println("Loading QTable...");
            FileInputStream fis = new FileInputStream(filename);
            GZIPInputStream gzis = new GZIPInputStream(fis);
            ObjectInputStream in = new ObjectInputStream(gzis);
            q = (Qiteration) in.readObject();
            in.close();
            System.out.println("QTable loaded: ");
            System.out.println(q);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return q;
    }

    /**
     * 
     */
    public void printQl()
    {
        for (int i = 0; i < 10; i++)
        {
            int i0, i1, i2, i3, i4, i5, i6, i7;
            i0 = (int) (Math.ceil(Math.random() * (ballVelocityNormSteps - 1)));
            i1 = (int) (Math.ceil(Math.random()
                    * (ballVelocityDirectionSteps - 1)));
            i2 = (int) (Math.ceil(Math.random() * (ballVelocityNormSteps - 1)));
            i3 = (int) Math.ceil(Math.random()
                    * (ballVelocityDirectionSteps - 1));
            i4 = (int) (Math.ceil(Math.random() * (relativeDirectionSteps - 1)));
            i5 = (int) (Math.ceil(Math.random() * (relativeDistanceSteps - 1)));
            i6 = (int) Math.ceil(Math.random() * (relativeDirectionSteps - 1));
            i7 = (int) (Math
                .ceil(Math.random() * ((dashSteps + turnSteps - 1))));

            System.out.println(i0 + " " + i1 + " " + i2 + " " + i3 + " " + i4
                    + " " + i5 + " " + i6 + " " + i7 + " ");
            System.out.println(qTable[i0][i1][i2][i3][i4][i5][i6][i7]);
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
        computeQl();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        String str = "";
        int stateSpaceSize = ballVelocityNormSteps * ballVelocityDirectionSteps
                * playerVelocityNormSteps * playerVelocityDirectionSteps
                * playerBodyDirectionSteps * relativeDistanceSteps
                * relativeDirectionSteps;
        int actionSpaceSize = (turnSteps + dashSteps);

        str += "X = {" + ballVelocityNormSteps + ", "
                + ballVelocityDirectionSteps + ", " + playerVelocityNormSteps
                + ", " + playerVelocityDirectionSteps + ", "
                + playerBodyDirectionSteps + ", " + relativeDistanceSteps
                + ", " + relativeDirectionSteps + "}" + "\n";

        str += "U = {" + turnSteps + ", " + dashSteps + "}" + "\n";

        str += "X x U² = "
                + (stateSpaceSize * actionSpaceSize * actionSpaceSize) + "\n";

        str += "Discount factor: " + discountFactor + "\n";

        str += "Total number of iterations done so far: " + totalNbOfIterations
                + "\n";
        str += "Total computation time so far (min): "
                + ((float) (totalComputationTime) / 1000f / 60f) + "\n";

        return str;
    }

    @Override
    public String getName()
    {
        String discountFactor = "" + this.discountFactor;
        discountFactor = discountFactor.replaceAll("\\.", "-");

        return "Qit_" + ballVelocityNormSteps + "_"
                + ballVelocityDirectionSteps + "_" + playerVelocityNormSteps
                + "_" + playerVelocityDirectionSteps + "_"
                + playerBodyDirectionSteps + "_" + relativeDistanceSteps + "_"
                + relativeDirectionSteps + "_" + discountFactor + "_"
                + totalNbOfIterations + ".zip";
    }
}
