import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import javax.swing.plaf.basic.BasicTreeUI.SelectionModelPropertyChangeHandler;

public class ReceiverSocket extends Receiver{
	private DatagramSocket socket;
	
	ReceiverSocket() {
		super();
		socket = null;
		setServerPort("12112");
		setIotPort("12114");
	}
	
	public void close() {
		if (socket != null && !socket.isClosed()) {
			socket.close();
			setAddress(null);
			setPort(null);
		}
	}
	
	public void open(String port, boolean isBroadcast) {
		if (socket == null || socket.isClosed()) {
			//TODO: verify if port range is valid
			try {
				socket = new DatagramSocket(null);
				InetSocketAddress isa = new InetSocketAddress(InetAddress.getByName("0.0.0.0"), 
						Integer.parseInt(port));
				setAddress(isa.getAddress().getHostAddress());
				setPort(String.valueOf(isa.getPort()));
				socket.bind(isa);
				socket.setBroadcast(isBroadcast);
			}
			catch (IOException ex) {
				System.out.println("Error: " + ex.toString());
				ex.printStackTrace(System.out);
				System.out.println("socket == null" + socket == null);
				System.out.println("socket.isClosed()" + socket.isClosed());
				System.out.println(socket.getLocalAddress().getHostAddress() + ':' + port);
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
				String msgStr = new String(packetMsg.getData());
				if (ProtocolMessage.getMessageCode(msgStr).trim().equals(code.trim())) {
					toReturn.add(packetMsg);
				}
			}
		} while (toReturn.size() < packetsMax);
		
		return toReturn;	
	}
	
	
	//returns packages received with one of the given codes
	//e.g. DISCVR_IOT, CONFRM_IOT, and CANICON_ID
	//TODO: create struct or class for codes
	private ArrayList<DatagramPacket> runSocket(int packetsMax, final String[] codes) throws IOException {
		ArrayList<DatagramPacket> toReturn = new ArrayList<DatagramPacket>();
		do {
			ArrayList<DatagramPacket> packetsRcvd = runSocket(packetsMax);
			for (DatagramPacket packetMsg : packetsRcvd) {
				String packetMsgStr = new String(packetMsg.getData());
				String msgReceived = ProtocolMessage.getMessageCode(packetMsgStr).trim();
				for (int i = 0; i < codes.length; i++) {
					if (msgReceived.equals(codes[i])) {
						toReturn.add(packetMsg);
						break;
					}
				}
			}
		} while (toReturn.size() < packetsMax);
		
		return toReturn;	
	}
	

	private String extractDatagramContent(DatagramPacket packet) {
		setPeerAddress(packet.getAddress().getHostAddress());
		return new String(packet.getData());
	}
	
	private ArrayList<String> extractDatagramContent(ArrayList<DatagramPacket> packets) {
		ArrayList<String> content = new ArrayList<String>(packets.size());
		for (DatagramPacket packet : packets) {
			content.add(extractDatagramContent(packet));
		}
		return content;
	}
	
	@Override
	public	ArrayList<String> receiveData(int packetsMax) {
		//returns the content of all packages read in order.
		try {
			socket.setSoTimeout(0);
			return extractDatagramContent(runSocket(packetsMax));
		}
		catch (Exception e) {
			return null;
		}
	}
	
	public String receiveData(String code) {
		return receiveData(1, code).get(0);
	}
	
	//returns packages received with a given code
	//e.g. DISCVR_IOT, CONFRM_IOT, and CANICON_ID
	//TODO: create struct or class for codes
	@Override
	public  ArrayList<String> receiveData(int packetsMax, String code) {
		try {
			socket.setSoTimeout(0);
			ArrayList<DatagramPacket> toReturn = runSocket(packetsMax, code);
			return extractDatagramContent(toReturn);
		}
		catch (IOException e) {
			return null;
		}
	}
	
	@Override
	public String receiveData(String code, int timeout) {
		try {
			socket.setSoTimeout(timeout);
			DatagramPacket packet = runSocket(1, code).get(0);
			setPeerAddress(packet.getAddress().getHostAddress());
			return extractDatagramContent(packet);
		}
		catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public String receiveData(final String[] codes, int timeout) {
		try {
			socket.setSoTimeout(timeout);
			DatagramPacket toReturn = runSocket(1, codes).get(0);
			setPeerAddress(toReturn.getAddress().getHostAddress());
			return extractDatagramContent(toReturn);
		}
		catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public ArrayList<String> receiveData(int packetsMax, int timeout) {
		try {
			socket.setSoTimeout(timeout);
			ArrayList<DatagramPacket> toReturn = runSocket(packetsMax);
			return extractDatagramContent(toReturn);
		}
		catch (Exception e) {
			return null;
		}
	}

	
	@Override
	public boolean isClosed() {
		return socket.isClosed();
	}
}
