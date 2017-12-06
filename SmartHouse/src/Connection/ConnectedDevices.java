package Connection;

import java.util.ArrayList;
import java.util.Arrays;

import device.Device;

public class ConnectedDevices {

	private ArrayList<Device> devices = new ArrayList<Device>();
	
	public static ArrayList<String> DEVICESSUPPORTED = new ArrayList<>();
	
	public ConnectedDevices() {
		DEVICESSUPPORTED.addAll(Arrays.asList("Camera","Lampada", "Tag", "Thermostat", "Tomada"));
	}

	public ArrayList<Device> getDevices() {
		return devices;
	}

	public void setDevices(ArrayList<Device> devices) {
		this.devices = devices;
	}
	
	public void addDevice(Device d) {
		devices.add(d);
	}
	
	public void rmvDevice(Device d) {
		devices.remove(d);
	}
	
}
