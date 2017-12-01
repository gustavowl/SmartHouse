
public abstract class Sender {
	
	private static String SERVER_SENDER_PORT;
	private static String IOT_SENDER_PORT;
	private String address;
	
	Sender() {
	}
	
	public abstract void close();
	
	public abstract void open(String port, boolean isBroadcast);
	
	public abstract void sendData(String content);
	
	public abstract void sendData(String content, String port);

	public abstract void sendData(String content, String address, String port);
	
	public abstract boolean isClosed();
	
	protected static void setServerPort(String serverPort) {
		SERVER_SENDER_PORT = serverPort;
	}
	
	public static String getServerPort() {
		return SERVER_SENDER_PORT;
	}
	
	protected static void setIotPort(String iotPort) {
		IOT_SENDER_PORT = iotPort;
	}
	
	public static String getIotPort() {
		return IOT_SENDER_PORT;
	}
	
	protected void setAddress(String address) {
		this.address = address;
	}
	
	public String getAddress() {
		return address;
	}
}
