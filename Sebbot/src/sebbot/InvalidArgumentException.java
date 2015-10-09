package sebbot;
/**
 * @author Sebastien Lentz
 *
 */
public class InvalidArgumentException extends Exception
{

    public InvalidArgumentException()
    {
    }

    public InvalidArgumentException(String message)
    {
        super(message);
    }

    public InvalidArgumentException(Throwable cause)
    {
        super(cause);
    }

    public InvalidArgumentException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
