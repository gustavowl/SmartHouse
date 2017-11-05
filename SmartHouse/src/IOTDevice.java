import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;

abstract public class IOTDevice {
	protected InetAddress address;
	//protected int listenerPort;
	protected InetAddress peerAddress;
	//protected int peerPort;
	//protected ArrayList<Method> methods;
	protected String name;
	
	protected IOTDevice() {
		this(null, 0, "");
	}
	
	protected IOTDevice(String name) {
		this(null, 0, name);
	}
	
	protected IOTDevice(InetAddress address, int listenerPort, String name) {
		if (address != null) {
			this.address = address;
		}
		else {
			InetSocketAddress isa = new InetSocketAddress("0.0.0.0", 0);
			this.address = isa.getAddress();
		}
		//this.listenerPort = listenerPort;
		this.name = name;
		this.peerAddress = null;
		//this.peerPort = 0;
		/*methods = new ArrayList<Method>();
		methods.add(new Method("update"));
		methods.add(new Method("remove"));*/
	}
	
	public void setPeerAddress(InetAddress peerAddress) {
		this.peerAddress = peerAddress;
	}
	
	/*public void setPeerPort(int peerPort) {
		//TODO: verify if range is valid
		this.peerPort = peerPort;
	}*/
	
	public InetAddress getAddress() {
		return address;
	}
	
	/*public int getListenerPort() {
		return listenerPort;
	}*/
	
	public InetAddress getPeerAddress() {
		return peerAddress;
	}
	
	/*public int getPeerPort() {
		return peerPort;
	}*/
	
	/*public ArrayList<Method> getMethods() {
		return methods;
	}*/
	
	public String getName() {
		return name;
	}
	
	public boolean isPeerAddressValid() {
		return peerAddress != null;
	}
	
	public abstract void update();
	
	public abstract void remove();
	
	public abstract void discover();
	
}
