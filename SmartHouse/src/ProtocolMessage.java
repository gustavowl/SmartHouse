
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
		String messageStr = new String(message);
		int index = messageStr.indexOf(SEPARATOR);
		return messageStr.substring(0, index);
	}
	
	public static String getMessageContent(byte[] message) {
		String messageStr = new String(message);
		int index = messageStr.indexOf(SEPARATOR);
		return messageStr.substring(index + 1).trim();		
	}
}
