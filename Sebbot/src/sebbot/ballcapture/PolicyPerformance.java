package sebbot.ballcapture;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

import sebbot.MathTools;
import sebbot.SoccerParams;

/**
 * @author Sebastien Lentz
 *
 */
public class PolicyPerformance
{
    private static LinkedList<State> initialStates;

    static
    {
        initialStates = new LinkedList<State>();

        State s;
        for (float i = 0; i < 3.0f; i += 2.5f)
        {
            for (float j = -180.0f; j < 180.0f; j += 54.0f)
            {
                for (float k = 0f; k < 0.4f; k += 0.39f)
                {
                    for (float l = -180.0f; l < 180.0f; l += 120.0f)
                    {
                        for (float m = -162.0f; m < 180.0f; m += 92f)
                        {
                            for (float n = 2f; n < 100.0f; n += 17.0f)
                            {
                                for (float o = -180.0f; o < 180.0f; o += 81.0f)
                                {
                                    s = new State(i, j, k, l, m, n, o);
                                    initialStates.add(s);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void testAllDps()
    {

        PrintWriter performanceLog = null;
        try
        {
            performanceLog = new PrintWriter(new FileWriter("performance.log",
                true));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        DirectPolicySearch dps = null;
        String path = File.separator + "home" + File.separator + "s051607"
                + File.separator + "tfe" + File.separator + "sebbot"
                + File.separator;

        float[] performance = new float[2];
        float[] scores = new float[10];
        float[] badTrajectories = new float[10];
        float[] max = new float[2];
        float[] min = new float[2];
        float[] mean = new float[2];
        float[] stdDev = new float[2];

        String line = "";

        for (int nbOfBFs = 12; nbOfBFs <= 12 + 9 * 2; nbOfBFs = nbOfBFs + 2)
        {
            for (int cce = 1; cce <= 3; cce++)
            {
                for (int nbOfIterations = 1; nbOfIterations <= 50; nbOfIterations++)
                {
                    max[0] = 0f;
                    max[1] = 0f;
                    min[0] = 99999999f;
                    min[1] = 99999999f;
                    mean[0] = 0f;
                    mean[1] = 0f;
                    stdDev[0] = 0f;
                    stdDev[1] = 0f;

                    for (int i = 1; i <= 10; i++)
                    {
                        String suffix = i + File.separator + "DPS_" + nbOfBFs
                                + "_" + (cce * nbOfBFs * (4 * 7 + 4)) + "_"
                                + "100" + "_" + nbOfIterations + ".zip";

                        try
                        {
                            dps = DirectPolicySearch.load(path + suffix);
                        }
                        catch (Exception e)
                        {
                            continue;
                        }

                        performance = performance(dps, false);

                        scores[i - 1] = performance[0];
                        badTrajectories[i - 1] = performance[1];
                    }

                    min[0] = MathTools.min(scores);
                    min[1] = MathTools.min(badTrajectories);

                    max[0] = MathTools.max(scores);
                    max[1] = MathTools.max(badTrajectories);

                    mean[0] = MathTools.mean(scores);
                    mean[1] = MathTools.mean(badTrajectories);

                    stdDev[0] = MathTools.stdDev(scores, mean[0]);
                    stdDev[1] = MathTools.stdDev(badTrajectories, mean[1]);

                    float computationTime = (float) dps
                        .getTotalComputationTime() / 1000 / 60;

                    line = dps.getInitialStates().size() + ";" + nbOfBFs + ";"
                            + cce + ";" + nbOfIterations + ";"
                            + computationTime + ";" + min[1] + ";" + max[1]
                            + ";" + stdDev[1] + ";" + mean[1] + ";" + min[0]
                            + ";" + max[0] + ";" + stdDev[0] + ";" + mean[0];

                    performanceLog.println(line);
                    performanceLog.flush();
                }
            }
        }

        performanceLog.close();
    }

    public static float[] performance(Policy policy, boolean logBadTrajectories)
    {
        float[] performance = new float[2];

        PrintWriter badTrajectories = null;
        if (logBadTrajectories)
        {
            try
            {
                badTrajectories = new PrintWriter(new FileWriter(policy
                    .getName()
                        + "_bad_trajectories.log"));
                badTrajectories.println("");
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
        }

        LinkedList<State> ts = new LinkedList<State>();
        LinkedList<Action> ta = new LinkedList<Action>();
        LinkedList<Float> tr = new LinkedList<Float>();
        float score;
        int iterationsDone = 0;
        int nbOfBadTrajectories = 0;
        float totalScore = 0f;
        float averageScore = 0f;
        for (State s : initialStates)
        {
            ts.clear();
            ta.clear();
            tr.clear();

            score = MarkovDecisionProcess.trajectoryReward(s, policy, 500, ts,
                ta, tr);

            if (score < 0f)
            {
                nbOfBadTrajectories++;

                if (logBadTrajectories)
                {
                    badTrajectories.println("Total score: " + score + ":");
                    for (int i = 1; !(ts.isEmpty() || ta.isEmpty()); i++)
                    {
                        badTrajectories.println(i + ": " + ts.removeFirst()
                                + " | " + ta.removeFirst() + " | "
                                + tr.removeFirst());
                    }

                    badTrajectories.println("");
                    badTrajectories
                        .println("----------------------------------------------------");
                    badTrajectories.println("");
                }
            }
            else
            {
                totalScore += score;
            }

            iterationsDone++;

            if (iterationsDone == 10 && nbOfBadTrajectories == 10)
            {
                totalScore = 0f;
                averageScore = 0f;
                nbOfBadTrajectories = initialStates.size();
                break;
            }
        }
        if (logBadTrajectories)
        {
            badTrajectories.println("Total number of bad trajectories: "
                    + nbOfBadTrajectories);
            badTrajectories.close();
        }

        if (nbOfBadTrajectories != initialStates.size())
        {
            averageScore = totalScore
                    / (initialStates.size() - nbOfBadTrajectories);
        }

        performance[0] = averageScore;
        performance[1] = nbOfBadTrajectories;

        return performance;
    }

    public static void logPerformances(Policy policy, boolean logBadTrajectories)
    {
        float[] performance = performance(policy, logBadTrajectories);

        PrintWriter performanceLog = null;
        try
        {
            performanceLog = new PrintWriter(new FileWriter("performance.log",
                true));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        String line;
        if (policy.getClass() == DirectPolicySearch.class)
        {
            DirectPolicySearch dps = (DirectPolicySearch) policy;
            float totalComputationTime = (float) dps.getTotalComputationTime() / 1000f / 60f;

            line = dps.getName() + ";" + dps.getTotalNbOfIterations() + ";"
                    + totalComputationTime + ";" + performance[0] + ";"
                    + performance[1];
        }
        else if (policy.getClass() == Qiteration.class)
        {
            Qiteration qit = (Qiteration) policy;
            float totalComputationTime = (float) qit.getTotalComputationTime() / 1000f / 60f;

            line = qit.getName() + ";" + qit.getDiscountFactor() + ";"
                    + qit.getTotalNbOfIterations() + ";" + totalComputationTime
                    + ";" + performance[0] + ";" + performance[1];
        }
        else
        {
            line = policy.getName() + ";" + performance[0] + ";"
                    + performance[1];
        }
        performanceLog.println(line);

        performanceLog.close();
    }

    public static float trajectoryReward(State initialState, Policy p,
                                         int nbOfSteps)
    {
        return trajectoryReward(initialState, p, nbOfSteps, null, null, null);
    }

    public static float trajectoryReward(State initialState, Policy p,
                                         int nbOfSteps,
                                         LinkedList<State> statesTrajectory,
                                         LinkedList<Action> actionsTrajectory,
                                         LinkedList<Float> rewardsTrajectory)
    {
        State s = initialState;
        Action a = p.chooseAction(s);
        float trajectoryReward = reward(s, a);

        if (statesTrajectory != null)
        {
            statesTrajectory.add(s);
            actionsTrajectory.add(a);
            rewardsTrajectory.add(reward(s, a));
        }

        int nbOfIterations = 1;
        while (!s.isTerminal() && nbOfIterations < nbOfSteps)
        {
            s = MarkovDecisionProcess.nextState(s, a);
            a = p.chooseAction(s);
            trajectoryReward += reward(s, a);

            if (statesTrajectory != null)
            {
                statesTrajectory.add(s);
                actionsTrajectory.add(a);
                rewardsTrajectory.add(reward(s, a));
            }

            nbOfIterations++;
        }

        return trajectoryReward;
    }

    public static float reward(State s, Action a)
    {
        float reward;

        if (s.isTerminal())
        {
            reward = 0f;
        }
        else
        {
            State nextState = MarkovDecisionProcess.nextState(s, a, false);

            float nextStepDistance = nextState.getRelativeDistance();

            if (nextStepDistance < SoccerParams.KICKABLE_MARGIN)
            {
                reward = 1000f;
            }
            else
            {
                reward = -1f;
            }
        }

        return reward;
    }
}
