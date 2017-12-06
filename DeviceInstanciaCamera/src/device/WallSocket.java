package device;

import entities.Field;

public class WallSocket extends Device {

	private int wattage;
	
	public WallSocket() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Integer getWattage() {
		return wattage;
	}

	public void setWattage(Integer wattage) {
		this.wattage = wattage;
	}

	@Override
	public void setClassFields() {
		Field fieldTemp = new Field("wattage", "int", "" + getWattage());
		getFields().add(fieldTemp);
		
	}

}
