import java.net.InetAddress;
import java.util.ArrayList;

abstract public class IOTDevice {
	protected InetAddress peerAddress;
	protected int peerPort;
	protected ArrayList<Method> methods;
	
	protected IOTDevice() {
		this(null, 0);
	}
	
	protected IOTDevice(InetAddress peerAddress, int peerPort) {
		this.peerAddress = peerAddress;
		this.peerPort = peerPort;
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
	
	public abstract void update();
	
	public abstract void remove();
	
	public abstract void discover();
	
}
