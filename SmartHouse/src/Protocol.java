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
			this.receiver.open(SERVER_RECEIVER_PORT);
			DatagramPacket dataFromIoT = null;
			while (!finished) {
				try {
					dataFromIoT = receiver.receiveData(1, "CANICON_ID").get(0);
				} catch (NullPointerException e) {
					this.interrupt();
				}
				
				if (!finished) {
					IOTDevice iot = extractIOTDeviceFromDatagram(dataFromIoT);
					synchronized (iotsFound) {
						iotsFound.add(iot); //STEP 3
					}

					synchronized (sender) {
						Protocol.sendMessage("CONFRM_IOT", "", iot.getAddress().getHostAddress(),
								IOT_RECEIVER_PORT, sender);
					}
				}
			}
		}
		
		@Override
		public void interrupt() {
			finished = true;
			receiver.close();
			thread.interrupt();
			super.interrupt();
		}
	}
	
	private static void sendMessage(String code, String content, int port, SenderSocket sender) {
		//broadcast
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
		System.out.println(Time.valueOf(LocalTime.now()) + " Broadcast - " + code +
				" / Threads: " + threadArray.length + printThreads(threadArray));
		
		byte[] message = ProtocolMessage.createMessage(code, content);
		sender.sendData(message, port);
	}

	private static void sendMessage(String code, String content, String address, int port, SenderSocket sender) {
		//unicast
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
		System.out.println(Time.valueOf(LocalTime.now()) + " Unicast - " + code +
				" / Threads: " + threadArray.length + printThreads(threadArray));
		
		byte[] message = ProtocolMessage.createMessage(code, content);
		sender.sendData(message, address, port);
	}
	
	//FIXME: Delete
	private static String printThreads(Thread[] threadArray) {
		String ret = "";
		for (int i = 0; i < threadArray.length; i++) {
			ret += "| " + threadArray[i].getName();
		}
		return ret;
	}
	
	public void confirmDiscoveredIotConnection(InetAddress address, SenderSocket sender) {
		sendMessage("ADD_IOT", "", address.getHostAddress(), IOT_RECEIVER_PORT, sender);
	}

	public InetSocketAddress discoverServer(ReceiverSocket receiver, SenderSocket sender, String iotId) {
		/* Protocol outline:
		 * 1 - Receives packet from the app, trying to discover new devices
		 * 2 - Sends packet to the app with device ID
		 * 3 - Receives packet from the app, asking to stop sending connection requests
		 * 4 - Receives packet from the app confirming connection
		 */
		
		receiver.open(IOT_RECEIVER_PORT);
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
								receiver.close();
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
			receiver.open(serverAddress.getHostName(), IOT_RECEIVER_PORT);
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
		sendMessage(VALID_SERVER_REQUESTS[0], "", address.getHostAddress(), IOT_RECEIVER_PORT, sender);
		receiver.open(address.getHostAddress(), SERVER_RECEIVER_PORT);
		DatagramPacket dp = receiver.receiveData("DSCNCT_SRV", 5000); //FIXME: CHANGE TO 60000
		receiver.close();
		if (dp != null) {
			return dp.getData();
		}
		//else timeout
		return ProtocolMessage.createMessage("TIMEOUT", "");
	}
	
	public static void iotDisconnectFromServer(InetAddress serverAddress, SenderSocket sender) {
		sendMessage("DSCNCT_SRV", "", serverAddress.getHostAddress(), 
				SERVER_RECEIVER_PORT, sender);
	}
}
