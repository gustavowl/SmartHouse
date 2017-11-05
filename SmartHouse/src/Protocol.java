import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Set;


public class Protocol {
	public static final int SERVER_RECEIVER_PORT = 12112;
	public static final int SERVER_SENDER_PORT = SERVER_RECEIVER_PORT + 1;
	public static final int IOT_RECEIVER_PORT = 12114;
	public static final int IOT_SENDER_PORT = IOT_RECEIVER_PORT + 1;
	public static final String[] VALID_SERVER_REQUESTS = {"DSCNCT_IOT", "CHCK_UPDT", "UPDATE",
			"GETFUNCLST", "RUNIOTFUNC"};
	public static final String[] VALID_SERVER_REQUESTS_DESCRIPTIONS = {"Disconnect device",
			"Check for available updates", "Update device",
			"Get list of device's specific functionalities"};
	/* {DISCONNECT_IOT, GET_LIST_OF_FUNCTIONS, RUN_IOT_FUNCTION } 
	 * Commands available:
	 * 1 - Remove connection with server
	 * 2 - Check for updates
	 * 3 - Update
	 * 4 - Get List of Methods
	 * 5 - Execute Method 
	 * 6 - List consumption (?)
	 */
	
	private static volatile ArrayList<IOTDevice> iotsFound;
	private volatile ThreadIOTDiscoverer iotDiscoverer;
	
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
			sender.open(SERVER_SENDER_PORT, true);
			sendMessage("DISCVR_IOT", "", IOT_RECEIVER_PORT, sender);
			sender.close();
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
		private volatile Thread thread;
		
		public ThreadIOTDiscoverer(String name, ReceiverSocket receiver, SenderSocket sender) {
			super(name);
			this.receiver = receiver;
			this.sender = sender;
			this.thread = Thread.currentThread();
		}
		
		@Override
		public void run() {
			//STEP 2
			this.receiver.open(SERVER_RECEIVER_PORT, true);
			DatagramPacket dataFromIoT = null;
			while (!finished) {
				try {
					dataFromIoT = this.receiver.receiveData(1, "CANICON_ID").get(0);
				} catch (NullPointerException e) {
					this.interrupt();
				}
				
				if (!finished) {
					IOTDevice iot = extractIOTDeviceFromDatagram(dataFromIoT);
					synchronized (iotsFound) {
						iotsFound.add(iot); //STEP 3
					}

					sender.open(SERVER_SENDER_PORT, false);
					Protocol.sendMessage("CONFRM_IOT", "", iot.getAddress().getHostAddress(),
							IOT_RECEIVER_PORT, sender);
					sender.close();
				}
			}
		}
		
		@Override
		public void interrupt() {
			finished = true;
			this.receiver.close();
			thread.interrupt();
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
	
	public void confirmDiscoveredIotConnection(InetAddress address, SenderSocket sender) {
		sender.open(SERVER_SENDER_PORT, false);
		sendMessage("ADD_IOT", "", address.getHostAddress(), IOT_RECEIVER_PORT, sender);
		sender.close();
	}
	
	/* Denies connection using unicast only. Does not deny in broadcast because UDP can 
	 * change the order of the messages. Henceforth, the "CONFIRM_CONNECTION" message 
	 * may arrive after the "DENY_CONNECTION" message
	 */
	public void denyDiscoveredIotConnection(InetAddress address, SenderSocket sender) {
		sender.open(SERVER_SENDER_PORT, false);
		sendMessage("DENY_IOT", "", address.getHostAddress(), IOT_RECEIVER_PORT, sender);
		sender.close();		
	}

	public InetSocketAddress discoverServer(ReceiverSocket receiver, SenderSocket sender, String iotId) {
		/* Protocol outline:
		 * 1 - Receives packet from the app, trying to discover new devices
		 * 2 - Sends packet to the app with device ID
		 * 3 - Receives packet from the app, asking to stop sending connection requests
		 * 4 - Receives packet from the app confirming or denying connection
		 */
		
		receiver.open(IOT_RECEIVER_PORT, true);
		while (true) {
			//STEP 1
			DatagramPacket dataFromApp = receiver.receiveData(1, "DISCVR_IOT").get(0);
			sender.open(IOT_SENDER_PORT, false);
			
			//STEP 2
			int attempts = 0;
			while (attempts <= 60) {
				if (attempts < 60) {
					sendMessage("CANICON_ID", iotId, dataFromApp.getAddress().getHostAddress(),
							SERVER_RECEIVER_PORT, sender);
					
					//STEP 3
					DatagramPacket dataRecvd = receiver.receiveData("CONFRM_IOT", 1000);
					
					if (dataRecvd != null) {
						//STEP 4
						attempts = 0;
						while (attempts <= 60) {
							dataRecvd = receiver.receiveData(new String[] {"ADD_IOT", "DENY_IOT"}, 1000);
							if (dataRecvd != null) {
								if (ProtocolMessage.getMessageCode(dataRecvd.getData()).equals("ADD_IOT")) {
									//CONNECTION CONFIRMED
									receiver.close();
									sender.close();
									InetSocketAddress address = new InetSocketAddress(
											dataFromApp.getAddress(), dataFromApp.getPort());
									return address;
								}
								else { //ProtocolMessage.getMessageCode(dataRecvd.getData()) == "RMV_IOT"
									//CONNECTION DENIED
									break;
								}
							}
							attempts++;
						}
						sender.close();
						break;
					}
				}
				else {
					sender.close();
					receiver.close();
					return null;
				}
				attempts++;
			}
		}
	}
	
	public String listenToServerRequests(InetAddress serverAddress, ReceiverSocket receiver) {
		while (true) {
			//Listens to server
			receiver.open(IOT_RECEIVER_PORT, false);
			ArrayList<DatagramPacket> dpList = receiver.receiveData(1);
			if (dpList != null) {
				DatagramPacket dp = dpList.get(0);
				String code = ProtocolMessage.getMessageCode(dp.getData());
				if (isServerRequestValid(code)) {
					return code;
				}
			}
			receiver.close();
		}
	}
	
	private boolean isServerRequestValid(String requestCode) {
		for (int i = 0; i < VALID_SERVER_REQUESTS.length; i++) {
			if (requestCode.equals(VALID_SERVER_REQUESTS[i])) {
				return true;
			}
		}
		return false;
	}
	
	public static byte[] serverRequestDisconnectIot(InetAddress address, SenderSocket sender,
			ReceiverSocket receiver) {
		/* PROTOCOL OUTLINE
		 * 1 - Sends disconnection request
		 * 2 - Wait for response
		 * 3 - If (timeout)
		 * 			returns timeoutMessage
		 * 	   else
		 * 			return messageFromIotDevice
		 */
		sender.open(SERVER_SENDER_PORT, false);
		sendMessage(VALID_SERVER_REQUESTS[0], "", address.getHostAddress(), IOT_RECEIVER_PORT, sender);
		sender.close();
		
		receiver.open(SERVER_RECEIVER_PORT, false);
		DatagramPacket dp = receiver.receiveData("DSCNCT_SRV", 5000); //FIXME: CHANGE TO 60000
		receiver.close();
		
		if (dp != null) {
			return dp.getData();
		}
		//else timeout
		return ProtocolMessage.createMessage("TIMEOUT", "");
	}
	
	public static void iotDisconnectFromServer(InetAddress serverAddress, SenderSocket sender) {
		sender.open(IOT_SENDER_PORT, false);
		sendMessage("DSCNCT_SRV", "", serverAddress.getHostAddress(), 
				SERVER_RECEIVER_PORT, sender);
		sender.close();
	}
	
	public static void iotSendFunctionalitiesToServer(InetAddress serverAddress, ReceiverSocket receiver,
			SenderSocket sender, ArrayList<String> iotFacadeMethods) {
		/* PROTOCOL OUTLINE
		 * 1 - Send Message to server and waits for connection confirmation
		 * 2 - Foreach method
		 * 3 - 		Send method to server
		 * 4 - 		wait for server confirmation. If necessary, resend
		 * 
		 * 5 - Whenever method was resent for a specific number of times, connection was lost. Return
		 */
		sender.open(IOT_SENDER_PORT, false);
		receiver.open(IOT_RECEIVER_PORT, false);
		
		//STEP 1
		//IOT FUNCTION BEGIN | IOT SEND START
		if (sendMessageAndWait("IOTFUNCBGN", new Integer(iotFacadeMethods.size()).toString(),
				serverAddress.getHostAddress(), SERVER_RECEIVER_PORT, 60, sender, "IOTSNDSTRT", receiver)) {
			
			//STEP 2
			for (int i = 0; i < iotFacadeMethods.size(); i++) {
				//STEP 3 and 4
				//IOT FUNCTION SENT | IOT FUNCTION RECEIVED
				if (! sendMessageAndWait("IOTFUNCSNT", iotFacadeMethods.get(i), serverAddress.getHostAddress(),
						SERVER_RECEIVER_PORT, 60, sender, "IOTFUNCRCV", receiver)) {
					//STEP 5
					return;
				}
			}
		}
		//STEP 5
	}
	
	//Returns true if message was received
	private static boolean sendMessageAndWait(final String msgCode, final String msgContent, 
			final String address, final int port, final int MAX_ATTEMPTS, final SenderSocket sender,
			final String confirmationCode, final ReceiverSocket receiver) {
		int attempts = 0;
		while (attempts < MAX_ATTEMPTS) {
			//IOT FUNCTION SENT
			sendMessage(msgCode, msgContent, address, port, sender);
			if (receiver.receiveData(confirmationCode, 1000) != null) {
				return true;
			}
			attempts++;
		}
		return false;
	}
}
