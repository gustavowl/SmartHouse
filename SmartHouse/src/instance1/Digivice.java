package instance1;
import java.lang.reflect.Method;
import java.util.ArrayList;
import framework.*;

public class Digivice extends IOT_IOTDevice {
	
	private int form;
	private String[] evolutions;
	Method[] facadeMethods;
	String[] facadeParameters;
	
	public Digivice() {
		super("Digivice");
		
		form = 0;
		evolutions = new String[]{"Agumon", "Greymon", "MetalGreymon", "WarGreymon"};
		
		Class<? extends Digivice> c = this.getClass();
		facadeMethods = c.getDeclaredMethods();
		ArrayList<String> parameters = new ArrayList<String>();
		ArrayList<Method> methods = new ArrayList<Method>();
		for (Method method : c.getDeclaredMethods()) {
			String str = method.getName();
			if (method.toString().indexOf("private") != 0 && !str.equals("getFacadeMethods") &&
					!str.equals("executeFacadeMethod")  && !str.equals("getIotStatus") &&
					!str.equals("initReceiver") && !str.equals("initSender") &&
					!str.equals("getFacadeMethodParameters")) {
				
				methods.add(method);
				parameters.add(getFacadeMethodParameters(method));
			}
		}
		facadeMethods = new Method[methods.size()];
		facadeMethods = methods.toArray(facadeMethods);
		
		facadeParameters = new String[methods.size()];
		facadeParameters = parameters.toArray(facadeParameters);
	}
	
	public String getFacadeMethodParameters(Method method) {
		switch (method.getName()) {
			case "speak":
				String str = "(String speech";
				if (method.getParameterCount() == 2) {
					str += ",Integer times";
				}
				return str + ")";
			default:
				return "()";
		}
	}

	@Override
	public ArrayList<String> getFacadeMethods() {
		ArrayList<String> list = new ArrayList<String>();
		int i = 0;
		for (Method method : facadeMethods) {
			list.add(method.getName() + facadeParameters[i]);
			i++;
		}
		return list;
	}
	
	@Override
	public void executeFacadeMethod(String methodSignature, String[] args) {
		
		Method method = getMethod(methodSignature);
		
		if (method != null) {
			
			if (method.getParameterCount() == args.length) {
				
				//Class<?>[] paramTypes = method.getParameterTypes();				
				Object[] objArgs = new Object[args.length];
				
				for (int i = 0; i < objArgs.length; i++) {
					//does not work for primitive
					//objArgs[i] = paramTypes[i].cast( (Object)args[i] );
					objArgs[i] = args[i];
				}
				
				try {
					method.invoke(this, objArgs);
				}
				catch(Exception e) {
					System.out.println("Error when trying to execute lamp facade method.\n" +
							"\tSignature: " + methodSignature + "\n\tArgs: " + args.toString() +
							"\t" + e.getMessage());
				}
			}
		}
	}
	
	private Method getMethod(String methodSignature) {
		Method method;
		for (int i = 0; i < facadeMethods.length; i++) {
			method = facadeMethods[i];
			
			/*System.out.println(methodSignature + " == " + method.getName() +
					getFacadeMethodParameters(method) + 
					methodSignature.equals(method.getName() +
							getFacadeMethodParameters(method)));*/
			
			if (methodSignature.equals(method.getName() +
					getFacadeMethodParameters(method))) {
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

	@Override
	public Receiver initReceiver() {
		return new ReceiverSocket();
	}

	@Override
	public Sender initSender() {
		return new SenderSocket();
	}
	
	public void speak(String str) {
		System.out.println("\"" + evolutions[form] + "\" says: " + str);
	}
	
	public void speak(String str, String times) {
		int speak_times = Integer.parseInt(times); 
		for (int i = 0; i < speak_times; i++) {
			speak(str);
		}
	}
}
