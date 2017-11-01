import java.net.DatagramPacket;
import java.util.ArrayList;


public class ProtocolFacade {
	
	Protocol protocol;
	
	public ProtocolFacade() {
		protocol = new Protocol();
	}
	
	public void IOTDiscoveryStart() {
		protocol.discover(true, false);
	}
	
	public void IOTDiscoveryStop() {
		protocol.discover(false, true);
	}
	
	public ArrayList<IOTDevice> getIotsDiscovered() {
		return protocol.getAndClearIotsFound();
	}

}
