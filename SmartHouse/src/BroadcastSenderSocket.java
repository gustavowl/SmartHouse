//import java.net.DatagramPacket;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class BroadcastSenderSocket {
	DatagramSocket socket = null;
	
	BroadcastSenderSocket() {
		this(12113);
	}
	
	BroadcastSenderSocket(int port) {
		//TODO: verify if port range is valid
		try {
			socket = new DatagramSocket(port, InetAddress.getByName("0.0.0.0"));
			socket.setBroadcast(true);
			
		}
		catch (IOException ex) {
			System.out.println("Error: " + ex.toString());
		}
	}
	
	void sendData(byte[] content) {
		//TODO: check content size before sending
		try {
			DatagramPacket dp = new DatagramPacket(content, content.length, 
					InetAddress.getByName("255.255.255.255"), 12112);
			socket.send(dp);
			System.out.println("Message sent via broadcast");
		}
		catch (Exception e) {
			System.out.println("Error: " + e.toString());
		}
	}
	
	//TODO: Destructor? Close socket
	
}
