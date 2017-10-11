import java.util.Scanner;

public class LocalSenderSocket extends SenderSocket {
	private Scanner scan;
	
	LocalSenderSocket() {
		super();
		scan = new Scanner(System.in);
	}
	
	LocalSenderSocket(int port) {
		super(port);
		scan = new Scanner(System.in);
	}
	
	LocalSenderSocket(String address) {
		super(address);
		scan = new Scanner(System.in);
	}
	
	LocalSenderSocket(String address, int port) {
		super(address, port);
		scan = new Scanner(System.in);
	}
	
	void writeAndSendData() {
		System.out.print("Type destination address: ");
		String address = scan.next();
		System.out.println(address);
		System.out.print("Type destination port: ");
		int port = scan.nextInt();
		System.out.println(port);
		System.out.print("Type data: ");
		String data_str = scan.next();
		System.out.println(data_str);
		byte[] data_byte = data_str.getBytes();
		
		super.sendData(data_byte, address, port);
	}
}
