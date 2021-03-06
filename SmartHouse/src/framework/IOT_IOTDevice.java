package framework;
import java.util.ArrayList;

//IOTDevice representation as seen by the IOT itself
public abstract class IOT_IOTDevice extends IOTDevice {
	Receiver receiver;
	Sender sender;
	ProtocolFacade protocol;
	
	public IOT_IOTDevice() {
		this("", null);
	}
	
	public IOT_IOTDevice(String name) {
		this(name, null);
	}
	
	public IOT_IOTDevice(String name, String peerAddress) {
		super(null, name);
		receiver = initReceiver();
		//listenerPort = port;
		sender = initSender();
		setPeerAddress(peerAddress);
		//setPeerPort(peerPort);
		protocol = new ProtocolFacade();
	}
	
	public void update() {
		System.out.println("TODO: Implement update method");
	}
	
	public void remove() {
		System.out.println("TODO: Implement update method");
	}
	
	public void discover() {
		while (true) {
			String address = protocol.ServerDiscoveryStart(receiver,
					sender, getName());
			if (address != null) {
				setPeerAddress(address);
				break;
			}
			else {
				//TODO: change to GUI
				System.out.println("IoT device was not recognized by Server. Timeout.");
				continue;
			}
		}
	}
	
	public void listenToPeer() {
		/* Commands available:
		 * 1 - Remove connection with server
		 * 2 - Execute Method
		 * 3 - Get List of Methods
		 * 4 - Update (?)
		 * 5 - Check for updates (?)
		 * 6 - List consumption (?)
		 */
		while (peerAddress != null) {
			String message = protocol.listenToServerRequests(getPeerAddress(), receiver);
			String code = ProtocolMessage.getMessageCode(message);
			String content = "";
			if (ProtocolFacade.isGetStatus(code)) {
				//run get status
				content = getIotStatus(); 
			}
			protocol.answerServerRequest(code, content, getPeerAddress(), receiver,
					sender, getFacadeMethods());
			if (ProtocolFacade.isDisconnectRequest(code)) {
				peerAddress = null;
				//TODO: change to GUI
				System.out.println("Disconnected by the server");
			}
			else if (ProtocolFacade.isRunDeviceFunctionality(code)) {
				//run method
				content = ProtocolMessage.getMessageContent(message);
				String signature = content;
				String[] parameters = new String[0];
				if (content.indexOf('(') > -1) {
					signature = content.substring(0, content.indexOf(")(") + 1);
					content = content.substring(content.indexOf(")(") + 2);
					content = content.substring(0, content.indexOf(')'));
					if (!content.equals("")) {
						parameters = content.split(",");
					}
					this.executeFacadeMethod(signature, parameters);
				}
				//run this
			}
		}
	}
	
	public void run() {
		while (true) {
			System.out.println("Wait for server Discovery request");
			discover();

			System.out.println("Listening for server requests");
			listenToPeer();
		}
	}
	
	protected abstract Receiver initReceiver();
	
	protected abstract Sender initSender();
	
	public abstract ArrayList<String> getFacadeMethods();
	
	public abstract void executeFacadeMethod(String methodSignature, String[] args);
	
	public abstract String getIotStatus();
}
