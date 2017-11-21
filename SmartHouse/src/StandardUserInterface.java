import java.util.Scanner;

public class StandardUserInterface extends UserInterface {

	private Scanner scan;
	
	public StandardUserInterface() {
		super();
		scan = new Scanner(System.in);
	}
	
	public StandardUserInterface(App app) {
		super(app);
		scan = new Scanner(System.in);
	}
	
	@Override
	protected void showInitialOptions() {
		System.out.println("------------------------------------");
		System.out.println("This is the Smart House app!");
		System.out.println("Please, choose the desisred action:");
		System.out.println("1 - Discover new IoTs");
		System.out.println("2 - Select IoT");
		System.out.println("3 - Quit app");
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
				app.selectIOT();
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
	protected void showErrorMessage(String message) {
		System.out.println(message);
	}

	@Override
	protected int executeDiscoveryOption(int option) {
		return option - 1;
	}
}
