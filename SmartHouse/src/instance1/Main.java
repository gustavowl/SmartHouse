package instance1;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		System.out.println("Hello, SmartHouse!");
		/* This is the central manager.
		 * Two broadcast sockets will be necessary.
		 * One for sending packages to all devices to be discovered
		 * one for receiving packages from these devices
		 */
		System.out.println("Choose your option:\n1 - App/Server\n2 - Lamp" +
				"\n3 - Digivice");
		Scanner scan = new Scanner(System.in);
		int opt = scan.nextInt();
		switch (opt) {
			case 1:
				StandardUserInterface sui = new StandardUserInterface();
				sui.run();
				break;
			case 2:
				//IOT_IOTDevice iotDevice = new IOT_IOTDevice("Digivice");
				Lamp lamp = new Lamp();
				/*for (String method : lamp.getFacadeMethods()) {
					System.out.println(method);
					lamp.executeFacadeMethod(method, new String[] {});
				}*/
				System.out.println("Starts running IoT \"" + lamp.getName() + "\"");
				lamp.run();
				break;
			case 3:
				Digivice digivice = new Digivice();
				System.out.println("Starts running IoT \"" + digivice.getName() + "\"");
				digivice.run();
			default: 
				System.out.println("Invalid input");
				break;
		}
		scan.close();
		
	}

}
