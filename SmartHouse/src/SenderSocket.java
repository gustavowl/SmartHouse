//import java.net.DatagramPacket;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class SenderSocket {
	DatagramSocket socket = null;
	
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
	
	void sendData(byte[] content) {
		sendData(content, "255.255.255.255", 12112);
	}
	
	void sendData(byte[] content, int port) {
		//TODO: check content size before sending
		sendData(content, "255.255.255.255", port);
	}

	void sendData(byte[] content, String address, int port) {
		//TODO: check content size before sending
		System.out.println("Sending message to " + address + ':' + Integer.toString(port));
		try {
			DatagramPacket dp = new DatagramPacket(content, content.length, 
					InetAddress.getByName(address), port);
			socket.send(dp);
			System.out.println("Message sent via broadcast");
		}
		catch (Exception e) {
			System.out.println("Error: " + e.toString());
		}
	}
	
	//TODO: Destructor? Close socket
	
}
