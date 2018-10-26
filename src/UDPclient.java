import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

public class UDPclient 
{
	
	DatagramSocket socket;
	
	public UDPclient() throws SocketException
	{
		socket= new DatagramSocket();		//istanzio la socket. La porta non è ancora nota.
		socket.setSoTimeout(1000);
	}
	
	public void closeSocket()
	{
		socket.close();
	}
	
	/**
	 * Spedisce la richiesta e riceve la risposta
	 * @param request stringa che voglio spedire al server come richiesta
	 * @param host Stringa che rapresenta l'IP del server sottoforma di stringa
	 * @param port porta del servizio server
	 * @return
	 * @throws IOException 
	 */
	public String sendAndReceive (String request, String host, int port) throws IOException
	{
		byte[] buffer;
		String answer;	//metterò i dati della risposta del server
		DatagramPacket datagram; //reference sia per il datgramma che uso per inviare la richiesta sia per ricevere la risposta
		
		InetAddress address=InetAddress.getByName(host);	//costruisco l'oggetto InetAddress a partire dall'IP del server
		
		if (socket.isClosed())
		{
			throw new IOException(); 	//verifica che la socket sia aperta altrimenti solleva una eccezione
		}
		
		buffer= request.getBytes("UTF-8");	//request è la stringa che voglio trasmettere. In questo modo la trasformo in una array di byte in codifica UTF-8
		datagram=new DatagramPacket(buffer, buffer.length,address,port); //costruzione del datagramma
		
		socket.send(datagram);	//invio il datagramma
		
		//attendo la ricezione del datagramma (al massimo per 1 s)
		socket.receive(datagram);
		
		//verifico che la risposta ricevuta "arrivi" dal processo a cui ho inviato la richiesta
		//confrontando IP e Porta del datagramma ricevuto con quelli che ho usato per trasmettere
		
		if (datagram.getAddress()==address && datagram.getPort()==port)
		{
			//estraggo i dati dal datagramma e formo la stringa di risposta
			answer=new String(datagram.getData(),0,datagram.getLength(),"ISO-8859-1");
		}
		else  //il datagramma ricevuto non arriva dal servizio richiesto
		{
			throw new SocketTimeoutException();
		}
		closeSocket();
		return answer;
	}
	
	/**
	 * 
	 * @param args argomenti passati ad una istanza di questa classe:
	 * args[0]: stringa contenente IP del server
	 * args[1]: stringa contenente numero di porta del processo sul server
	 * args[2]: stringa contenente il messaggio da inviare al server
	 * Per inserire tali argomenti nel prompt dei comandi basta scrivere le tre stringhe separate
	 * da uno spazio.
	 * Esempio: c:\.....>java UDPclient "127.0.0.1" "7" "ciao ciao lao lao"
	 * 
	 */
	public static void main(String[] args) 
	{
		
		UDPclient client;
		String IP_address;	//indirizzo IP del server
		int UDP_port;		//porta sul server
		String request;		//messaggio da inviare
		String answer;		//risposta del server
		
		if (args.length!=3)
		{
			IP_address="127.0.0.1";
			UDP_port=7;
			request="Hello World!";
		}
		
		else
		{
			IP_address=args[0];
			UDP_port=Integer.parseInt(args[1]);
			request=args[2];
		}
		
		try 
		{
			client=new UDPclient();
			answer=client.sendAndReceive(request, IP_address, UDP_port);
			System.out.println("Risposta ricevuta dal srver: "+answer);
			client.closeSocket();
		}
		catch (SocketException e) 
		{
			System.err.println("Errore creazione socket");
		}
		catch (SocketTimeoutException e) 
		{
			System.err.println("Nessuna risposta dal server");
		}
		catch (IOException e) 
		{
			System.err.println("Errore generico");
		}
		
	}

}
