import java.net.InetAddress;
import java.net.InetSocketAddress;

//IOTDevice representation as seen by the IOT itself
public class IOT_IOTDevice extends IOTDevice {
	ReceiverSocket receiver;
	SenderSocket sender;
	ProtocolFacade protocol;
	
	public IOT_IOTDevice() {
		this(ProtocolFacade.getStandardIotReceiverPort(), "", null);
	}
	
	public IOT_IOTDevice(String name) {
		this(ProtocolFacade.getStandardIotReceiverPort(), name, null);
	}
	
	public IOT_IOTDevice(int port, String name, InetAddress peerAddress) {
		super(null, port, name);
		receiver = new ReceiverSocket();
		//listenerPort = port;
		sender = new SenderSocket();
		setPeerAddress(peerAddress);
		//setPeerPort(peerPort);
		protocol = new ProtocolFacade();
	}
	
	public void update() {
		System.out.println("TODO: Implement update method");
	}
	
	public void remove() {
		System.out.println("TODO: Implement update method");
	}
	
	public void discover() {
		while (true) {
			InetSocketAddress isa = protocol.ServerDiscoveryStart(receiver,
					sender, getName());
			if (isa != null) {
				setPeerAddress(isa.getAddress());
				break;
			}
			else {
				//TODO: change to GUI
				System.out.println("IoT device was not recognized by Server. Timeout.");
				continue;
			}
		}
	}
	
	public void listenToPeer() {
		/* Commands available:
		 * 1 - Remove connection with server
		 * 2 - Execute Method
		 * 3 - Get List of Methods
		 * 4 - Update (?)
		 * 5 - Check for updates (?)
		 * 6 - List consumption (?)
		 */
		while (peerAddress != null) {
			String code = protocol.listenToServerRequests(getPeerAddress(), receiver);
			protocol.answerServerRequest(code, getPeerAddress(), sender);
			if (ProtocolFacade.isDisconnectRequest(code)) {
				peerAddress = null;
				//TODO: change to GUI
				System.out.println("Disconnected by the server");
			}
		}
	}
	
	public void run() {
		while (true) {
			System.out.println("Wait for server Discovery request");
			discover();

			System.out.println("Listening for server requests");
			listenToPeer();
		}
	}
}
