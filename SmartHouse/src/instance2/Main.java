package instance2;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		System.out.println("Hello, SmartHouse!");
		/* This is the central manager.
		 * Two broadcast sockets will be necessary.
		 * One for sending packages to all devices to be discovered
		 * one for receiving packages from these devices
		 */
		System.out.println("Choose your option:\n1 - App/Server\n2 - Lamp Manager");
		Scanner scan = new Scanner(System.in);
		int opt = scan.nextInt();
		switch (opt) {
			case 1:
				StandardUserInterface sui = new StandardUserInterface();
				sui.run();
				break;
			case 2:
				LampManager lm = new LampManager(1);
				lm.run();
				break;
			default: 
				System.out.println("Invalid input");
				break;
		}
		scan.close();
		
	}

}
