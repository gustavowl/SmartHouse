import java.net.DatagramPacket;
import java.net.InetAddress;

//IOTDevice representation as seen by the IOT itself
public class IOT_IOTDevice extends IOTDevice {
	ReceiverSocket receiver;
	SenderSocket sender;
	
	public IOT_IOTDevice() {
		this(12114, "", null, 0);
	}
	
	public IOT_IOTDevice(String name) {
		this(12114, name, null, 0);
	}
	
	public IOT_IOTDevice(int port, String name,
			InetAddress peerAddress, int peerPort) {
		super(null, port, name);
		receiver = new ReceiverSocket(port);
		sender = new SenderSocket("0.0.0.0", 12115);
		setPeerAddress(peerAddress);
		setPeerPort(peerPort);
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
			DatagramPacket dataFromApp = receiver.receiveData(1, "DISCVR_IOT").get(0);
			
			//STEP 2
			String messageStr = "CANICON_ID"; 
			byte[] messageByte = messageStr.getBytes();
			int attempts = 0;
			while (attempts <= 60) {
				if (attempts < 60) {
					sender.sendData(messageByte, dataFromApp.getAddress().getHostAddress(),
							dataFromApp.getPort() - 1);
					
					//STEP 3
					DatagramPacket dataRecvd = receiver.receiveData("CONFRM_IOT", 1000);
					if (dataRecvd != null) {
						//System.out.println("IoT device recognized by Server");
						
						//STEP 4
						attempts = 0;
						while (attempts <= 60) {
							dataRecvd = receiver.receiveData("ADD_IOT", 1000);
							//System.out.print("HI");
							if (dataRecvd != null) {
								peerAddress = dataFromApp.getAddress();
								peerPort = dataFromApp.getPort();
								break;
							}
							attempts++;
						}
						break;
					}
				}
				else {
					System.out.println("IoT device was not recognized by Server. Timeout.");
				}
				attempts++;
			}
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
