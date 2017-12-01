import java.util.ArrayList;

public class App {
	ArrayList<AppIOTDevice> connectedIots;
	private ArrayList<AppIOTDevice> iotsDiscovered;
	private ProtocolFacade protocol;
	private Receiver receiver;
	private Sender sender;
	private ThreadDiscoverer  td;
	private UserInterface ui;
	
	public App(Receiver receiver, Sender sender) {
		connectedIots = new ArrayList<AppIOTDevice>();
		protocol = new ProtocolFacade();
		this.receiver = receiver;
		this.sender = sender;
		ui = new StandardUserInterface(this);
	}
	
	public void run() {
		ui.run();
	}
	
	public void discoveryStart() {
		/* Protocol outline:
		 * 1 - Sends broadcast message in order to discover new devices
		 * 2 - Receives messages from multiple devices
		 * 3 - Prints available device list to connect
		 * 4 - Sends message confirming or denying connection to devices
		 * 5 - Adds IoT to list of recognized devices
		 */
		protocol.IOTDiscoveryStart(receiver, sender);
		iotsDiscovered = new ArrayList<AppIOTDevice>();
		td = new ThreadDiscoverer("Writer");
		td.start();
	}
	
	public void discoveryFinish(int iotIndex) {			
		protocol.IOTDiscoveryStop();
		td.interrupt();
		if (iotIndex >= 0 && iotIndex < iotsDiscovered.size()) {
			AppIOTDevice iot = iotsDiscovered.get(iotIndex);
			
			protocol.confirmConnection(iot.getAddress(), sender); //STEP 4
			connectedIots.add(iot); //STEP 5
		}
		for (AppIOTDevice iot : iotsDiscovered) {
			protocol.denyConnection(iot.getAddress(), sender);
		}
		iotsDiscovered.clear();
		iotsDiscovered = null;
	}
	
	public String getNewDiscoveredDevice(int iotIndex) {
		if (iotIndex < iotsDiscovered.size()) {
			return iotsDiscovered.get(iotIndex).getName();
		}
		return null;
	}
	
	class ThreadDiscoverer extends Thread {
		//TODO: transfer to GUI
		volatile boolean finish = false;
		
		public ThreadDiscoverer(String name) {
			super(name);
		}
		
		@Override
		public void run() {
			ArrayList<IOTDevice> iots;
			//STEP 3
			while (!finish) {
				iots = protocol.getIotsDiscovered();
				while (iots != null && iots.size() > 0) {
					AppIOTDevice iot = (AppIOTDevice)iots.remove(0);
					iotsDiscovered.add(iot);
				}
			}
		}
		
		@Override
		public void interrupt() {
			finish = true;
			super.interrupt();
		}
	}
	
	public String[] getConnectedIots() {
		String[] connIotsNames = new String[connectedIots.size()];
		for (int i = 0; i < connIotsNames.length; i++) {
			connIotsNames[i] = connectedIots.get(i).getName();
		}
		return connIotsNames;
	}
	
	public String[] listSelectIotOptions() {
		return ProtocolFacade.getValidServerRequestsDescriptions();
	}
	
	//returns true if list
	public boolean executeIotStandardFunctionality(int iotIndex, int option, StringBuilder message) {
		if (ProtocolFacade.isGetDevicesFunctionalities(option)) {
			//int iotsConnectedSize = connectedIots.size();
			
			String iotFunctionalitiesStr = ProtocolFacade.runGeneralServerRequest(option,
					connectedIots, iotIndex, sender, receiver);
			
			message.append(iotFunctionalitiesStr);
			/*if (iotsConnectedSize != connectedIots.size()) {
				//TIMEOUT MESSAGE RECEIVED
				return true;
			}*/
			return true;
		}
		else {
			message.append(ProtocolFacade.runGeneralServerRequest(
					option, connectedIots, iotIndex, sender, receiver));
			return false;
		}
	}
	
	public String executeIotSpecificFunctionality(int iotIndex, String functionality) {
		/*String[] iotFunctionalities = iotFunctionalitiesStr.split(
				Pattern.quote(ProtocolMessage.getSeparator()));
		
		int opt = ui.listIotSpecificFunctionalities(iotFunctionalities);
		if (opt != -1) {
			ui.showMessage(ProtocolFacade.runSpecificIotFunctionality(connectedIots,
					index, sender, receiver, iotFunctionalities[opt]));
		}*/
		return ProtocolFacade.runSpecificIotFunctionality(connectedIots, 
				iotIndex, sender, receiver, functionality);
	}
}
