package framework;

//IOTDevice representation as seen by the App
public class AppIOTDevice extends IOTDevice {
	
	public AppIOTDevice() {
		this("");
		//creates local representation of IOT (local address)
	}
	
	public AppIOTDevice(String name) {
		this(null, name);
		//creates local representation of IOT (local address)
	}
	
	public AppIOTDevice(String address, String name) {
		super(address, name);
		
	}
	
	public void update() {
		System.out.println("TODO: Implement update method");
		System.out.println("\tSend request to IOT to check for updates");
	}
	
	public void remove() {
		System.out.println("TODO: Implement update method");
		System.out.println("\tSend request to IOT to forget app and run Discover()");
	}
	
	public void discover() {
		System.out.println("TODO: Implement discover method");
	}
}
