import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Scanner;

public class App {
	ArrayList<AppIOTDevice> connectedIots;
	private static volatile boolean finished;
	private static volatile ArrayList<DatagramPacket> iotsFound;
	private Scanner scan;
	
	public App() {
		connectedIots = new ArrayList<AppIOTDevice>();
		finished = false;
		scan = new Scanner(System.in);
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
		
		finished = false;
		ReceiverSocket receiver = new ReceiverSocket(12112);
		SenderSocket sender = new SenderSocket(12113);
		ThreadAppDiscover tap = new ThreadAppDiscover("Discover IOTs", receiver, sender);

		//STEP 1				
		byte[] messageByte = "DISCVR_IOT".getBytes();
		sender.sendData(messageByte, 12114);
		iotsFound = new ArrayList<DatagramPacket>();
		int newIot = 0;
		tap.start(); //STEP 2 and 3
		
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
			
		}
		
		finished = true;
		iotsFound = null;
	}
	
	static class ThreadAppDiscover extends Thread {
		ReceiverSocket receiver;
		SenderSocket sender;
		private static ArrayList<String> text;
		
		public ThreadAppDiscover(String name, ReceiverSocket receiver, SenderSocket sender) {
			super(name);
			this.receiver = receiver;
			this.sender = sender;
			text = new ArrayList<String>();
		}
		
		@Override
		public void run() {
			//STEP 2
			ThreadWriter tw = new ThreadWriter("Writer");
			tw.start();
			while (!finished) {
				DatagramPacket dataFromIoT = receiver.receiveData(1, "CANICON_ID").get(0);
				if (!finished) {
					iotsFound.add(dataFromIoT); //STEP 3 and 4
					text.add("ID");
					byte[] messageByte = "CONFRM_IOT".getBytes();
					synchronized (sender) {
						sender.sendData(messageByte, dataFromIoT.getAddress().getHostAddress(),
								dataFromIoT.getPort() - 1);
					}
				}
				synchronized (tw) {
					tw.notify();
				}
			}
			tw.interrupt();
		}
		
		public static synchronized ArrayList<String> getText() {
			return text;
		}
	}
	
	static class ThreadWriter extends Thread {
		public ThreadWriter(String name) {
			super(name);
		}
		
		@Override
		public void run() {
			int i = 0;
			ArrayList<String> text;
			//STEP 3
			System.out.println("0 - Quit");
			while (!finished) {
				try {
					synchronized (this) {
						wait();
					}
				} catch (InterruptedException e) {
					System.out.println(e.toString());
				}
				text = ThreadAppDiscover.getText();
				synchronized (text) {
					for (; i < text.size(); i++) {
						System.out.println(Integer.toString(i+1) + " - ID" + 
								Integer.toString(i));
					}
				}
			}
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
