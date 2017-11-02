import java.net.InetSocketAddress;
import java.util.ArrayList;

public class ProtocolFacade {
	
	Protocol protocol;
	
	public ProtocolFacade() {
		protocol = new Protocol();
	}
	
	public void IOTDiscoveryStart(ReceiverSocket receiver, SenderSocket sender) {
		protocol.discoverIot(true, false, receiver, sender);
	}
	
	public void IOTDiscoveryStop() {
		protocol.discoverIot(false, true, null, null);
	}
	
	public InetSocketAddress ServerDiscoveryStart(ReceiverSocket receiver,
			SenderSocket sender) {
		return protocol.discoverServer(receiver, sender);
	}
	
	public ArrayList<IOTDevice> getIotsDiscovered() {
		return protocol.getAndClearIotsFound();
	}
	
	public void confirmConnection(IOTDevice iot, SenderSocket sender) {
		protocol.confirmDiscoveredIotConnection(iot, sender);
	}
	
	public static int getStandardServerReceiverPort() { 
		return Protocol.SERVER_RECEIVER_PORT;
	}
	
	public static int getStandardServerSenderPort() {
		return Protocol.SERVER_SENDER_PORT;
	}
	
	public static int getStandardIotReceiverPort() { 
		return Protocol.IOT_RECEIVER_PORT;
	}
	
	public static int getStandardIotSenderPort() {
		return Protocol.IOT_SENDER_PORT;
	}

}
