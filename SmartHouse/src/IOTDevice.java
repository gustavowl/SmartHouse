import java.net.InetAddress;
import java.util.ArrayList;

abstract public class IOTDevice {
	protected InetAddress peerAddress;
	protected int peerPort;
	protected ArrayList<Method> methods;
	protected String name;
	
	protected IOTDevice() {
		this(null, 0, "");
	}
	
	protected IOTDevice(String name) {
		this(null, 0, name);
	}
	
	protected IOTDevice(InetAddress peerAddress, int peerPort, String name) {
		this.peerAddress = peerAddress;
		this.peerPort = peerPort;
		this.name = name;
		methods = new ArrayList<Method>();
		methods.add(new Method("update"));
		methods.add(new Method("remove"));
	}
	
	public InetAddress getPeerAddress() {
		return peerAddress;
	}
	
	public int getPeerPort() {
		return peerPort;
	}
	
	public ArrayList<Method> getMethods() {
		return methods;
	}
	
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
