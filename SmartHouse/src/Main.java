import java.util.Scanner; 

public class Main {

	public static void main(String[] args) {
		System.out.println("Hello, SmartHouse!");
		/* This is the central manager.
		 * Two broadcast sockets will be necessary.
		 * One for sending packages to all devices to be discovered
		 * one for receiving packages from these devices
		 */
		System.out.println("Choose your option:\n1 - Sender\n2 - Receiver");
		Scanner scan = new Scanner(System.in);
		int opt = scan.nextInt();
		scan.close();
		switch (opt) {
			case 1: System.out.println("Sender"); break;
			case 2: System.out.println("Receiver"); break;
			default: System.out.println("Invalid input"); break;
		}
		
	}

}
