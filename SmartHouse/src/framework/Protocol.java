package framework;
import java.util.ArrayList;


public class Protocol {
	//public static final int Receiver.getServerPort() = 12112;
	//public static final int Sender.getServerPort() = Receiver.getServerPort() + 1;
	//public static final int Receiver.getIotPort() = 12114;
	//public static final int Sender.getIotPort() = Receiver.getIotPort() + 1;
	public static final String[] VALID_SERVER_REQUESTS = {"DSCNCT_IOT", "CHCK_UPDT", "UPDATE",
			"GET_STATUS", "GETFUNCLST", "RUNIOTFUNC"};
	public static final String[] VALID_SERVER_REQUESTS_DESCRIPTIONS = {"Disconnect device",
			"Check for available updates", "Update device", "Get device's current status",
			"Get list of device's specific functionalities"};
	/* {DISCONNECT_IOT, GET_LIST_OF_FUNCTIONS, RUN_IOT_FUNCTION } 
	 * Commands available:
	 * 1 - Remove connection with server
	 * 2 - Check for updates
	 * 3 - Update
	 * 4 - Get iot status
	 * 5 - Get List of Methods
	 * 6 - Execute Method 
	 * 7 - List consumption (?)
	 */
	
	private static volatile ArrayList<IOTDevice> iotsFound;
	private volatile ThreadIOTDiscoverer iotDiscoverer;
	
	public Protocol() {}
	
	/*private static String discoverBuildMessageContent(Sender sender) {
		return sender.getAddress() + "__" + Receiver.getServerPort();
	}
	
	private static String[] discoverExtractMessageContent(String message) {
		String[] str = new String[2];
		int index = message.indexOf("__");
		str[0] = message.substring(0, index);
		str[1] = message.substring(index + 2);
		return str;
	}*/
	
	public void discoverIot(boolean restart, boolean finish, Receiver receiver,
			Sender sender) {
		/* Protocol outline:
		 * 1 - Sends broadcast message in order to discover new devices
		 * 2 - Receives messages from multiple devices
		 * 3 - Adds discovered devices to list
		 */
		
		if (restart) {
			iotsFound = new ArrayList<IOTDevice>();
			iotDiscoverer = new ThreadIOTDiscoverer("Discover IOTs", receiver, sender);
			
			//STEP 1
			sender.open(Sender.getServerPort(), true);
			sendMessage("DISCVR_IOT", "", Receiver.getIotPort(), sender);
			sender.close();
			iotDiscoverer.start(); //STEP 2 and 3
		}
		
		if (finish) {
			iotDiscoverer.interrupt();
			iotsFound.clear();
			iotDiscoverer = null;
		}
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
		private Receiver receiver;
		private Sender sender;
		private boolean finished = false;
		private volatile Thread thread;
		
		public ThreadIOTDiscoverer(String name, Receiver receiver, Sender sender) {
			super(name);
			this.receiver = receiver;
			this.sender = sender;
			this.thread = Thread.currentThread();
		}
		
		@Override
		public void run() {
			//STEP 2
			this.receiver.open(Receiver.getServerPort(), true);
			String dataFromIoT = null;
			while (!finished) {
				try {
					dataFromIoT = this.receiver.receiveData(1, "CANICON_ID").get(0);
				} catch (NullPointerException e) {
					this.interrupt();
				}
				
				if (!finished) {
					IOTDevice iot = new AppIOTDevice(receiver.getPeerAddress(),
							ProtocolMessage.getMessageContent(dataFromIoT));
					synchronized (iotsFound) {
						iotsFound.add(iot); //STEP 3
					}

					sender.open(Sender.getServerPort(), false);
					Protocol.sendMessage("CONFRM_IOT", "", iot.getAddress(),
							Receiver.getIotPort(), sender);
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
	
	private static void sendMessage(String code, String content, String port, Sender sender) {
		//broadcast
		sender.sendData(ProtocolMessage.createMessage(code, content), port);
	}

	private static void sendMessage(String code, String content, String address, String port, Sender sender) {
		//unicast		
		sender.sendData(ProtocolMessage.createMessage(code, content), address, port);
	}
	
	public void confirmDiscoveredIotConnection(String address, Sender sender) {
		sender.open(Sender.getServerPort(), false);
		sendMessage("ADD_IOT", "", address, Receiver.getIotPort(), sender);
		sender.close();
	}
	
	/* Denies connection using unicast only. Does not deny in broadcast because UDP can 
	 * change the order of the messages. Henceforth, the "CONFIRM_CONNECTION" message 
	 * may arrive after the "DENY_CONNECTION" message
	 */
	public void denyDiscoveredIotConnection(String address, Sender sender) {
		sender.open(Sender.getServerPort(), false);
		sendMessage("DENY_IOT", "", address, Receiver.getIotPort(), sender);
		sender.close();		
	}

	public String discoverServer(Receiver receiver, Sender sender, String iotId) {
		/* Protocol outline:
		 * 1 - Receives packet from the app, trying to discover new devices
		 * 2 - Sends packet to the app with device ID
		 * 3 - Receives packet from the app, asking to stop sending connection requests
		 * 4 - Receives packet from the app confirming or denying connection
		 */
		
		receiver.open(Receiver.getIotPort(), true);
		while (true) {
			//STEP 1
			receiver.receiveData("DISCVR_IOT");
			/*String dataFromApp = receiver.receiveData("DISCVR_IOT");
			dataFromApp = ProtocolMessage.getMessageContent(dataFromApp);
			dataFromApp = discoverExtractMessageContent(dataFromApp)[0];*/
			sender.open(Sender.getIotPort(), false);
			
			//STEP 2
			int attempts = 0; //FIXME: USE sendMessageAndWait
			while (attempts <= 60) {
				if (attempts < 60) {
					sendMessage("CANICON_ID", iotId, receiver.getPeerAddress(),
							Receiver.getServerPort(), sender);
					
					//STEP 3
					String dataRecvd = receiver.receiveData("CONFRM_IOT", 1000);
					
					if (dataRecvd != null) {
						//STEP 4
						attempts = 0;
						while (attempts <= 60) {
							dataRecvd = receiver.receiveData(new String[] {"ADD_IOT", "DENY_IOT"}, 1000);
							if (dataRecvd != null) {
								if (ProtocolMessage.getMessageCode(dataRecvd).equals("ADD_IOT")) {
									//CONNECTION CONFIRMED
									receiver.close();
									sender.close();
									return receiver.getPeerAddress();
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
	
	public String listenToServerRequests(Receiver receiver) {
		while (true) {
			//Listens to server
			receiver.open(Receiver.getIotPort(), false);
			ArrayList<String> dpList = receiver.receiveData(1);
			if (dpList != null) {
				String dp = dpList.get(0);
				String code = ProtocolMessage.getMessageCode(dp);
				if (isServerRequestValid(code)) {
					return dp;
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
	
	public static String serverRequestDisconnectIot(String address, Sender sender,
			Receiver receiver) {
		/* PROTOCOL OUTLINE
		 * 1 - Sends disconnection request
		 * 2 - Wait for response
		 * 3 - If (timeout)
		 * 			returns timeoutMessage
		 * 	   else
		 * 			return messageFromIotDevice
		 */
		sender.open(Sender.getServerPort(), false);
		sendMessage(VALID_SERVER_REQUESTS[0], "", address, Receiver.getIotPort(), sender);
		sender.close();
		
		receiver.open(Receiver.getServerPort(), false);
		String dp = receiver.receiveData("DSCNCT_SRV", 5000); //FIXME: CHANGE TO 60000
		receiver.close();
		
		if (dp != null) {
			return dp;
		}
		//else timeout
		return ProtocolMessage.createMessage("TIMEOUT", "");
	}
	
	public static void iotDisconnectFromServer(String serverAddress, Sender sender) {
		sender.open(Sender.getIotPort(), false);
		sendMessage("DSCNCT_SRV", "", serverAddress, Receiver.getServerPort(), sender);
		sender.close();
	}
	
	public static void iotSendFunctionalitiesToServer(String serverAddress, Receiver receiver,
			Sender sender, ArrayList<String> iotFacadeMethods) {
		/* PROTOCOL OUTLINE
		 * 1 - Send Message to server and waits for connection confirmation
		 * 2 - Foreach method
		 * 3 - 		Send method to server
		 * 4 - 		wait for server confirmation. If necessary, resend
		 * 
		 * 5 - Whenever method was resent for a specific number of times, connection was lost. Return
		 */
		sender.open(Sender.getIotPort(), false);
		receiver.open(Receiver.getIotPort(), false);
		
		//STEP 1
		//IOT FUNCTION BEGIN | IOT SEND START
		if (sendMessageAndWait("IOTFUNCBGN", new Integer(iotFacadeMethods.size()).toString(),
				serverAddress, Receiver.getServerPort(), 60, sender, "IOTSNDSTRT",
				receiver) != null) {
			
			//STEP 2
			for (int i = 0; i < iotFacadeMethods.size(); i++) {
				//STEP 3 and 4
				//IOT FUNCTION SENT | IOT FUNCTION RECEIVED
				if (sendMessageAndWait("IOTFNCSNT" + i, iotFacadeMethods.get(i), serverAddress,
						Receiver.getServerPort(), 60, sender, "IOTFNCRCV" + i, receiver) == null) {
					//STEP 5
					sender.close();
					receiver.close();
					return;
				}
			}
		}
		sender.close();
		receiver.close();
		//STEP 5
	}
	
	//Returns null if timeout
	//Return the ENTIRE content of the package otherwise (alongside with the code)
	private static String sendMessageAndWait(final String msgCode, final String msgContent, 
			final String address, final String port, final int MAX_ATTEMPTS, final Sender sender,
			final String confirmationCode, final Receiver receiver) {
		int attempts = 0;
		while (attempts < MAX_ATTEMPTS) {
			//IOT FUNCTION SENT
			sendMessage(msgCode, msgContent, address, port, sender);
			String packet = receiver.receiveData(confirmationCode, 1000); 
			if (packet != null) {
				return packet;
			}
			attempts++;
		}
		return null;
	}
	
	public static String serverRequestIotFunctionalities(String iotAddress, Receiver receiver,
			Sender sender) {
		/* PROTOCOL OUTLINE
		 * 1 - Sends message to iot requesting lists
		 * 2 - Wait message from iot and sends connection confirmation
		 * 3 - foreach method
		 * 4 - 		wait for message from iot
		 * 5 - 		sends server confirmation
		 * 
		 * 6 - Whenever confirmation was resent for a specific number of times, connection was lost. Return
		 */
		
		//TODO: detect if size of package is too big. Return ArrayList<byte[]>?
		
		//STEP 1 and beginning of STEP 2
		//GET FUNCTIONALITIES LIST | IOT FUNCTION BEGIN
		sender.open(Sender.getServerPort(), false);
		receiver.open(Receiver.getServerPort(), false);
		String msgReceived;

		msgReceived = sendMessageAndWait("GETFUNCLST", "", iotAddress,
				Receiver.getIotPort(), 60, sender, "IOTFUNCBGN", receiver); 

		if (msgReceived != null) {
			int qttPackets = Integer.parseInt(ProtocolMessage.getMessageContent(msgReceived));
			
			//STEP 2  (ending) and STEP 3 (beginning)
			msgReceived = sendMessageAndWait("IOTSNDSTRT", "", iotAddress,
					Receiver.getIotPort(), 60, sender, "IOTFNCSNT0", receiver); 
			if (msgReceived != null) {
				//STEP 3 and 4
				int i = 1;
				String ret = ProtocolMessage.getMessageContent(msgReceived);
				while(i < qttPackets) {
					msgReceived = sendMessageAndWait("IOTFNCRCV" + (i - 1), "", iotAddress,
							Receiver.getIotPort(), 60, sender, "IOTFNCSNT" + i, receiver); 
					if (msgReceived != null) {
						ret += ProtocolMessage.getSeparator() + ProtocolMessage.getMessageContent(msgReceived);
						i++;
					}
					else {
						sender.close();
						receiver.close();
						return ProtocolMessage.createMessage("TIMEOUT", "");
					}
				}
				sendMessage("IOTFNCRCV" + (i - 1), "", iotAddress,
						Receiver.getIotPort(), sender);
				sender.close();
				receiver.close();
				return ProtocolMessage.createMessage("IRRELEVANT", ret);
			}			
		}
		
		sender.close();
		receiver.close();
		return ProtocolMessage.createMessage("TIMEOUT", "");
	}
	
	public static String serverRequestRunningIotFunctionality(String methodSignature, String args,
			String iotAddress, Receiver receiver, Sender sender) {
		
		receiver.open(Receiver.getServerPort(), false);
		sender.open(Sender.getServerPort(), false);
		//RUN IOT ESPECIFIC FUNCTIONALITY | RUN IOT FUNCTIONALITY REQUEST RECEIVED
		String result = sendMessageAndWait("RUNIOTFUNC", methodSignature + args,
				iotAddress, Receiver.getIotPort(), 60, sender, "IOTFUNCRCV", receiver);
		receiver.close();
		sender.close();
		if (result != null) {
			return result;
		}
		return ProtocolMessage.createMessage("TIMEOUT", "");
	}
	
	public static void iotSendRunFunctionalityRequestReceived(String serverAddress, Sender sender) {
		sender.open(Sender.getIotPort(), false);
		sendMessage("IOTFUNCRCV", "", serverAddress, Receiver.getServerPort(), sender);
		sender.close();
	}
	
	public static String serverRequestIotStatus(String iotAddress, Receiver receiver,
			Sender sender) {
		
		sender.open(Sender.getServerPort(), false);
		receiver.open(Receiver.getServerPort(), false);
		
		String result = sendMessageAndWait("GET_STATUS", "", iotAddress, Receiver.getIotPort(),
				60, sender, "IOT_STATUS", receiver);
		
		receiver.close();
		sender.close();
		
		if (result != null) {
			return result;
		}
		return ProtocolMessage.createMessage("TIMEOUT", "");
	}
	
	public static void iotSendStatus(String status, String serverAddress, Sender sender) {
		sender.open(Sender.getIotPort(), false);
		sendMessage("IOT_STATUS", status, serverAddress, Receiver.getServerPort(), sender);
		sender.close();
	}
}
