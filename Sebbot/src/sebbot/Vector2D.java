package sebbot;

/**
 * @author Sebastien Lentz
 *
 */
public class Vector2D implements Cloneable
{
    private double x;
    private double y;

    public Vector2D(double component1, double component2,
            boolean arePolarCoordinates)
    {
        if (arePolarCoordinates)
        { // component1 = radius, component2 = angle.
            this.x = component1 * Math.cos(Math.toRadians(component2));
            this.y = component1 * Math.sin(Math.toRadians(component2));
        }
        else
        { // component1 = x, component2 = y.
            this.x = component1;
            this.y = component2;
        }
    }
    
    public Vector2D(double x, double y)
    {
        this(x,y,false);
    }


    public Object clone()
    {
        Vector2D cloneVector;
        try
        {
            cloneVector = (Vector2D) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            e.printStackTrace();
            cloneVector = null;
        }

        return cloneVector;
    }

    /**
     * @return the x
     */
    public double getX()
    {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(double x)
    {
        this.x = x;
    }

    /**
     * @return the y
     */
    public double getY()
    {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(double y)
    {
        this.y = y;
    }

    public Vector2D subtract(Vector2D v) throws NullVectorException
    {
        if (v == null)
        {
            throw new NullVectorException();
        }

        return new Vector2D(x - v.getX(), y - v.getY());
    }

    public Vector2D add(Vector2D v) throws NullVectorException
    {
        if (v == null)
        {
            throw new NullVectorException();
        }

        return new Vector2D(x + v.getX(), y + v.getY());
    }

    public Vector2D multiply(double f)
    {
        return new Vector2D(x * f, y * f);
    }

    public double multiply(Vector2D v) throws NullVectorException
    {
        if (v == null)
        {
            throw new NullVectorException();
        }

        return x * v.getX() + y * v.getY();
    }

    public double polarRadius()
    {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    public double polarAngle()
    {
        double angle;
        if ((y == 0) && (x == 0))
        {
            angle = 0.0D;
        }
        else
        {
            angle = Math.toDegrees(Math.atan2(y, x));
        }

        return angle;

    }

    public Vector2D normalize(double modulusMax)
    {
        double currentModulus = polarRadius();
        if (currentModulus > modulusMax)
        {
            this.x *= (modulusMax / currentModulus);
            this.y *= (modulusMax / currentModulus);
        }
        
        return this;
    }

    public double distanceTo(double x, double y)
    {
        return (new Vector2D(x, y)).subtract(this).polarRadius();
    }

    public double distanceTo(Vector2D v) throws NullVectorException
    {
        if (v == null)
        {
            throw new NullVectorException();
        }

        return v.subtract(this).polarRadius();
    }

    public double distanceTo(MobileObject o) throws NullVectorException
    {
        if (o == null)
        {
            throw new NullVectorException();
        }

        return distanceTo(o.getPosition());
    }

    public double directionOf(double x, double y)
    {
        return (new Vector2D(x, y)).subtract(this).polarAngle();
    }

    public double directionOf(Vector2D v) throws NullVectorException
    {
        if (v == null)
        {
            throw new NullVectorException();
        }

        return v.subtract(this).polarAngle();
    }

    public double directionOf(MobileObject o) throws NullVectorException
    {
        if (o == null)
        {
            throw new NullVectorException();
        }

        return directionOf(o.getPosition());
    }

    /*
     * =========================================================================
     * 
     *                          Other methods
     * 
     * =========================================================================
     */
    public String toString()
    {
        return String.format("(%g - %g)", x, y);
    }

}
