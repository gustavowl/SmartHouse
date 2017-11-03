import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;

public class ReceiverSocket {
	private DatagramSocket socket;
	
	ReceiverSocket() {
		socket = null;
	}
	
	/*ReceiverSocket() {
		this(12112);
	}
	
	ReceiverSocket(int port) {
		this("0.0.0.0", port);
		try {
			socket.setBroadcast(true);
		}
		catch (IOException e) {
			System.out.println("Error: " + e.toString());
		}
	}
	
	ReceiverSocket(String address) {
		this(address, 12112);
	}
	
	ReceiverSocket(String address, int port) {
		//TODO: verify if port range is valid
		try {
			socket = new DatagramSocket(port, InetAddress.getByName(address));
			
		}
		catch (IOException ex) {
			System.out.println("Error: " + ex.toString());
		}
	}*/
	
	public void close() {
		if (socket != null && !socket.isClosed()) {
			socket.close();
		}
	}
	
	public void open() {
		open(12112);
	}
	
	public void open(int port) {
		open("0.0.0.0", port);
		if (socket != null) {
			try {
				socket.setBroadcast(true);
			}
			catch (IOException e) {
				System.out.println("Error: " + e.toString());
				e.printStackTrace(System.out);
			}
		}
	}
	
	public void open(String address) {
		open(address, 12112);
	}
	
	public void open(String address, int port) {
		if (socket == null || socket.isClosed()) {
			//TODO: verify if port range is valid
			try {
				socket = new DatagramSocket(null);
				socket.bind(new InetSocketAddress(InetAddress.getByName(address), port));
				socket.setBroadcast(false);
			}
			catch (IOException ex) {
				System.out.println("Error: " + ex.toString());
				ex.printStackTrace(System.out);
				System.out.println("socket == null" + socket == null);
				System.out.println("socket.isClosed()" + socket.isClosed());
				System.out.println(address + ':' + port);
			}
		}
	}
	
	private ArrayList<DatagramPacket> runSocket(int packetsMax) throws IOException {
		//returns the content of all packages read in order.
		ArrayList<DatagramPacket> toReturn = new ArrayList<DatagramPacket>();
		//max number of packets to be received. -1 = infinity
		while (packetsMax != 0) {
			byte[] rcvdInfo = new byte[256];
			DatagramPacket packet = new DatagramPacket(rcvdInfo, rcvdInfo.length);
			socket.receive(packet);
			
	        //Packet received
			toReturn.add(packet);
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
	private ArrayList<DatagramPacket> runSocket(int packetsMax, String code) throws IOException {
		ArrayList<DatagramPacket> toReturn = new ArrayList<DatagramPacket>();
		do {
			ArrayList<DatagramPacket> packetsRcvd = runSocket(packetsMax);
			for (DatagramPacket packetMsg : packetsRcvd) {
				//FIXME: remove
				/*System.out.println("FIXME: rcvd/xpctd - " + ProtocolMessage.getMessageCode(packetMsg.getData()) +
						"/" + code);*/
				String temp = ProtocolMessage.getMessageCode(packetMsg.getData()).trim(); //FIXME: DELETE
				if (ProtocolMessage.getMessageCode(packetMsg.getData()).trim().equals(code.trim())) {
					toReturn.add(packetMsg);
				}
			}
		} while (toReturn.size() < packetsMax);
		
		return toReturn;	
	}
	
	ArrayList<DatagramPacket> receiveData(int packetsMax) {
		//returns the content of all packages read in order.
		try {
			socket.setSoTimeout(0);
			return runSocket(packetsMax);
		}
		catch (Exception e) {
			return null;
		}
	}
	
	//returns packages received with a given code
	//e.g. DISCVR_IOT, CONFRM_IOT, and CANICON_ID
	//TODO: create struct or class for codes
	ArrayList<DatagramPacket> receiveData(int packetsMax, String code) {
		try {
			socket.setSoTimeout(0);
			ArrayList<DatagramPacket> toReturn = runSocket(packetsMax, code);
			return toReturn;
		}
		catch (IOException e) {
			return null;
		}
	}
	
	DatagramPacket receiveData(String code, int timeout) {
		try {
			socket.setSoTimeout(timeout);
			ArrayList<DatagramPacket> toReturn = runSocket(1, code);
			return toReturn.get(0);
		}
		catch (Exception e) {
			return null;
		}
	}
	
	ArrayList<DatagramPacket> receiveData(int packetsMax, int timeout) {
		try {
			socket.setSoTimeout(timeout);
			ArrayList<DatagramPacket> toReturn = runSocket(packetsMax);
			return toReturn;
		}
		catch (Exception e) {
			return null;
		}
	}
	
	//TODO: Destructor? Close socket
}
