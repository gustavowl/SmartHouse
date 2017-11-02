import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.ArrayList;


public class Protocol {
	
	private static volatile ArrayList<IOTDevice> iotsFound;
	private ReceiverSocket receiver;
	private SenderSocket sender;
	private ThreadIOTDiscoverer iotDiscoverer;
	
	public Protocol() {}
	
	public void discoverIot(boolean restart, boolean finish) {
		/* Protocol outline:
		 * 1 - Sends broadcast message in order to discover new devices
		 * 2 - Receives messages from multiple devices
		 * 3 - Adds discovered devices to list
		 */
		
		if (restart) {
			iotsFound = new ArrayList<IOTDevice>();
			receiver = new ReceiverSocket(12112);
			sender = new SenderSocket(12113);
			iotDiscoverer = new ThreadIOTDiscoverer("Discover IOTs", receiver, sender);
			
			//STEP 1				
			byte[] messageByte = "DISCVR_IOT".getBytes();
			sender.sendData(messageByte, 12114);
			iotDiscoverer.start(); //STEP 2 and 3
		}
		
		if (finish) {
			iotDiscoverer.interrupt();
			iotsFound.clear();
			//sender.close();
			//sender = null;
			receiver.close();
			//receiver = null;
			iotDiscoverer = null;
		}
	}
	
	private static IOTDevice extractIOTDeviceFromDatagram(DatagramPacket dp) {
		String data = new String(dp.getData());
		//System.out.println("------------\n" + data + "\n----------------");
		return new AppIOTDevice(dp.getAddress(), dp.getPort() - 1, data.trim());
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

				byte[] messageByte = "CONFRM_IOT".getBytes();
				synchronized (sender) {
					if (!sender.isClosed()) {
						sender.sendData(messageByte, iot.getAddress().getHostAddress(),
								iot.getListenerPort());
					}
				}
			}
		}
		
		@Override
		public void interrupt() {
			finished = true;
			super.interrupt();
		}
	}

	private void sendMessage(String content, String address, int port) {
		//sender = new SenderSocket(12113);
		byte[] message = content.getBytes();
		sender.sendData(message, address, port);
		sender.close();
	}
	
	public void confirmDiscoveredIotConnection(IOTDevice iot) {
		sendMessage("ADD_IOT", iot.getAddress().getHostAddress(), iot.getListenerPort());
	}

	public InetSocketAddress discoverServer(ReceiverSocket receiver, SenderSocket sender) {
		System.out.println("TODO: Implement discover method");
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
					System.out.println("IoT device was not recognized by Server. Timeout.");
				}
				attempts++;
			}
		}
	}
}
