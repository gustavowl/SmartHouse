import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class BroadcastReceiverSocket {
	DatagramSocket socket = null;
	
	BroadcastReceiverSocket() {
		this(12112);
	}
	
	BroadcastReceiverSocket(int port) {
		//TODO: verify if port range is valid
		try {
			socket = new DatagramSocket(port, InetAddress.getByName("0.0.0.0"));
			socket.setBroadcast(true);
			
		}
		catch (IOException ex) {
			System.out.println("Error: " + ex.toString());
		}
	}
	
	void receiveData(int packetsMax) {
		try {
			//max number of packets to be received. -1 = infinity
			while (packetsMax != 0) {
				byte[] rcvdInfo = new byte[10];
				DatagramPacket packet = new DatagramPacket(rcvdInfo, rcvdInfo.length);
				socket.receive(packet);
				
		        //Packet received
				System.out.println("-------------------------");
				System.out.println(getClass().getName() + "Packet received from: " + packet.getAddress().getHostAddress());
				System.out.println(getClass().getName() + "Data: " + new String(packet.getData()));
				
				if (packetsMax > 0) {
					packetsMax--;
				}
			}
		}
		catch (Exception e) {
			System.out.println("Error: " + e.toString());
		}
	}
	
	//TODO: Destructor? Close socket
}
