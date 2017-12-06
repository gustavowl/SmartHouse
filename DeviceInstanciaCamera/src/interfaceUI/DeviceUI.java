package interfaceUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import Connection.ConnectedDevices;
import device.Camera;
import device.Device;
import testeCam.VerMidia;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DeviceUI extends JFrame {

	private JPanel contentPane;
	private JanelaLampada lampada;
	public static int interval;
	private ConnectedDevices connectedD;
	private AddDevice addDevice;
	private SobreDevice sobreDevice;
	private DefaultListModel model;
	private JButton btnOnoff;
	private JButton btnInfo;
	private JButton btnMidia;
	private JButton btnAddDevice;
	private JButton btnRemoveDevice;
	private JList<String> list;
	private VerMidia verMidia;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DeviceUI frame = new DeviceUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public DeviceUI() {
		
		model = new DefaultListModel();
		addDevice = new AddDevice();
		sobreDevice = new SobreDevice();
		addDevice.instancia(this);
		sobreDevice.instancia(this);
		connectedD = new ConnectedDevices();
		lampada = new JanelaLampada();
		verMidia = new VerMidia();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(400, 250, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		btnOnoff = new JButton("On/Off");
		btnOnoff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toggleOnOff();
			}
		});
		btnOnoff.setBounds(270, 50, 117, 25);
		contentPane.add(btnOnoff);
		
		btnInfo = new JButton("Info");
		btnInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sobreDevice.setUi();
				sobreDevice.setVisible(true);
			}
		});
		btnInfo.setBounds(270, 120, 117, 25);
		contentPane.add(btnInfo);
		
		btnMidia = new JButton("Ver Midia");
		btnMidia.setEnabled(false);
		btnMidia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				for(Device deviceTemp : connectedD.getDevices()) {
					if(list.getSelectedValue().equals(deviceTemp.getNome())) {
						if(deviceTemp instanceof Camera) {
							verMidia.setDeviceMedia((Camera) deviceTemp);
							verMidia.setVisible(true);
						}
						
						else {
							new ErrorWindow(true);
						}
						
						
					}
				}
				
				
			}
		});
		btnMidia.setBounds(270, 190, 117, 25);
		contentPane.add(btnMidia);
		
		btnAddDevice = new JButton("Add Device");
		btnAddDevice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addDevice();
				verificaBotao();
			}
		});
		btnAddDevice.setBounds(60, 263, 117, 25);
		contentPane.add(btnAddDevice);
		
		btnRemoveDevice = new JButton("Remove Device");
		btnRemoveDevice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rmvDevice();
				verificaBotao();
			}
		});
		btnRemoveDevice.setBounds(248, 263, 139, 25);
		contentPane.add(btnRemoveDevice);
		//contentPane.add(list);
		JScrollPane scrollPane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
			     ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setLocation(38, 49);
		scrollPane.setSize(169, 202);
		contentPane.add(scrollPane);
		
		list = new JList();
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				verificaBotao();
			}
		});
		scrollPane.setViewportView(list);
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				for(Device d : connectedD.getDevices()) {
					if(d.getNome().equals(list.getSelectedValue())) {
						if(d instanceof Camera) {
							btnMidia.setEnabled(true);
						}
					}
				}
			}
		});
		list.setModel(model);
		
		verificaBotao();

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
			if(list.getSelectedValue().equals(deviceTemp.getNome())) {
				
				//Remove da Jlist
				model.remove(list.getSelectedIndex());
				
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
		if(model.getSize() > 0 && list.getSelectedIndex() != -1) {
			btnRemoveDevice.setEnabled(true);
			btnInfo.setEnabled(true);
			btnOnoff.setEnabled(true);
		}
		
		else {
			btnRemoveDevice.setEnabled(false);
			btnInfo.setEnabled(false);
			btnOnoff.setEnabled(false);
			btnMidia.setEnabled(false);
		}
	}
	
	
	public DefaultListModel getModel() {
		return model;
	}

	public void setModel(DefaultListModel model) {
		this.model = model;
	}

	public JList<String> getList() {
		return list;
	}

	public void setList(JList<String> list) {
		this.list = list;
	}
	
	public ConnectedDevices getConnectedD() {
		return connectedD;
	}

	public void setConnectedD(ConnectedDevices connectedD) {
		this.connectedD = connectedD;
	}

	/*
	 * Funcao que seta o Timer
	 * */
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
}
