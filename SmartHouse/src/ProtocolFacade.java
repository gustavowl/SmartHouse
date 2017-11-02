import java.net.InetSocketAddress;
import java.util.ArrayList;

public class ProtocolFacade {
	
	Protocol protocol;
	
	public ProtocolFacade() {
		protocol = new Protocol();
	}
	
	public void IOTDiscoveryStart() {
		protocol.discoverIot(true, false);
	}
	
	public void IOTDiscoveryStop() {
		protocol.discoverIot(false, true);
	}
	
	public InetSocketAddress ServerDiscoveryStart(ReceiverSocket receiver,
			SenderSocket sender) {
		return protocol.discoverServer(receiver, sender);
	}
	
	public ArrayList<IOTDevice> getIotsDiscovered() {
		return protocol.getAndClearIotsFound();
	}
	
	public void confirmConnection(IOTDevice iot) {
		protocol.confirmDiscoveredIotConnection(iot);
	}

}
