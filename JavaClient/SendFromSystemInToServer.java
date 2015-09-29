import java.lang.*;
import java.net.*;
import java.io.*;
import java.util.*;
public class SendFromSystemInToServer implements Runnable
{
	private DatagramSocket socket;
	private Scanner scanner = new Scanner(System.in);

	public SendFromSystemInToServer(DatagramSocket inSocket)
	{
		System.out.println("Sending thread initialized.");
		socket = inSocket;
	}

	public void run()
	{
		while(true)
		{
			send(scanner.nextLine());
		}
	}

	private void send(String message)
	{
		System.out.println("Sending: " + message);

		byte[] buffer = message.getBytes();

		try
		{
			DatagramPacket pkt = new DatagramPacket(buffer, buffer.length, 
						InetAddress.getByName("compute212.cs.lafayette.edu"), 6000);
			
			try
			{
				socket.send(pkt);
			}
			catch (IOException e)
			{
				System.out.println("Socket sending error: " + e);
			}		

		}
		catch (UnknownHostException e)
		{
			System.out.println("Unknown host exception: " + e);
		}
	}
}