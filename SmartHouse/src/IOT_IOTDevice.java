import java.net.DatagramPacket;
import java.net.InetAddress;

//IOTDevice representation as seen by the IOT itself
public class IOT_IOTDevice extends IOTDevice {
	
	public IOT_IOTDevice() {
		super();
	}
	
	public IOT_IOTDevice(String name) {
		super(name);
	}
	
	public IOT_IOTDevice(InetAddress peerAddress, int peerPort, String name) {
		super(peerAddress, peerPort, name);
		
	}
	
	public void update() {
		System.out.println("TODO: Implement update method");
	}
	
	public void remove() {
		System.out.println("TODO: Implement update method");
	}
	
	public void discover() {
		System.out.println("TODO: Implement discover method");
		/* Protocol outline:
		 * 1 - Receives packet from the app, trying to discover new devices
		 * 2 - Sends packet to the app with device ID
		 * 3 - Receives packet from the app, asking to stop sending connection requests
		 * 4 - Receives packet from the app confirming connection
		 */
		
		while (!isPeerAddressValid()) {
			//STEP 1
			ReceiverSocket discoverableSocket = new ReceiverSocket(12114);
			DatagramPacket dataFromApp = discoverableSocket.receiveData(1, "DISCVR_IOT").get(0);
			
			//STEP 2
			SenderSocket sender = new SenderSocket("0.0.0.0", 12115);
			String messageStr = "CANICON_ID"; 
			byte[] messageByte = messageStr.getBytes();
			int attempts = 0;
			while (attempts <= 60) {
				if (attempts < 60) {
					sender.sendData(messageByte, dataFromApp.getAddress().getHostAddress(),
							dataFromApp.getPort() - 1);
					
					//STEP 3
					DatagramPacket dataRecvd = discoverableSocket.receiveData("CONFRM_IOT", 1000);
					if (dataRecvd != null) {
						System.out.println("IoT device recognized by Server");
						
						//STEP 4
						attempts = 0;
						while (attempts <= 60) {
							dataRecvd = discoverableSocket.receiveData("CONFRM_IOT", 1000);
							if (dataRecvd != null) {
								peerAddress = dataFromApp.getAddress();
								peerPort = dataFromApp.getPort();
								break;
							}
						}
						break;
					}
				}
				else {
					System.out.println("IoT device was not recognized by Server. Timeout.");
				}
			}
			attempts++;
		}
	}
	
	public void listenToPeer() {
		System.out.println("TODO: wait for peer action requests");
	}
	
	public void run() {
		while (peerAddress == null) {
			discover();
		}
		while (peerAddress != null) {
			listenToPeer();
		}
	}
}
