import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.annotation.PostConstruct;

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
		ArrayList<Method> methods = new ArrayList<Method>();
		for (Method method : c.getDeclaredMethods()) {
			String str = method.getName();
			if (method.toString().indexOf("private") != 0 && !str.equals("getFacadeMethods") &&
					!str.equals("executeFacadeMethod")) {
				
				methods.add(method);
			}
		}
		facadeMethods = new Method[methods.size()];
		facadeMethods = methods.toArray(facadeMethods);
	}

	@Override
	public ArrayList<String> getFacadeMethods() {
		ArrayList<String> list = new ArrayList<String>();
		for (Method method : facadeMethods) {
			list.add(method.getName());
		}
		return list;
	}
	
	@Override
	public void executeFacadeMethod(String methodSignature, String[] args) {
		// TODO Auto-generated method stub
		Method method = getMethod(methodSignature);
		if (method != null) {
			try {
				method.invoke(this);
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
		System.out.println("Lamp is on");
	}
	
	public void turnOff() {
		isLampOn = false;
		System.out.println("Lamp is off");
	}
	
	public boolean getLampState() {
		return isLampOn;
	}
}
