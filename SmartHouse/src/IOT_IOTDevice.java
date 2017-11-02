import java.net.InetAddress;
import java.net.InetSocketAddress;

//IOTDevice representation as seen by the IOT itself
public class IOT_IOTDevice extends IOTDevice {
	ReceiverSocket receiver;
	SenderSocket sender;
	ProtocolFacade protocol;
	
	public IOT_IOTDevice() {
		this(ProtocolFacade.getStandardIotReceiverPort(), "", null, 0);
	}
	
	public IOT_IOTDevice(String name) {
		this(ProtocolFacade.getStandardIotReceiverPort(), name, null, 0);
	}
	
	public IOT_IOTDevice(int port, String name, InetAddress peerAddress, int peerPort) {
		super(null, port, name);
		receiver = new ReceiverSocket(port);
		sender = new SenderSocket("0.0.0.0", ProtocolFacade.getStandardIotSenderPort());
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
		while (true) {
			while (true) {
				InetSocketAddress isa = protocol.ServerDiscoveryStart(receiver,
						sender, getName());
				if (isa != null) {
					setPeerAddress(isa.getAddress());
					setPeerPort(isa.getPort());
					break;
				}
				else {
					System.out.println("IoT device was not recognized by Server. Timeout.");
					continue;
				}
			}
			while (peerAddress != null) {
				listenToPeer();
			}	
		}
	}
}
