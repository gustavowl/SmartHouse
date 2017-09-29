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

import Connection.ConnectedDevices;
import device.Device;
import javax.swing.JList;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;

public class DeviceUI extends JFrame{

	private JFrame frame;
	private JanelaLampada lampada;
	public static int interval;
	private ConnectedDevices connectedD;
	private AddDevice addDevice;
	DefaultListModel model;
	JList<Device> list;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DeviceUI window = new DeviceUI();
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
	public DeviceUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initialize() {
		list = new JList<Device>();
		model = new DefaultListModel();
		addDevice = new AddDevice();
		addDevice.instancia(this);
		connectedD = new ConnectedDevices();
		lampada = new JanelaLampada();
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		JButton btnAddDevice = new JButton("Add Device");
		btnAddDevice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Adiciona um Device	
				addDevice();
			}
		});
		btnAddDevice.setBounds(105, 222, 111, 25);
		panel.add(btnAddDevice);
		
		JButton btnRemoveDevice = new JButton("Remove Device");
		btnRemoveDevice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rmvDevice();
			}
		});
		btnRemoveDevice.setBounds(260, 222, 139, 25);
		panel.add(btnRemoveDevice);
		
		JList<Device> list = new JList<Device>();
		/*list.setModel(new AbstractListModel() {
			String[] values = new String[] {"Lampada",
					"Device 2", "Device 3", "Device 4", "Device 5", "Device 6", "Device 7 ", "Device 8"};
			for(Device dTemp : connectedD) {
				values.a
			}
			public int getSize() {
				return values.length;
			}
			public Object getElementAt(int index) {
				return values[index];
			}
		});*/
		
		list.setModel(model);
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
	
	public void addDevice() {
		//addDevice.setVisible(true);
		//connectedD.addDevice(d);
		/*if((addDevice.getConnectedD() != null) ) {
			for(Device d : addDevice.getConnectedD().getDevices()) {
				connectedD.addDevice(d);
			}
		}*/
		//INSTANCIA A NOVA JANELA
		addDevice.setVisible(true);
		System.out.println(list.getSelectedIndex());
		
	}
	
	public void rmvDevice() {
		for(Device deviceTemp : connectedD.getDevices()) {
			if(model.getElementAt(list.getSelectedIndex()).equals(deviceTemp.getNome())) {
				model.remove(list.getSelectedIndex());
				connectedD.getDevices().remove(deviceTemp);
			}
		}
	}
	
	public void addNewDevice(Device dev) {
		connectedD.addDevice(dev);
		//for(Device deviceTemp : connectedD.getDevices()) {
			model.addElement(dev.getNome());
		//}
			
	}

	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	public DefaultListModel getModel() {
		return model;
	}

	public void setModel(DefaultListModel model) {
		this.model = model;
	}
	
	
	
	
}
