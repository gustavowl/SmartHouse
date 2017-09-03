import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

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
	
	private ArrayList<String> runSocket(int packetsMax) throws IOException {
		//returns the content of all packages read in order.
		ArrayList<String> toReturn = new ArrayList<String>();
		//max number of packets to be received. -1 = infinity
		while (packetsMax != 0) {
			byte[] rcvdInfo = new byte[10];
			DatagramPacket packet = new DatagramPacket(rcvdInfo, rcvdInfo.length);
			socket.receive(packet);
			
	        //Packet received
			toReturn.add(packet.getData().toString());
			/* do not print because Single Responsibility
			System.out.println("-------------------------");
			System.out.println(getClass().getName() + "Packet received from: " + packet.getAddress().getHostAddress());
			System.out.println(getClass().getName() + "Data: " + toReturn.get(toReturn.size() - 1)); */
			
			packetsMax--;
		}
		return toReturn;
	}

	//returns packages received with a given code
	//e.g. DISCVR_IOT, CONFRM_IOT, and CANICON_ID
	//TODO: create struct or class for codes
	private ArrayList<String> runSocket(int packetsMax, String code) throws IOException {
		ArrayList<String> toReturn = new ArrayList<String>();
		do {
			ArrayList<String> packetsRcvd = runSocket(packetsMax);
			for (String packetMsg : packetsRcvd) {
				if (packetMsg.equals(code)) {
					toReturn.add(packetMsg);
				}
			}
		} while (toReturn.size() < packetsMax);
		
		return toReturn;	
	}
	
	ArrayList<String> receiveData(int packetsMax) {
		//returns the content of all packages read in order.
		try {
			return runSocket(packetsMax);
		}
		catch (Exception e) {
			System.out.println("Error: " + e.toString());
			return null;
		}
	}
	
	//returns packages received with a given code
	//e.g. DISCVR_IOT, CONFRM_IOT, and CANICON_ID
	//TODO: create struct or class for codes
	ArrayList<String> receiveData(int packetsMax, String code) {
		try {
			return runSocket(packetsMax, code);
		}
		catch (IOException e) {
			System.out.println("Error: " + e.toString());
			return null;
		}
	}
	
	String receiveData(String code, int timeout) {
		try {
			socket.setSoTimeout(timeout);
			ArrayList<String> toReturn = runSocket(1, code);
			return toReturn.get(0);
		}
		catch (Exception e) {
			System.out.println("Error: " + e.toString());
			return null;
		}
	}
	
	//TODO: Destructor? Close socket
}
