import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Scanner;

public class LocalReceiverSocket extends ReceiverSocket {
	
	LocalReceiverSocket() {
		super();
	}
	
	LocalReceiverSocket(int port) {
		super();
		open(port, true);
	}
	
	LocalReceiverSocket(int port, boolean isBroadcast) {
		super();
		open(port, isBroadcast);
	}
	
	void readData(int packetsMax) {
		printReceivedPackets(super.receiveData(packetsMax));
	}
	
	void readData(int packetsMax, String code) {
		printReceivedPackets(super.receiveData(packetsMax, code));
	}
	
	//FIXME: not closing socket
	void readData(String code, int timeout) {
		DatagramPacket dp = super.receiveData(code, timeout);
		ArrayList<DatagramPacket> datagram_list = new ArrayList<DatagramPacket>();
		datagram_list.add(dp);
		printReceivedPackets(datagram_list);
	}
	
	private void printReceivedPackets(ArrayList<DatagramPacket> datagram_list) {
		System.out.println("-----Begin Received Data-----");
		DatagramPacket dp = datagram_list.get(0);
		System.out.println("From Address: " + dp.getAddress().toString());
		System.out.println("From Port: " + dp.getPort());
		for (int i = 0; i < datagram_list.size(); i++) {
			dp = datagram_list.get(i);
			String data = dp.getData().toString();
			System.out.print(data);
		}
		System.out.println("-----End Received Data-----");
		
	}

}
