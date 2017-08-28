//import java.net.DatagramPacket;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class BroadcastSenderSocket {
	DatagramSocket socket = null;
	
	BroadcastSenderSocket() {
		try {
			socket = new DatagramSocket(12112, InetAddress.getByName("0.0.0.0"));
		}
		catch (IOException ex) {
			System.out.println("Error: " + ex.toString());
		}
	}
	
}
