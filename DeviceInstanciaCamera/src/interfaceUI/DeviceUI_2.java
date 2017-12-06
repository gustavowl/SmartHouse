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
import java.lang.Object;
import org.opencv.*;
import org.opencv.core.CvType;
import org.opencv.highgui.VideoCapture;
import org.opencv.ml.CvNormalBayesClassifier;;

public class DeviceUI_2 extends JFrame{

	private JFrame frame;
	private JanelaLampada lampada;
	public static int interval;
	private ConnectedDevices connectedD;
	private AddDevice addDevice;
	private SobreDevice sobreDevice;
	DefaultListModel model;
	private JButton btnRemoveDevice;
	private JButton btnsobre;
	private JButton btnToggleOnoff;
	private JButton btnSetTimer;
	private JButton btnWattage;
	private JList<String> jlist;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DeviceUI_2 window = new DeviceUI_2();
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public DeviceUI_2() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initialize() {
		model = new DefaultListModel();
		addDevice = new AddDevice();
		sobreDevice = new SobreDevice();
		addDevice.instancia(this);
		sobreDevice.instancia(this);
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
				verificaBotao();
			}
		});
		btnAddDevice.setBounds(105, 222, 139, 25);
		panel.add(btnAddDevice);
		
		btnRemoveDevice = new JButton("Remove Device");
		btnRemoveDevice.setEnabled(false);
		btnRemoveDevice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rmvDevice();
				verificaBotao();
			}
		});
		btnRemoveDevice.setBounds(105, 259, 139, 25);
		panel.add(btnRemoveDevice);
		
		jlist = new JList<String>();
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
		
		jlist.setModel(model);
		jlist.setBounds(106, 42, 130, 154);
		panel.add(jlist);
		
		btnToggleOnoff = new JButton("ON/OFF");
		btnToggleOnoff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toggleOnOff();
				
			}
		});
		btnToggleOnoff.setBounds(260, 40, 117, 25);
		panel.add(btnToggleOnoff);
		
		btnSetTimer = new JButton("Set Timer");
		btnSetTimer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setTimer(6);
			}
		});
		btnSetTimer.setBounds(260, 100, 117, 25);
		panel.add(btnSetTimer);
		
		btnWattage = new JButton("Wattage");
		btnWattage.setBounds(260, 160, 117, 25);
		panel.add(btnWattage);
		
		btnsobre = new JButton("Sobre");
		btnsobre.setEnabled(false);
		btnsobre.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sobreDevice.setUi();
				sobreDevice.setVisible(true);
			}
		});
		btnsobre.setBounds(260, 210, 117, 25);
		panel.add(btnsobre);
		
		JButton btnIamgemvideo = new JButton("Iamgem/Video");
		btnIamgemvideo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				VideoCapture videoCapture = new VideoCapture(0);
				
			}
		});
		btnIamgemvideo.setBounds(260, 259, 117, 25);
		panel.add(btnIamgemvideo);
		verificaBotao();

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
		
	}
	
	public void rmvDevice() {
		for(Device deviceTemp : connectedD.getDevices()) {
			if(jlist.getSelectedValue().equals(deviceTemp.getNome())) {
				
				//Remove da Jlist
				model.remove(jlist.getSelectedIndex());
				
				//Remove da lista de Devices conectados
				connectedD.getDevices().remove(deviceTemp);
				break;
			}
		}
		
	}
	
	public void addNewDevice(Device dev) {
		connectedD.addDevice(dev);
		//for(Device deviceTemp : connectedD.getDevices()) {
			model.addElement(dev.getNome());
		//}
			
	}
	
	public void verificaBotao() {
		if(model.getSize() > 0) {
			btnRemoveDevice.setEnabled(true);
			btnsobre.setEnabled(true);
			btnToggleOnoff.setEnabled(true);
			btnSetTimer.setEnabled(true);
			btnWattage.setEnabled(true);
		}
		else {
			btnRemoveDevice.setEnabled(false);
			btnsobre.setEnabled(false);
			btnToggleOnoff.setEnabled(false);
			btnSetTimer.setEnabled(false);
			btnWattage.setEnabled(false);
		}
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

	public JList<String> getJlist() {
		return jlist;
	}

	public void setJlist(JList<String> jlist) {
		this.jlist = jlist;
	}

	public ConnectedDevices getConnectedD() {
		return connectedD;
	}

	public void setConnectedD(ConnectedDevices connectedD) {
		this.connectedD = connectedD;
	}
}
