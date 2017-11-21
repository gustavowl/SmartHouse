import java.lang.reflect.Method;
import java.util.ArrayList;

public class Digivice extends IOT_IOTDevice {
	
	private int form;
	private String[] evolutions;
	Method[] facadeMethods;
	
	public Digivice() {
		super("Digivice");
		
		form = 0;
		evolutions = new String[]{"Agumon", "Greymon", "MetalGreymon", "WarGreymon"};
		
		Class<? extends Digivice> c = this.getClass();
		facadeMethods = c.getDeclaredMethods();
		ArrayList<Method> methods = new ArrayList<Method>();
		for (Method method : c.getDeclaredMethods()) {
			String str = method.getName();
			if (method.toString().indexOf("private") != 0 && !str.equals("getFacadeMethods") &&
					!str.equals("executeFacadeMethod")  && !str.equals("getIotStatus")) {
				
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
	
	public void showActualForm() {
		System.out.println("My actual form is: " + evolutions[form]);
	}
	
	public void digivolve() {
		if (form < evolutions.length - 1) {
			System.out.println(evolutions[form] + " digivolves to " + evolutions[++form]);
		}
		else {
			System.out.println("I'm already at my strongest form!");
		}
	}
	
	public void unDigivolve() {
		if (form > 0) {
			System.out.println(evolutions[form--] + " undigivolves back to " + evolutions[form]);
		}
		else {
			System.out.println("I'm already at my weakest form!");
		}
	}

	@Override
	public String getIotStatus() {
		return "My current form is " + evolutions[form];
	}
}
