import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class App {
	ArrayList<AppIOTDevice> connectedIots;
	private ArrayList<AppIOTDevice> iotsDiscovered;
	private Scanner scan;
	private ProtocolFacade protocol;
	private ReceiverSocket receiver;
	private SenderSocket sender;
	private ThreadWriter  tw;
	private UserInterface ui;
	
	public App() {
		connectedIots = new ArrayList<AppIOTDevice>();
		scan = new Scanner(System.in);
		protocol = new ProtocolFacade();
		receiver = new ReceiverSocket();
		sender = new SenderSocket();
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
		tw = new ThreadWriter("Writer");
		tw.start();
	}
	
	public void discoveryFinish(int iotIndex) {			
		protocol.IOTDiscoveryStop();
		tw.interrupt();
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
	
	class ThreadWriter extends Thread {
		//TODO: transfer to GUI
		volatile boolean finish = false;
		
		public ThreadWriter(String name) {
			super(name);
		}
		
		@Override
		public void run() {
			int i = 0;
			ArrayList<IOTDevice> iots;
			//STEP 3
			while (!finish) {
				iots = protocol.getIotsDiscovered();
				for (; iots != null && iots.size() > 0; i++) {
					AppIOTDevice iot = (AppIOTDevice)iots.remove(0);
					ui.showNewDiscoveredIot(iot.getName());
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
	
	public void executeIotStandardFunctionality(int iotIndex, int option) {
		if (ProtocolFacade.isGetDevicesFunctionalities(option)) {
			listSelectIotFunctionalities(option, iotIndex);
		}
		else {
			//TODO: send to GUI
			ui.showMessage(ProtocolFacade.runGeneralServerRequest(
					option, connectedIots, iotIndex, sender, receiver));
		}
	}
	
	private void listSelectIotFunctionalities(int codeIndex, int index) {
		int iotsConnectedSize = connectedIots.size();
		String iotFunctionalitiesStr = ProtocolFacade.runGeneralServerRequest(codeIndex, connectedIots,
				index, sender, receiver);
		if (iotsConnectedSize == connectedIots.size()) {
			String[] iotFunctionalities = iotFunctionalitiesStr.split(
					Pattern.quote(ProtocolMessage.getSeparator()));
			
			int opt = ui.listIotSpecificFunctionalities(iotFunctionalities);
			if (opt != -1) {
				ui.showMessage(ProtocolFacade.runSpecificIotFunctionality(connectedIots,
						index, sender, receiver, iotFunctionalities[opt]));
			}
			
		}
		else {
			//TIMEOUT MESSAGE RECEIVED
			ui.showErrorMessage(iotFunctionalitiesStr);
		}
	}
}
