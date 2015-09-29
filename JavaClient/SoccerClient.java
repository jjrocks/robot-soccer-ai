import java.net.*;
import java.io.*;
import java.util.*;

public class SoccerClient
{
	private static DatagramSocket socket;

	public static void main(String[] args)
	{
		try
		{
			socket = new DatagramSocket();
			(new Thread(new SendFromSystemInToServer(socket))).start();
			(new Thread(new ReceiveFromServerToSystemOut(socket))).start();

		}
		catch (SocketException e)
		{
			System.out.println("Socket exception: " + e);
		}
		
	}	

}
