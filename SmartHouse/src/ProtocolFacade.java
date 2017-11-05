import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;

public class ProtocolFacade {
	
	Protocol protocol;
	
	public ProtocolFacade() {
		protocol = new Protocol();
	}
	
	public void IOTDiscoveryStart(ReceiverSocket receiver, SenderSocket sender) {
		protocol.discoverIot(true, false, receiver, sender);
	}
	
	public void IOTDiscoveryStop() {
		protocol.discoverIot(false, true, null, null);
	}
	
	public InetSocketAddress ServerDiscoveryStart(ReceiverSocket receiver,
			SenderSocket sender, String iotId) {
		return protocol.discoverServer(receiver, sender, iotId);
	}
	
	public ArrayList<IOTDevice> getIotsDiscovered() {
		return protocol.getAndClearIotsFound();
	}
	
	public void confirmConnection(InetAddress address, SenderSocket sender) {
		protocol.confirmDiscoveredIotConnection(address, sender);
	}
	
	/* Denies connection using unicast only. Does not deny in broadcast because UDP can 
	 * change the order of the messages. Henceforth, the "CONFIRM_CONNECTION" message 
	 * may arrive after the "DENY_CONNECTION" message
	 */
	public void denyConnection(InetAddress address, SenderSocket sender) {
		protocol.denyDiscoveredIotConnection(address, sender);
	}
	
	/* Denies connection in broadcast. Not implemented because UDP can change the order
	 * of the messages. Henceforth, the "CONFIRM_CONNECTION" message may arrive after the
	 * "DENY_CONNECTION" message
	public void denyConnection(SenderSocket sender) {
		protocol.denyDiscoveredIotConnection(sender);
	}*/
	
	public static int getStandardServerReceiverPort() { 
		return Protocol.SERVER_RECEIVER_PORT;
	}
	
	public static int getStandardServerSenderPort() {
		return Protocol.SERVER_SENDER_PORT;
	}
	
	public static int getStandardIotReceiverPort() { 
		return Protocol.IOT_RECEIVER_PORT;
	}
	
	public static int getStandardIotSenderPort() {
		return Protocol.IOT_SENDER_PORT;
	}

	public String listenToServerRequests(InetAddress serverAddress, ReceiverSocket receiver) {
		String code = null;
		while (code == null) {
			code = protocol.listenToServerRequests(serverAddress, receiver);
		}
		return code; 
	}
	
	public void answerServerRequest(String code, InetAddress serverAddress, ReceiverSocket receiver, 
			SenderSocket sender, ArrayList<String> iotFacadeMethods) {
		int opt = -1;
		for (int i = 0; i < Protocol.VALID_SERVER_REQUESTS.length; i++) {
			if (code.equals(Protocol.VALID_SERVER_REQUESTS[i])) {
				opt = i;
				break;
			}
		}
		switch (opt) {
			case 0: //"DSCNCT_IOT"
				System.out.println("IOT DISCONNECT FROM SERVER");
				Protocol.iotDisconnectFromServer(serverAddress, sender);
				break;
			case 1: //"CHCK_UPDT"
				break;
			case 2: //"UPDATE"
				break;
			case 3: //"GETFUNCLST"
				System.out.println("SENDING IOT FUNCTIONALITIES TO SERVER");
				Protocol.iotSendFunctionalitiesToServer(serverAddress, receiver, sender, iotFacadeMethods);
				break;
		}
	}
	
	public static String[] getValidServerRequestsDescriptions() {
		return Protocol.VALID_SERVER_REQUESTS_DESCRIPTIONS;
	}
	
	public static boolean isGetDevicesFunctionalities(int index) {
		return index == 3;
	}
	
	//disconnect, update, check for updates and Get list of device's specific functionalities
	public static String runGeneralServerRequest(int requestIndex, ArrayList<AppIOTDevice> 
		connectedIots, int iotIndex, SenderSocket sender, ReceiverSocket receiver) {
		
		switch (requestIndex) {
			case 0: //"DSCNCT_IOT"
				byte[] msgByte = Protocol.serverRequestDisconnectIot(connectedIots.get(iotIndex).getAddress(),
						sender, receiver);

				if (ProtocolMessage.getMessageCode(msgByte).equals("TIMEOUT")) {
					return iotTimeout(connectedIots, iotIndex); 
				}
				
				AppIOTDevice iot = connectedIots.remove(iotIndex);
				return "The device \"" + iot.getName() + "\" was removed from the list of connected devices";
				
			case 1: //"CHCK_UPDT"
				return "";
			case 2: //"UPDATE"
				return "";
			case 3: //"GETFUNCLST"
				msgByte = Protocol.serverRequestIotFunctionalities(connectedIots.get(iotIndex).getAddress(),
						receiver, sender);
				
				if (ProtocolMessage.getMessageCode(msgByte).equals("TIMEOUT")) {
					return iotTimeout(connectedIots, iotIndex); 
				}
				
				return ProtocolMessage.getMessageContent(msgByte);
		}
		return "Not a general server request.";
	}
	
	private static String iotTimeout(ArrayList<AppIOTDevice> connectedIots, int index) {
		AppIOTDevice iot = connectedIots.remove(index);
		System.out.println("TODO: REMOVE EH O FRESQUE");
		return "The device " + iot.getName() +
				" did not respond. Request timeout." +
				"\nDevice removed from the list of connected devices";
	}

	public static boolean isDisconnectRequest(String code) {
		return code.equals(Protocol.VALID_SERVER_REQUESTS[0]);
	}
}
