
public final class ProtocolMessage {
	private static final String SEPARATOR = "|";
	
	public static String createMessage(String code, String content) {
		if (code.length() > 0 && code.length() <= 10 && !code.contains(SEPARATOR)) {
			return code + SEPARATOR + content;
		}
		return null;
	}
	
	public static String getMessageCode(String message) {
		int index = message.indexOf(SEPARATOR);
		return message.substring(0, index);
	}
	
	public static String getMessageContent(String message) {
		int index = message.indexOf(SEPARATOR);
		return message.substring(index + 1).trim();		
	}
	
	public static String getSeparator() {
		return SEPARATOR;
	}
}
