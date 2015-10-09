package sebbot.ballcapture;

import sebbot.MathTools;

/**
 * @author Sebastien Lentz
 *
 */
public class Action
{
    private static int dashSteps = 2;
    private static int turnSteps = 10;

    private float      value;    // Value of the action:-180<=turn<=180 or 0<=dash<=100.
    private boolean    isTurn;   // True if action = turn, false if action = dash.
    private int        actionNb; // 0 <= actionNb < dashSteps + turnSteps.

    /*
     * =========================================================================
     * 
     *                     Constructors and destructors
     * 
     * =========================================================================
     */
    /**
     * @param value
     * @param isTurn
     */
    public Action(float value, boolean isTurn)
    {
        this.value = value;
        this.isTurn = isTurn;
        updateActionNb();
    }

    /**
     * @param actionNo
     */
    public Action(int actionNb)
    {
        this.actionNb = actionNb;
        updateFromActionNb();
    }

    /*
     * =========================================================================
     * 
     *                     Getters and setters
     * 
     * =========================================================================
     */
    /**
     * @return the dashSteps
     */
    public static int getDashSteps()
    {
        return dashSteps;
    }

    /**
     * @param dashSteps the dashSteps to set
     */
    public static void setDashSteps(int dashSteps)
    {
        Action.dashSteps = dashSteps;
    }

    /**
     * @return the turnSteps
     */
    public static int getTurnSteps()
    {
        return turnSteps;
    }

    /**
     * @param turnSteps the turnSteps to set
     */
    public static void setTurnSteps(int turnSteps)
    {
        Action.turnSteps = turnSteps;
    }

    /**
     * @return the value
     */
    public float getValue()
    {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(float value)
    {
        this.value = value;
    }

    /**
     * @return the isTurn
     */
    public boolean isTurn()
    {
        return isTurn;
    }

    /**
     * @param isTurn the isTurn to set
     */
    public void setTurn(boolean isTurn)
    {
        this.isTurn = isTurn;
    }

    /**
     * @return the actionNb
     */
    public int getActionNb()
    {
        return actionNb;
    }

    /**
     * @param actionNo the actionNo to set
     */
    public void setActionNb(int actionNb)
    {
        this.actionNb = actionNb;
        updateFromActionNb();
    }

    /*
     * =========================================================================
     * 
     *                          Main methods
     * 
     * =========================================================================
     */
    /**
     * @param dashSteps
     * @param turnSteps
     * @return
     */
    public Action discretize(int dashSteps, int turnSteps)
    {
        if (isTurn)
        {
            value = (float) MathTools.discretize(value, -180.0f, 180.0f,
                turnSteps, 0.5f);
        }
        else
        {
            value = (float) MathTools.discretize(value, 0.0f, 100.0f,
                dashSteps, 1.0f);
        }

        return this;
    }

    public Action discretize()
    {
        discretize(dashSteps, turnSteps);

        return this;
    }

    public String toString()
    {
        String str = "{" + (isTurn ? "TURN" : "DASH") + ", " + value + ", " + actionNb + "}";

        return str;
    }

    /*
     * =========================================================================
     * 
     *                          Misc methods
     * 
     * =========================================================================
     */
    private void updateFromActionNb()
    {
        if (this.actionNb < turnSteps)
        {
            this.isTurn = true;
            this.value = (float) MathTools.indexToValue(actionNb, -180.0d,
                180.0d, turnSteps, 0.5f);
        }
        else
        {
            this.isTurn = false;
            this.value = (float) MathTools.indexToValue(actionNb - turnSteps,
                0.0d, 100.0d, dashSteps, 1.0f);
        }
    }

    private void updateActionNb()
    {
        if (this.isTurn)
        {
            this.actionNb = MathTools.valueToIndex(this.value, -180.0d, 180.0d,
                turnSteps);
        }
        else
        {
            this.actionNb = MathTools.valueToIndex(this.value, 0.0d, 100.0d,
                dashSteps)
                    + turnSteps;
        }
    }

}
