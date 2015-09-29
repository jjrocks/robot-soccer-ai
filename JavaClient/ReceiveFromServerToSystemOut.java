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
			System.out.println("Receiving: " + receive());
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
}