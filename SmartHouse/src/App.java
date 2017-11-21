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
	
	public void selectIOT() {
		boolean finish = false;
		while (!finish) {
			//TODO: Transfer to GUI
			System.out.println("Select an IOT from the following list:\n0 - Quit");
			int opt = 1;
			for (AppIOTDevice iot : connectedIots) {
				System.out.println(Integer.toString(opt) + " - " + iot.getName());
			}
			
			opt = scan.nextInt();
			if (opt > 0 && opt <= connectedIots.size()) {
				finish = listSelectIotOptions(opt - 1);
			}
			else if (opt == 0) {
				finish = true;
			}
			else {
				System.out.println("Please enter a valid option\n-----------------");
			}
		}
	}
	
	//@returns true if "quit/return" option selected
	//@returns false if operation involving iot was selected
	private boolean listSelectIotOptions(int index) {
		int opt = 0;
		while (true) {
			//TODO: Transfer to GUI
			//prints requests
			String[] options = ProtocolFacade.getValidServerRequestsDescriptions();
			int i = 0;
			for (; i < options.length; i++) {
				System.out.println((i + 1) + " - " + options[i]);
			}
			
			//reads and validates option
			opt = scan.nextInt();
			if (opt > 0 && opt <= options.length) {
				opt--;
				if (ProtocolFacade.isGetDevicesFunctionalities(opt)) {
					listSelectIotFunctionalities(opt, index);
				}
				else {
					//TODO: send to GUI
					System.out.println(ProtocolFacade.runGeneralServerRequest(
							opt, connectedIots, index, sender, receiver));
				}
				opt++;
				break;
			}
			else if (opt == 0) {
				//isOptionInvalid = false;
				break;
			}
			System.out.println("Please enter a valid option\n-----------------");
			
		}
		return opt == 0;
	}
	
	private void listSelectIotFunctionalities(int codeIndex, int index) {
		int iotsConnectedSize = connectedIots.size();
		String iotFunctionalitiesStr = ProtocolFacade.runGeneralServerRequest(codeIndex, connectedIots,
				index, sender, receiver);
		if (iotsConnectedSize == connectedIots.size()) {
			String[] iotFunctionalities = iotFunctionalitiesStr.split(
					Pattern.quote(ProtocolMessage.getSeparator()));
			boolean finished = false;
			
			while (!finished) {
				//Print options
				System.out.println("0 - Quit");
				for (int i = 0; i < iotFunctionalities.length; i++) {
					System.out.println((i + 1) + " - " + iotFunctionalities[i]);
				}
				
				int opt = scan.nextInt();
				if (opt >= 0 && opt <= iotFunctionalities.length) {
					finished = true;
					System.out.println(ProtocolFacade.runSpecificIotFunctionality(connectedIots,
							index, sender, receiver, iotFunctionalities[opt - 1]));
					break;
				}
				System.out.println("\"" + opt + "\" is an invalid option");
			}
		}
		else {
			//TIMEOUT MESSAGE RECEIVED
			System.out.println(iotFunctionalitiesStr);
		}
	}
}
