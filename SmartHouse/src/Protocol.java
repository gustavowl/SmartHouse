import java.net.DatagramPacket;
import java.util.ArrayList;


public class Protocol {
	
	private static volatile ArrayList<IOTDevice> iotsFound;
	private ReceiverSocket receiver;
	private SenderSocket sender;
	private ThreadIOTDiscoverer iotDiscoverer;
	
	public Protocol() {}
	
	public void discover(boolean restart, boolean finish) {
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
			iotsFound = null;
			sender.close();
			receiver.close();
			iotDiscoverer = null;
		}
		
		/*
		int opt = scan.nextInt();
		if (opt > 0 && opt <= iotsFound.size()) {
			//STEP 4
			messageByte = "ADD_IOT".getBytes();
			DatagramPacket iot = iotsFound.get(opt - 1);
			synchronized (sender) {
				sender.sendData(messageByte, iot.getAddress().getHostAddress(),
						iot.getPort() - 1);
			}
			
			//STEP 5
			newIot = opt - 1;
			AppIOTDevice iotDiscovered = new AppIOTDevice(iotsFound.get(newIot).getAddress(),
					iotsFound.get(newIot).getPort(), "ID" + Integer.toString(newIot+1));
			connectedIots.add(iotDiscovered);
			
		}*/

	}
	
	private static IOTDevice extractIOTDeviceFromDatagram(DatagramPacket dp) {
		String data = new String(dp.getData());
		return new AppIOTDevice(dp.getAddress(), dp.getPort(), data.trim());
	}
	
	public ArrayList<IOTDevice> getIotsFound() {
		return iotsFound;
	}
	
	public ArrayList<IOTDevice> getAndClearIotsFound() {
		ArrayList<IOTDevice> iots;
		synchronized (iotsFound) {
			iots = (ArrayList<IOTDevice>) iotsFound.clone();
			iotsFound.clear();
		}
		return iots;
	}
	
	static class ThreadIOTDiscoverer extends Thread {
		private ReceiverSocket receiver;
		private SenderSocket sender;
		
		public ThreadIOTDiscoverer(String name, ReceiverSocket receiver, SenderSocket sender) {
			super(name);
			this.receiver = receiver;
			this.sender = sender;
		}
		
		@Override
		public void run() {
			//STEP 2
			DatagramPacket dataFromIoT = null;
			while (true) {
				try {
					dataFromIoT = receiver.receiveData(1, "CANICON_ID").get(0);
				} catch (NullPointerException e) {
					this.interrupt();
				}
				
				synchronized (iotsFound) {
					iotsFound.add(extractIOTDeviceFromDatagram(dataFromIoT)); //STEP 3
				}

				byte[] messageByte = "CONFRM_IOT".getBytes();
				synchronized (sender) {
					sender.sendData(messageByte, dataFromIoT.getAddress().getHostAddress(),
							dataFromIoT.getPort() - 1);
				}
			}
		}
	}
}
