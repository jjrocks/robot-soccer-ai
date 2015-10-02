import java.lang.*;
import java.net.*;
import java.io.*;
import java.util.*;
public class ReceiveFromServerToSystemOut implements Runnable
{
	private DatagramSocket socket;

	public ReceiveFromServerToSystemOut(DatagramSocket inSocket)
	{
		System.out.println("Receiving thread initialized.");

		socket = inSocket;
	}

	public void run()
	{
		while(true)
		{
			parse(receive());
		}
	}

	private String receive()
	{
		byte[] buffer = new byte[4096];
		DatagramPacket pkt = new DatagramPacket(buffer, buffer.length);
		try
		{
			socket.receive(pkt);
		}
		catch (IOException e)
		{
			System.out.println("Socket recieving error: " + e);
		}

		return new String(buffer);
	}

	private void parse(String input)
	{
		if(input.substring(0,4).equals("(see"))
		{
			int index = input.indexOf("(b)");
			if(index > -1)
			{
				String blah = input.substring(index+3);
				Scanner sc = new Scanner(blah);
				System.out.println(blah);
				double ballDist = sc.nextDouble();
				String a = sc.next();
				System.out.println("a: " + a);
				if(a.substring(a.length()-1).equals(")"))
				{
					a = a.substring(0,a.length()-1);
					System.out.println("modded a: " + a);
				}
				double ballDir = Double.parseDouble(a);
				if(ballDir > 50)
				{
					send("(turn " + ballDir + ")");
				}
				else
				{
					if(ballDist > 1)
					{
						send("(dash 10)");
					}
					else
					{
						send("(kick 10 0)");
					}
				}
			}
			else
			{
				send("(turn 50)");
			}
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