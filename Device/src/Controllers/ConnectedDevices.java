package Controllers;

import java.util.ArrayList;
import java.util.Arrays;

import Entity.Device;

public class ConnectedDevices {

	private ArrayList<Device> devices = new ArrayList<Device>();
	
	public static ArrayList<String> DEVICESSUPPORTED = new ArrayList<>();
	
	//Esse tem construtor
	public ConnectedDevices() {
		DEVICESSUPPORTED.addAll(Arrays.asList("Camera","Lampada", "Tag", "Thermostat", "Tomada"));
	}

	public ArrayList<Device> getDevices() {
		return devices;
	}

	//is this needed (?)
	public void setDevices(ArrayList<Device> devices) {
		this.devices = devices;
	}
	
	//Boa Claudio
	public void addDevice(Device d) {
		devices.add(d);
	}
	
	//is this how it works [?]
	public void rmvDevice(Device d) {
		devices.remove(d);
	}
	
}
