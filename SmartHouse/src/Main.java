import java.util.Scanner; 
import java.util.ArrayList;

public class Main {

	public static void main(String[] args) {
		System.out.println("Hello, SmartHouse!");
		/* This is the central manager.
		 * Two broadcast sockets will be necessary.
		 * One for sending packages to all devices to be discovered
		 * one for receiving packages from these devices
		 */
		System.out.println("Choose your option:\n1 - App/Server\n2 - IoT Device");
		Scanner scan = new Scanner(System.in);
		int opt = scan.nextInt();
		scan.close();
		switch (opt) {
			case 1:
				System.out.println("You chose App/Server"); //TODO: class
				
				/* Protocol outline:
				 * 1 - Sends message in broadcast in order to discover new devices
				 * 2 - Receives messages from multiple devices
				 * 3 - Sends message confirming or denying connection to devices
				 * 4 - Add device to list
				 * 5 - Print current devices
				 */
				
				BroadcastSenderSocket sender = new BroadcastSenderSocket();
				BroadcastReceiverSocket receiver = new BroadcastReceiverSocket();

				//STEP 1				
				byte[] messageByte = "DISCVR_IOT".getBytes();
				sender.sendData(messageByte);
				ArrayList<String> ids = new ArrayList<String>();
				
				while (true) {
					//STEP 2
					//TODO: create Threads in order to avoid losing packets
					receiver.receiveData(1, "CANICON_ID");
					
					//STEP 3
					messageByte = "CONFRM_IOT".getBytes();
					//TODO: send message individually. Currently broadcasting
					sender.sendData(messageByte);
					
					//STEP 4
					ids.add("ID" + Integer.toString(ids.size() + 1));
					
					//STEP 5
					String id;
					System.out.println("-------------------------");
					System.out.println(Integer.toString(ids.size()) + " device(s) discovered:");
					for (int i = 0; i < ids.size() - 1; i++) {
						id = ids.get(i);
						System.out.print(id + ", ");
					}
					id = ids.get(ids.size() - 1);
					System.out.println(id);
					
				}
			case 2:
				System.out.println("You chose IoT Device"); //TODO: class
				/* Protocol outline:
				 * 1 - Receives packet from the app, trying to discover new devices
				 * 2 - Sends packet to the app with device ID
				 * 2 - Receives packet from the app, confirming connection or denying connection 
				 */
				
				//STEP 1
				BroadcastReceiverSocket discoverableSocket = new BroadcastReceiverSocket();
				discoverableSocket.receiveData(1, "DISCVR_IOT");
				
				//STEP 2
				sender = new BroadcastSenderSocket();
				String messageStr = "CANICON_ID"; 
				messageByte = messageStr.getBytes();
				for (int attempts = 0; attempts <= 5; attempts++) {
					if (attempts < 5) {
						sender.sendData(messageByte);
						
						//STEP 3
						String dataRecvd = discoverableSocket.receiveData("CONFRM_IOT", 1000);
						if (dataRecvd.equals("CONFRM_IOT" )) {
							System.out.println("IoT device recognized by Server");
							break;
						}
					}
					else {
						System.out.println("IoT device was not recognized by Server. Timeout.");
					}
				}
				
				break;
			default: 
				System.out.println("Invalid input");
				break;
		}
		
	}

}
