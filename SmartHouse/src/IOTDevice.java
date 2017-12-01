
abstract public class IOTDevice {
	protected String address;
	//protected String port;
	//protected int listenerPort;
	protected String peerAddress;
	//protected String peerPort;
	//protected int peerPort;
	//protected ArrayList<Method> methods;
	protected String name;
	
	protected IOTDevice() {
		this(null, "");
	}
	
	protected IOTDevice(String name) {
		this(null, name);
	}
	
	protected IOTDevice(String address, String name) {
		this.address = address;
		/*
		if (address != null) {
			this.address = address;
		}
		else {
			InetSocketAddress isa = new InetSocketAddress("0.0.0.0", 0);
			this.address = isa.getAddress();
		}*/
		//this.listenerPort = listenerPort;
		this.name = name;
		this.peerAddress = null;
		//this.peerPort = 0;
		/*methods = new ArrayList<Method>();
		methods.add(new Method("update"));
		methods.add(new Method("remove"));*/
	}
	
	public void setPeerAddress(String peerAddress) {
		this.peerAddress = peerAddress;
	}
	
	/*public void setPeerPort(int peerPort) {
		//TODO: verify if range is valid
		this.peerPort = peerPort;
	}*/
	
	public String getAddress() {
		return address;
	}
	
	/*public int getListenerPort() {
		return listenerPort;
	}*/
	
	public String getPeerAddress() {
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
