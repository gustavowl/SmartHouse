import java.util.ArrayList;

public abstract class Receiver {
	
	private static String SERVER_RECEIVER_PORT;
	private static String IOT_RECEIVER_PORT;
	private String address;
	private String peerAddress;
	private String port;
	
	Receiver() {
	}
	
	public abstract void close();
	
	public abstract void open(String port, boolean isBroadcast);
	
	public abstract boolean isClosed();
	
	public abstract ArrayList<String> receiveData(int packetsMax);
	
	public abstract String receiveData(String code);
	
	public abstract ArrayList<String> receiveData(int packetsMax, String code);
	
	public abstract String receiveData(String code, int timeout);
	
	public abstract String receiveData(final String[] codes, int timeout);
	
	public abstract ArrayList<String> receiveData(int packetsMax, int timeout);
	
	protected static void setServerPort(String serverPort) {
		SERVER_RECEIVER_PORT = serverPort;
	}
	
	public static String getServerPort() {
		return SERVER_RECEIVER_PORT;
	}
	
	protected static void setIotPort(String iotPort) {
		IOT_RECEIVER_PORT = iotPort;
	}
	
	public static String getIotPort() {
		return IOT_RECEIVER_PORT;
	}
	
	protected void setAddress(String address) {
		this.address = address;
	}
	
	public String getAddress() {
		return address;
	}
	
	protected void setPeerAddress(String address) {
		this.peerAddress = address;
	}
	
	public String getPeerAddress() {
		return peerAddress;
	}
	
	protected void setPort(String port) {
		this.port = port;
	}
	
	public String getPort() {
		return port;
	}

}
