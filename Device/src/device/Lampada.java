package device;

import java.awt.EventQueue;

import entities.Field;
import interfaceUI.AddDevice;

public class Lampada extends Device {
	private int wattage;
	
	public Lampada() {
		super();
		setClassFields();
	}
	
	public Integer getWattage() {
		return wattage;
	}

	public void setWattage(Integer wattage) {
		this.wattage = wattage;
	}

	@Override
	public void setClassFields() {
		Field fieldTemp = new Field("wattage", "int", "2");
		getFields().add(fieldTemp);
		
	}

}
