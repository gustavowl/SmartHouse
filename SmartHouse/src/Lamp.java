import java.lang.reflect.Method;
import java.util.ArrayList;

public class Lamp extends IOT_IOTDevice {
	
	private boolean isLampOn;
	ArrayList<String> facadeMethods;
	
	public Lamp() {
		this("Lamp");
	}
	
	public Lamp(String name) {
		super(name);
		isLampOn = false;
		facadeMethods = generateFacadeMethods();
	}
	
	private ArrayList<String> generateFacadeMethods() {
		Class<? extends Lamp> c = this.getClass();
		Method[] methods = c.getDeclaredMethods();
		ArrayList<String> list = new ArrayList<String>();
		for (Method method : methods) {
			String str = method.getName();
			if (!str.equals("getFacadeMethods")) {
				list.add(method.getName());
				//System.out.println(method.getName());
			}
		}
		return list;
	}

	@Override
	public ArrayList<String> getFacadeMethods() {
		return facadeMethods;
	}
	
	public void turnOn() {
		isLampOn = true;
	}
	
	public void turnOff() {
		isLampOn = false;
	}
	
	public boolean getLampState() {
		return isLampOn;
	}
}
