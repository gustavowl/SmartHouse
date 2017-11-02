import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;


public class Protocol {
	public static final int SERVER_RECEIVER_PORT = 12112;
	public static final int SERVER_SENDER_PORT = SERVER_RECEIVER_PORT + 1;
	public static final int IOT_RECEIVER_PORT = 12114;
	public static final int IOT_SENDER_PORT = IOT_RECEIVER_PORT + 1;
	public static final String[] VALID_SERVER_REQUESTS = {"DSCNCT_IOT", "RUNIOTFUNC", "GETFUNCLST"};
	/* {DISCONNECT_IOT, RUN_IOT_FUNCTION, GET_LIST_OF_FUNCTIONS } 
	 * Commands available:
	 * 1 - Remove connection with server
	 * 2 - Execute Method
	 * 3 - Get List of Methods
	 * 4 - Update (?)
	 * 5 - Check for updates (?)
	 * 6 - List consumption (?)
	 */
	
	private static volatile ArrayList<IOTDevice> iotsFound;
	private ThreadIOTDiscoverer iotDiscoverer;
	
	public Protocol() {}
	
	public void discoverIot(boolean restart, boolean finish, ReceiverSocket receiver,
			SenderSocket sender) {
		/* Protocol outline:
		 * 1 - Sends broadcast message in order to discover new devices
		 * 2 - Receives messages from multiple devices
		 * 3 - Adds discovered devices to list
		 */
		
		if (restart) {
			iotsFound = new ArrayList<IOTDevice>();
			iotDiscoverer = new ThreadIOTDiscoverer("Discover IOTs", receiver, sender);
			
			//STEP 1				
			sendMessage("DISCVR_IOT", "", IOT_RECEIVER_PORT, sender);
			iotDiscoverer.start(); //STEP 2 and 3
		}
		
		if (finish) {
			iotDiscoverer.interrupt();
			iotsFound.clear();
			iotDiscoverer = null;
		}
	}
	
	private static IOTDevice extractIOTDeviceFromDatagram(DatagramPacket dp) {
		//port at datagram is sender. Receiver will be it - 1 (check constants)
		return new AppIOTDevice(dp.getAddress(), dp.getPort() - 1, 
				ProtocolMessage.getMessageContent(dp.getData()));
	}
	
	public ArrayList<IOTDevice> getIotsFound() {
		return iotsFound;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<IOTDevice> getAndClearIotsFound() {
		ArrayList<IOTDevice> iots = null;
		if (iotsFound != null) {
			synchronized (iotsFound) {
				iots = (ArrayList<IOTDevice>) iotsFound.clone();
				iotsFound.removeAll(iotsFound);
			}	
		}
		return iots;
	}
	
	static class ThreadIOTDiscoverer extends Thread {
		private ReceiverSocket receiver;
		private SenderSocket sender;
		private boolean finished = false;
		
		public ThreadIOTDiscoverer(String name, ReceiverSocket receiver, SenderSocket sender) {
			super(name);
			this.receiver = receiver;
			this.sender = sender;
		}
		
		@Override
		public void run() {
			//STEP 2
			DatagramPacket dataFromIoT = null;
			while (!finished) {
				try {
					dataFromIoT = receiver.receiveData(1, "CANICON_ID").get(0);
				} catch (NullPointerException e) {
					this.interrupt();
				}
				
				IOTDevice iot = extractIOTDeviceFromDatagram(dataFromIoT);
				synchronized (iotsFound) {
					iotsFound.add(iot); //STEP 3
				}

				synchronized (sender) {
					Protocol.sendMessage("CONFRM_IOT", "", iot.getAddress().getHostAddress(),
							iot.getListenerPort(), sender);
				}
			}
		}
		
		@Override
		public void interrupt() {
			finished = true;
			super.interrupt();
		}
	}
	
	private static void sendMessage(String code, String content, int port, SenderSocket sender) {
		//broadcast
		byte[] message = ProtocolMessage.createMessage(code, content);
		sender.sendData(message, port);
	}

	private static void sendMessage(String code, String content, String address, int port, SenderSocket sender) {
		//unicast
		byte[] message = ProtocolMessage.createMessage(code, content);
		sender.sendData(message, address, port);
	}
	
	public void confirmDiscoveredIotConnection(IOTDevice iot, SenderSocket sender) {
		sendMessage("ADD_IOT", "", iot.getAddress().getHostAddress(), iot.getListenerPort(), sender);
	}

	public InetSocketAddress discoverServer(ReceiverSocket receiver, SenderSocket sender, String iotId) {
		/* Protocol outline:
		 * 1 - Receives packet from the app, trying to discover new devices
		 * 2 - Sends packet to the app with device ID
		 * 3 - Receives packet from the app, asking to stop sending connection requests
		 * 4 - Receives packet from the app confirming connection
		 */
		
		while (true) {
			//STEP 1
			DatagramPacket dataFromApp = receiver.receiveData(1, "DISCVR_IOT").get(0);
			
			//STEP 2
			int attempts = 0;
			while (attempts <= 60) {
				if (attempts < 60) {
					sendMessage("CANICON_ID", iotId, dataFromApp.getAddress().getHostAddress(),
							dataFromApp.getPort() - 1, sender);
					
					//STEP 3
					DatagramPacket dataRecvd = receiver.receiveData("CONFRM_IOT", 1000);
					if (dataRecvd != null) {
						
						//STEP 4
						attempts = 0;
						while (attempts <= 60) {
							dataRecvd = receiver.receiveData("ADD_IOT", 1000);
							if (dataRecvd != null) {
								InetSocketAddress address = new InetSocketAddress(
										dataFromApp.getAddress(), dataFromApp.getPort());
								return address;
							}
							attempts++;
						}
						break;
					}
				}
				else {
					return null;
				}
				attempts++;
			}
		}
	}
	
	public String listenToServerRequests(InetAddress serverAddress, int serverPort,
			ReceiverSocket receiver) {
		while (true) {
			//Listens to server
			DatagramPacket dp = receiver.receiveData(1).get(0);
			String code = ProtocolMessage.getMessageCode(dp.getData());
			if (isServerRequestValid(code)) {
				return code;
			}
		}
	}
	
	private boolean isServerRequestValid(String requestCode) {
		for (String code : VALID_SERVER_REQUESTS) {
			if (requestCode.equals(code)) {
				return true;
			}
		}
		return false;
	}
}
