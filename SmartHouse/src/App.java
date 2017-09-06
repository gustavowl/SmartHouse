import java.util.ArrayList;
import java.util.Scanner;

public class App {
	ArrayList<AppIOTDevice> connectedIots;
	
	public App() {
		connectedIots = new ArrayList<AppIOTDevice>();
	}
	
	//TODO: extends runnable?
	public void run() {
		boolean finish = false;
		Scanner scan = new Scanner(System.in);
		int opt;
		while (!finish) {
			System.out.println("-------------------------");
			System.out.println("This is the Smart House app!");
			System.out.println("Please, choose the desisred action:");
			System.out.println("1 - Discover new IoTs");
			System.out.println("2 - Select IoT");
			System.out.println("3 - Quit app");
			
			opt = scan.nextInt();
			
			switch(opt) {
				case 1:
					System.out.println("TODO: implement discover method and update protocol");
					break;
				case 2:
					selectIOT();
					break;
				case 3:
					System.out.println("Goodbye! We hope to see you soon enough.");
					finish = true;
					break;
				default: 
					System.out.println("That is an invalid option!");
					break;
			}
		}
		scan.close();
	}
	
	private void selectIOT() {
		System.out.println("Select an IOT from the following list:\n0 - Quit");
		int i = 1;
		for (AppIOTDevice iot : connectedIots) {
			System.out.println(Integer.toString(i) + " - " + iot.getName());
		}
		System.out.println("TODO: IMPLEMENT THIS");
	}
}
