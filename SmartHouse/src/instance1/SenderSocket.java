package instance1;
//import java.net.DatagramPacket;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import framework.*;

public class SenderSocket extends Sender {
	private DatagramSocket socket;
	
	public SenderSocket() {
		super();
		setServerPort("12113");
		setIotPort("12115");
		socket = null;
	}
	
	public void close() {
		if (socket != null && !socket.isClosed()) {
			socket.close();
			setAddress(null);
		}
	}
	
	public void open(String port, boolean isBroadcast) {
		if (socket == null || socket.isClosed()) {
			//TODO: verify if port range is valid
			try {
				socket = new DatagramSocket(null);
				InetSocketAddress isa = new InetSocketAddress(InetAddress.getByName("0.0.0.0"), 
						Integer.parseInt(port));
				socket.bind(isa);
				setAddress(isa.getAddress().getHostAddress());
				socket.setBroadcast(isBroadcast);
			}
			catch (IOException ex) {
				System.out.println("Error: " + ex.toString());
				ex.printStackTrace(System.out);
				System.out.println("socket == null" + socket == null);
				System.out.println("socket.isClosed()" + socket.isClosed());
				System.out.println(socket.getLocalAddress().getHostAddress() + ':' + port);
			}
		}
	}
	
	public void sendData(String content) {
		sendData(content, "255.255.255.255", "12112");
	}
	
	public void sendData(String content, String port) {
		//TODO: check content size before sending
		sendData(content, "255.255.255.255", port);
	}

	public void sendData(String content, String address, String port) {
		//TODO: check content size before sending
		try {
			byte[] contentByte = content.getBytes();
			DatagramPacket dp = new DatagramPacket(contentByte, contentByte.length, 
					InetAddress.getByName(address), Integer.parseInt(port));
			socket.send(dp);
			//FIXME: Delete next line
			//System.out.println("Message sent to " + address + ':' + Integer.toString(port));
		}
		catch (Exception e) {
			System.out.println("Error: " + e.toString());
			e.printStackTrace(System.out);
		}
	}
	
	public boolean isClosed() {
		return socket.isClosed();
	}
	
	//TODO: Destructor? Close socket
	
}
