import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPServer extends Thread
{
	
	private DatagramSocket socket;
	
	public UDPServer(int port) throws SocketException
	{
		socket=new DatagramSocket(port);
		socket.setSoTimeout(1000); //Attende per 1 s la ricezione di un dato
	}
	
	public void run()
	{
		DatagramPacket answer;
		
		byte[] buffer=new byte[8192];
		DatagramPacket request=new DatagramPacket(buffer,buffer.length);
		
		
			while(!interrupted())
			{
				try
				{
					socket.receive(request);
					answer=new DatagramPacket(request.getData(),request.getLength(),request.getAddress(),request.getPort());
					socket.send(answer);
				}
				
				catch (IOException e) 
				{
					System.err.println("Timeout");
				}		
			} 
			socket.close();
	
	}

	public static void main(String[] args) throws IOException 
	{
		try 
		{
			UDPServer echoServer= new UDPServer(7);
			echoServer.start();
			int stop=System.in.read();	//pressione pulsante
			echoServer.interrupt();		//interruzione thread echoServer
			echoServer.join();			//attesa fine esecuzione del thread echoServer
		} 
		catch (SocketException e) 
		{
			System.err.println("Errore di comunicazione");
		} 
		catch (InterruptedException e) 
		{
			
			e.printStackTrace();
		}

	}

}
