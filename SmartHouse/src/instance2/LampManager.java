package instance2;

import framework.IOT_IOTDevice;
import framework.Receiver;
import framework.Sender;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Label;
import java.util.ArrayList;
import javax.swing.JFrame;

public class LampManager extends IOT_IOTDevice {
	
	Lamp[] lamps;
	JFrame[] frames;
	
	public LampManager() {
		this(4);
	}
	
	public LampManager(int numLamps) {
		lamps = new Lamp[numLamps];
		frames = new JFrame[numLamps];
		for (int i = 0; i < numLamps; i++) {
			lamps[i] = new Lamp();
			lamps[i].start();
			lamps[i].setRed(255);
			lamps[i].setGreen(255);
			lamps[i].setBlue(255);
			lamps[i].setOn(true);
			lamps[i].setStrob_time_ms(100);
			
			frames[i] = new JFrame("Lamp " + i);
			frames[i].setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frames[i].getContentPane().add(new Label(), BorderLayout.CENTER);
			frames[i].setSize(200, 200);
			frames[i].setLocation(frames[i].getWidth() * i, 0);
			frames[i].getContentPane().setBackground(Color.BLACK);
		}
	}
	
	class ThreadFrame extends Thread {
		
		int index;
		
		public ThreadFrame(int index) {
			this.index = index;
		}
		
		@Override
		public void run() {
			while (true) {				
				frames[index].getContentPane().removeAll();
				frames[index].getContentPane().add(new Label(), BorderLayout.CENTER);
				frames[index].getContentPane().setBackground(
						new Color(
								lamps[index].getRed(),
								lamps[index].getGreen(),
								lamps[index].getBlue()
						));
				System.out.println(frames[index].getContentPane().getBackground());
			}
		}
	}
	
	public void run() {
		for (int i = 0; i < frames.length; i++) {
			frames[i].setVisible(true);
			ThreadFrame tf = new ThreadFrame(i);
			tf.start();
		}
		/*while (true) {
			System.out.println(lamps[0].getRed() + ", " + lamps[0].getGreen() + "," + lamps[0].getBlue());
		}*/
	}

	@Override
	protected Receiver initReceiver() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Sender initSender() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<String> getFacadeMethods() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void executeFacadeMethod(String methodSignature, String[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getIotStatus() {
		// TODO Auto-generated method stub
		return null;
	}
}
