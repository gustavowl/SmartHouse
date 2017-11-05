import java.lang.reflect.Method;
import java.util.ArrayList;

public class Lamp extends IOT_IOTDevice {
	
	private boolean isLampOn;
	Method[] facadeMethods;
	
	public Lamp() {
		this("Lamp");
	}
	
	public Lamp(String name) {
		super(name);
		isLampOn = false;
		
		Class<? extends Lamp> c = this.getClass();
		facadeMethods = c.getDeclaredMethods();
	}

	@Override
	public ArrayList<String> getFacadeMethods() {
		ArrayList<String> list = new ArrayList<String>();
		for (Method method : facadeMethods) {
			String str = method.getName();
			if (!str.equals("getFacadeMethods")) {
				list.add(method.getName());
				//System.out.println(method.getName());
			}
		}
		return list;
	}
	
	@Override
	public void executeFacadeMethod(String methodSignature, String[] args) {
		// TODO Auto-generated method stub
		Method method = getMethod(methodSignature);
		if (method != null) {
			try {
				method.invoke(this, (Object)args);
			}
			catch(Exception e) {
				System.out.println("Error when trying to execute lamp facade method.\n" +
						"\tSignature: " + methodSignature + "\n\tArgs: " + args.toString() +
						"\t" + e.getMessage());
			}
		}
	}
	
	private Method getMethod(String methodSignature) {
		for (Method method : facadeMethods) {
			if (methodSignature.equals(method.getName())) {
				return method;
			}
		}
		return null;
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
