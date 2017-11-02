import java.util.ArrayList;
import java.util.Scanner;

public class App {
	ArrayList<AppIOTDevice> connectedIots;
	private ArrayList<AppIOTDevice> iotsDiscovered;
	private Scanner scan;
	private ProtocolFacade protocol;
	private ReceiverSocket receiver;
	private SenderSocket sender;
	
	public App() {
		connectedIots = new ArrayList<AppIOTDevice>();
		scan = new Scanner(System.in);
		protocol = new ProtocolFacade();
		receiver = new ReceiverSocket(ProtocolFacade.getStandardServerReceiverPort());
		sender = new SenderSocket(ProtocolFacade.getStandardServerSenderPort());
	}
	
	//TODO: extends runnable?
	public void run() {
		boolean finish = false;
		int opt;
		while (!finish) {
			System.out.println("------------------------------------");
			System.out.println("This is the Smart House app!");
			System.out.println("Please, choose the desisred action:");
			System.out.println("1 - Discover new IoTs");
			System.out.println("2 - Select IoT");
			System.out.println("3 - Quit app");
			
			opt = scan.nextInt();
			
			switch(opt) {
				case 1:
					discover();
					break;
				case 2:
					selectIOT();
					break;
				case 3:
					System.out.println("Goodbye! We hope to see you soon enough.");
					finish = true;
					break;
				default: 
					System.out.println("That is an invalid option!");
					break;
			}
		}
		scan.close();
	}
	
	public void discover() {
		/* Protocol outline:
		 * 1 - Sends broadcast message in order to discover new devices
		 * 2 - Receives messages from multiple devices
		 * 3 - Prints available device list to connect
		 * 4 - Sends message confirming or denying connection to devices
		 * 5 - Adds IoT to list of recognized devices
		 */
		protocol.IOTDiscoveryStart(receiver, sender);
		iotsDiscovered = new ArrayList<AppIOTDevice>();
		ThreadWriter tw = new ThreadWriter("Writer");
		tw.start();
		
		int opt = scan.nextInt();
		while ( !(opt > 0 && opt <= iotsDiscovered.size()) ) {
			System.out.println("INVALID OPTION. Range: " + 0 + " - " + 
					iotsDiscovered.size());
			opt = scan.nextInt();
		}
			
		protocol.IOTDiscoveryStop();
		tw.interrupt();
		if (opt > 0 ) {
			AppIOTDevice iot = iotsDiscovered.get(opt - 1);
			
			protocol.confirmConnection(iot, sender); //STEP 4
			connectedIots.add(iot); //STEP 5
		}
		iotsDiscovered.clear();
		iotsDiscovered = null;
	}
	
	class ThreadWriter extends Thread {
		//TODO: transfer to GUI
		boolean finish = false;
		
		public ThreadWriter(String name) {
			super(name);
		}
		
		@Override
		public void run() {
			int i = 0;
			ArrayList<IOTDevice> iots;
			//STEP 3
			System.out.println("0 - Quit");
			while (!finish) {
				iots = protocol.getIotsDiscovered();
				for (; iots != null && iots.size() > 0; i++) {
					AppIOTDevice iot = (AppIOTDevice)iots.remove(0);
					System.out.println(Integer.toString(i+1) + " - " + 
							iot.getName() + Integer.toString(i));
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
	
	private void selectIOT() {
		System.out.println("Select an IOT from the following list:\n0 - Quit");
		int i = 1;
		for (AppIOTDevice iot : connectedIots) {
			System.out.println(Integer.toString(i) + " - " + iot.getName());
		}
		System.out.println("TODO: IMPLEMENT THIS");
	}
}
