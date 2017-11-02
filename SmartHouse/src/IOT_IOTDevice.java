import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

//IOTDevice representation as seen by the IOT itself
public class IOT_IOTDevice extends IOTDevice {
	ReceiverSocket receiver;
	SenderSocket sender;
	ProtocolFacade protocol;
	
	public IOT_IOTDevice() {
		this(12114, "", null, 0);
	}
	
	public IOT_IOTDevice(String name) {
		this(12114, name, null, 0);
	}
	
	public IOT_IOTDevice(int port, String name,
			InetAddress peerAddress, int peerPort) {
		super(null, port, name);
		receiver = new ReceiverSocket(port);
		sender = new SenderSocket("0.0.0.0", 12115);
		setPeerAddress(peerAddress);
		setPeerPort(peerPort);
		protocol = new ProtocolFacade();
	}
	
	public void update() {
		System.out.println("TODO: Implement update method");
	}
	
	public void remove() {
		System.out.println("TODO: Implement update method");
	}
	
	public void discover() {

	}
	
	public void listenToPeer() {
		System.out.println("TODO: wait for peer action requests");
	}
	
	public void run() {
		while (peerAddress == null) {
			InetSocketAddress isa = protocol.ServerDiscoveryStart(
					receiver, sender);
			setPeerAddress(isa.getAddress());
			setPeerPort(isa.getPort());
		}
		while (peerAddress != null) {
			listenToPeer();
		}
	}
}
