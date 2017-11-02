
public final class ProtocolMessage {
	private static final String SEPARATOR = "|";
	
	public static byte[] createMessage(String code, String content) {
		byte[] messageByte = null;
		if (code.length() > 0 && code.length() <= 10 && !code.contains(SEPARATOR)) {
			String messageStr = code + SEPARATOR + content;
			messageByte = messageStr.getBytes();
		}
		return messageByte;
	}
	
	public static String getMessageCode(byte[] message) {
		String messageStr = message.toString();
		int index = messageStr.indexOf(SEPARATOR);
		System.out.println(messageStr.substring(0, index));
		return messageStr.substring(0, index);
	}
	
	public static String getMessageContent(byte[] message) {
		String messageStr = message.toString();
		int index = messageStr.indexOf(SEPARATOR);
		System.out.println(messageStr.substring(index + 1));
		return messageStr.substring(index + 1);		
	}
}
