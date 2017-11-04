//import java.net.DatagramPacket;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class SenderSocket {
	private DatagramSocket socket;
	
	SenderSocket() {
		socket = null;
	}
	
	public void close() {
		if (socket != null && !socket.isClosed()) {
			socket.close();
		}
	}
	
	public void open(int port, boolean isBroadcast) {
		if (socket == null || socket.isClosed()) {
			//TODO: verify if port range is valid
			try {
				socket = new DatagramSocket(null);
				socket.bind(new InetSocketAddress(InetAddress.getByName("0.0.0.0"), port));
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
	
	void sendData(byte[] content) {
		sendData(content, "255.255.255.255", 12112);
	}
	
	void sendData(byte[] content, int port) {
		//TODO: check content size before sending
		sendData(content, "255.255.255.255", port);
	}

	void sendData(byte[] content, String address, int port) {
		//TODO: check content size before sending
		try {
			DatagramPacket dp = new DatagramPacket(content, content.length, 
					InetAddress.getByName(address), port);
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
