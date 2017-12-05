package instance1;
import java.util.Scanner;
import framework.*;

public class StandardUserInterface extends UserInterface {

	private Scanner scan;
	
	public StandardUserInterface() {
		this(new IotManager(new ReceiverSocket(), new SenderSocket()));
	}
	
	public StandardUserInterface(IotManager iotManager) {
		super(iotManager);
		scan = new Scanner(System.in);
	}
	
	@Override
	protected void showInitialOptions() {
		System.out.println("------------------------------------");
		System.out.println("This is the Smart House IoT Manager!");
		System.out.println("Please, choose the desired action:");
		System.out.println("1 - Discover new IoTs");
		System.out.println("2 - Select IoT");
		System.out.println("3 - Quit Smart House IoT Manager");
		validOptionMin = 1;
		validOptionMax = 3;
	}

	@Override
	protected boolean executeInitialOption(int option) {
		switch(option) {
			case 1:
				executeDiscovery();
				return false;
			case 2:
				listSelectedIots();
				return false;
			case 3:
				System.out.println("Goodbye! We hope to see you soon enough.");
				scan.close();
				return true;
			default: 
				return false;
		}
	}

	@Override
	protected void showDiscoveryInitialOptions() {
		System.out.println("0 - Quit");
		validOptionMin = 0;
		validOptionMax = 0;
	}

	@Override
	protected void showNewDiscoveryOption(String name) {
		System.out.println(++validOptionMax + " - " + name);
	}

	@Override
	protected int readOption() {
		return scan.nextInt();
	}
	
	@Override
	protected void showMessage(String message) {
		System.out.println(message);
	}

	@Override
	protected void showErrorMessage(String errorMessage) {
		System.out.println("Error: " + errorMessage);
	}

	@Override
	protected int executeDiscoveryOption(int option) {
		return option - 1;
	}

	@Override
	protected void showListSelectedIotsOptions(String[] connectedIots) {
		System.out.println("Select an IOT from the following list:\n0 - Quit");
		
		validOptionMin = 0;
		validOptionMax = 0;
		
		for (String iot : connectedIots) {
			System.out.println(Integer.toString(++validOptionMax) + " - " + iot);
		}
	}

	@Override
	protected int executeListSelectedIotsOption(int option) {
		return option - 1;
	}

	@Override
	protected void showListIotStandardFunctionalities(String[] functionalities) {
		System.out.println("0 - Quit");
		
		validOptionMin = 0;
		validOptionMax = 0;
		
		for (; validOptionMax < functionalities.length; ) {
			System.out.println(++validOptionMax + " - " + functionalities[validOptionMax - 1]);
		}
	}

	@Override
	protected int executeListIotStandardFunctionalities(int option) {
		return option - 1;
	}

	@Override
	public void showIotSpecificFunctionalities(String[] functionalities) {
		System.out.println("0 - Quit");
		
		validOptionMin = 0;
		validOptionMax = 0;
		
		for (; validOptionMax < functionalities.length; ) {
			System.out.println(++validOptionMax + " - " + functionalities[validOptionMax - 1]);
		}
		
	}

	@Override
	protected int executeListIotSpecificFunctionality(int option) {
		return option - 1;
	}

	@Override
	protected String[] readArgs(String[] argsDescription) {
		String[] args = new String[argsDescription.length];
		scan.nextLine();
		for (int i = 0; i < argsDescription.length; i++) {
			System.out.println("Please, enter \"" + argsDescription[i] + 
					"\" value: ");
			args[i] = scan.nextLine();
		}
		showMessage("Finished reading parameters");
		return args;
	}
}
