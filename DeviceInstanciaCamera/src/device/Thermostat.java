package device;

public class Thermostat extends Device  {

	private int temperature;

	
	public Thermostat() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getTemperature() {
		return temperature;
	}

	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}

	@Override
	public void setClassFields() {
		// TODO Auto-generated method stub
		
	}
	
}
