package interfaceUI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;

import device.Device;
import javax.swing.JList;
import javax.swing.AbstractListModel;

public class MainUI {

	private JFrame frame;
	private ArrayList<Device> devices = new ArrayList<Device>();
	private Lampada lampada;
	public static int interval;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainUI window = new MainUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		lampada = new Lampada();
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		JButton btnAddDevice = new JButton("Add Device");
		btnAddDevice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnAddDevice.setBounds(105, 222, 111, 25);
		panel.add(btnAddDevice);
		
		JButton btnRemoveDevice = new JButton("Remove Device");
		btnRemoveDevice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnRemoveDevice.setBounds(260, 222, 139, 25);
		panel.add(btnRemoveDevice);
		
		JList list = new JList();
		list.setModel(new AbstractListModel() {
			String[] values = new String[] {"Lampada", "Device 2", "Device 3", "Device 4", "Device 5", "Device 6", "Device 7 ", "Device 8"};
			public int getSize() {
				return values.length;
			}
			public Object getElementAt(int index) {
				return values[index];
			}
		});
		list.setBounds(106, 42, 130, 154);
		panel.add(list);
		
		JButton btnToggleOnoff = new JButton("ON/OFF");
		btnToggleOnoff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toggleOnOff();
				
			}
		});
		btnToggleOnoff.setBounds(260, 40, 117, 25);
		panel.add(btnToggleOnoff);
		
		JButton btnSetTimer = new JButton("Set Timer");
		btnSetTimer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setTimer(6);
			}
		});
		btnSetTimer.setBounds(260, 100, 117, 25);
		panel.add(btnSetTimer);
		
		JButton btnWattage = new JButton("Wattage");
		btnWattage.setBounds(260, 160, 117, 25);
		panel.add(btnWattage);

	}
	
	public void setTimer(int timed) {
		
        interval = timed;
        int delay = 1000;
        int period = 1000;
        final Timer time = new Timer();
        System.out.println(interval);
        time.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                if (interval == 0) {
                    System.out.println("work finished");
                    time.cancel();
                    time.purge();
                    toggleOnOff();
                    
                } else {
                    System.out.println(setInterval());
                }
            }
        }, delay, period);
    }
	
	private static int setInterval() {

        return --interval;
    }
	
	public void toggleOnOff( ) {
		lampada.setVisible(true);
		if(lampada.isLigada())
			lampada.desligar();
		else
			lampada.ligar();
	}
}
