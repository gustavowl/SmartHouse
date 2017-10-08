package Entity;

public class Lampada extends Device {
	private int wattage;
	
	public Integer getWattage() { //Why Integer instead of int [?]
		return wattage;
	}

	public void setWattage(Integer wattage) { //same
		this.wattage = wattage;
	}

}
