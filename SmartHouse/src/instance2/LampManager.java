package instance2;

import framework.IOT_IOTDevice;
import framework.Receiver;
import framework.Sender;
import instance2.ReceiverSocket;
import instance2.SenderSocket;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Label;
import java.lang.reflect.Method;
import java.util.ArrayList;
import javax.swing.JFrame;

public class LampManager extends IOT_IOTDevice {
	
	Lamp[] lamps;
	JFrame[] frames;
	Method[] facadeMethods;
	String[] facadeParameters;
	final int stdStroboscopeTime;
	
	public LampManager() {
		this(4);
	}
	
	public LampManager(int numLamps) {
		super("Lamp Manager");
		lamps = new Lamp[numLamps];
		frames = new JFrame[numLamps];
		stdStroboscopeTime = 100;
		for (int i = 0; i < numLamps; i++) {
			lamps[i] = new Lamp();
			lamps[i].start();
			lamps[i].setRed(255);
			lamps[i].setGreen(255);
			lamps[i].setBlue(255);
			lamps[i].setOn(false);
			lamps[i].setStrob_time_ms(0);
			
			frames[i] = new JFrame("Lamp " + i);
			frames[i].setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frames[i].getContentPane().add(new Label(), BorderLayout.CENTER);
			frames[i].setSize(200, 200);
			frames[i].setLocation(frames[i].getWidth() * i, 0);
			frames[i].getContentPane().setBackground(Color.BLACK);
		}
		
		Class<? extends LampManager> c = this.getClass();
		facadeMethods = c.getDeclaredMethods();
		ArrayList<String> parameters = new ArrayList<String>();
		ArrayList<Method> methods = new ArrayList<Method>();
		for (Method method : c.getDeclaredMethods()) {
			String str = method.getName();
			if (method.toString().indexOf("private") != 0 && !str.equals("getFacadeMethods") &&
					!str.equals("executeFacadeMethod")  && !str.equals("getIotStatus") &&
					!str.equals("initReceiver") && !str.equals("initSender") &&
					!str.equals("getFacadeMethodParameters") && !str.equals("getLampStatus")) {
				
				methods.add(method);
				parameters.add(getFacadeMethodParameters(method));
			}
		}
		facadeMethods = new Method[methods.size()];
		facadeMethods = methods.toArray(facadeMethods);
		
		facadeParameters = new String[methods.size()];
		facadeParameters = parameters.toArray(facadeParameters);
		
		showFrames();
	}
	
	class ThreadFrame extends Thread {
		
		int index;
		
		public ThreadFrame(int index) {
			this.index = index;
		}
		
		@Override
		public void run() {
			while (true) {				
				frames[index].getContentPane().removeAll();
				frames[index].getContentPane().add(new Label(), BorderLayout.CENTER);
				synchronized (lamps[index]) {
					frames[index].getContentPane().setBackground(
							new Color(
									lamps[index].getShownRed(),
									lamps[index].getShownGreen(),
									lamps[index].getShownBlue()
							));
				}
			}
		}
	}
	
	private void showFrames() {
		for (int i = 0; i < frames.length; i++) {
			frames[i].setVisible(true);
			ThreadFrame tf = new ThreadFrame(i);
			tf.start();
		}
	}

	@Override
	protected Receiver initReceiver() {
		return new ReceiverSocket();
	}

	@Override
	protected Sender initSender() {
		return new SenderSocket();
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
					objArgs[i] = Integer.parseInt(args[i]);
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
	
	public String getLampStatus(int index) {
		if (index >= 0 && index < lamps.length) {
			String status = "Lamp " + index + ":\n";
			status += "\tLamp is turned ";
			if (lamps[index].isOn()) {
				status += "ON\n";
			}
			else {
				status += "OFF\n";
			}
			status += "\tMode: ";
			if (lamps[index].isStroboscope()) {
				status += "Stroboscope (" + lamps[index].getStrob_time_ms() +
						" milliseconds interval)\n";
			}
			else {
				status += "Standard\n";
			}
			status += "\tColor (RGB): " + lamps[index].getRed() + ", " +
					lamps[index].getGreen() + ", " + lamps[index].getBlue();
			return status;
		}
		return "";
	}

	@Override
	public String getIotStatus() {
		String status = "";
		for (int i = 0; i < lamps.length - 1; i++) {
			status += getLampStatus(i) + "\n----------\n";
		}
		status += getLampStatus(lamps.length - 1);
		return status;
	}
	
	public String getFacadeMethodParameters(Method method) {
		switch (method.getName()) {
			case "turnLampOn":
				return "(Integer Lamp number)";
			case "turnLampOff":
				return "(Integer Lamp number)";
			case "swiftLampMode":
				return "(Integer Lamp number)";
			case "changeLampColor":
				return "(Integer Lamp number,Integer red [0 to 255]," + 
					"Integer green [0 to 255], Integer blue [0 to 255])";
			case "changeAllLampsColor":
				return "(Integer red [0 to 255]," + 
				"Integer green [0 to 255], Integer blue [0 to 255])";
		}
		return "()";
	}
	
	private boolean isIndexValid(int index) {
		return index >= 0 && index < lamps.length;
	}
	
	public void turnLampOn(Integer lampIndex) {
		if (isIndexValid(lampIndex)) {
			lamps[lampIndex].setOn(true);
		}
	}
	
	public void turnLampOff(Integer lampIndex) {
		if (isIndexValid(lampIndex)) {
			lamps[lampIndex].setOn(false);
		}
	}
	
	public void turnAllLampsOn() {
		for (int i = 0; i < lamps.length; i++) {
			turnLampOn(i);
		}
	}
	
	public void turnAllLampsOff() {
		for (int i = 0; i < lamps.length; i++) {
			turnLampOff(i);
		}
	}
	
	public void swiftLampMode(Integer lampIndex) {
		if (isIndexValid(lampIndex)) {
			lamps[lampIndex].setStroboscope(
					!lamps[lampIndex].isStroboscope());
		}
	}
	
	public void swiftAllLampsMode() {
		for (int i = 0; i < lamps.length; i++) {
			swiftLampMode(i);
		}
	}
	
	public void changeLampColor(Integer lampIndex, Integer red, Integer blue, Integer green) {
		if (isIndexValid(lampIndex)) {
			lamps[lampIndex].setRed(red);
			lamps[lampIndex].setGreen(blue);
			lamps[lampIndex].setBlue(green);
		}
	}
	
	public void changeAllLampsColor(Integer red, Integer blue, Integer green) {
		for (int i = 0; i < lamps.length; i++) {
			changeLampColor(i, red, blue, green);
		}
	}
	
}
