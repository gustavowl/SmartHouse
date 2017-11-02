//import java.net.DatagramPacket;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class SenderSocket {
	private DatagramSocket socket = null;
	
	SenderSocket() {
		this(12113);
	}
	
	SenderSocket(int port) {
		this("0.0.0.0", port);
		
		try {
			socket.setBroadcast(true);
		}
		catch (SocketException e) {
			System.out.println("Error: " + e.toString());
		}
	}
	
	SenderSocket(String address) {
		this(address, 12113);
	}
	
	SenderSocket(String address, int port) {
		//TODO: verify if port range is valid
		try {
			socket = new DatagramSocket(port, InetAddress.getByName(address));
		}
		catch (IOException e) {
			System.out.println("Error: " + e.toString());
		}
	}
	
	public void close() {
		socket.close();
	}
	
	void sendData(byte[] content) {
		sendData(content, "255.255.255.255", 12112);
	}
	
	void sendData(byte[] content, int port) {
		//TODO: check content size before sending
		sendData(content, "255.255.255.255", port);
	}

	void sendData(byte[] content, String address, int port) {
		//TODO: check content size before sending
		try {
			DatagramPacket dp = new DatagramPacket(content, content.length, 
					InetAddress.getByName(address), port);
			socket.send(dp);
			//FIXME: Delete next line
			//System.out.println("Message sent to " + address + ':' + Integer.toString(port));
		}
		catch (Exception e) {
			System.out.println("Error: " + e.toString());
			e.printStackTrace(System.out);
		}
	}
	
	public boolean isClosed() {
		return socket.isClosed();
	}
	
	//TODO: Destructor? Close socket
	
}
