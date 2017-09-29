package interfaceUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import Connection.ConnectedDevices;
import device.Camera;
import device.Device;
import device.Lampada;
import device.Tag;
import device.Thermostat;
import device.WallSocket;

import javax.swing.JLabel;

public class AddDevice extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private ConnectedDevices connectedD;
	JComboBox comboBox;
	javax.swing.JFrame jframe;
	DeviceUI ui;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AddDevice frame = new AddDevice();
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
	public AddDevice() {
		connectedD = new ConnectedDevices();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(600, 200, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		contentPane.add(panel, BorderLayout.CENTER);
		
		JLabel lblNome = new JLabel("Nome");
		lblNome.setBounds(57, 41, 70, 15);
		panel.add(lblNome);
		
		JLabel lblTipo = new JLabel("Tipo");
		lblTipo.setBounds(57, 90, 70, 15);
		panel.add(lblTipo);
		
		comboBox = new JComboBox();
		comboBox.setBounds(161, 85, 114, 24);
		for(String d : connectedD.DEVICESSUPPORTED) {
			comboBox.addItem(d);
		}
		panel.add(comboBox);
		
		textField = new JTextField();
		textField.setBounds(161, 39, 114, 19);
		panel.add(textField);
		textField.setColumns(10);
		
		JButton btnSalvar = new JButton("Salvar");
		btnSalvar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addDevice();
			}
		});
		btnSalvar.setBounds(97, 211, 117, 25);
		panel.add(btnSalvar);
		
		JButton btnCancelar = new JButton("Fechar");
		btnCancelar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnCancelar.setBounds(258, 211, 117, 25);
		panel.add(btnCancelar);
		
		
	}
	
	public ConnectedDevices getConnectedD() {
		return connectedD;
	}

	public void setConnectedD(ConnectedDevices connectedD) {
		this.connectedD = connectedD;
	}
	
	public void addDevice() {
		Device d = null;
		
		if(comboBox.getSelectedIndex() == 0)
			d = new Camera();
		if(comboBox.getSelectedIndex() == 1)
			d = new Lampada();
		if(comboBox.getSelectedIndex() == 2)
			d = new Tag();
		if(comboBox.getSelectedIndex() == 3)
			d = new Thermostat();
		if(comboBox.getSelectedIndex() == 4)
			d = new WallSocket();
		d.setNome(textField.getText());
		
		for(Device deviceTemp : connectedD.getDevices()) {
			if(!deviceTemp.getNome().equals(d.getNome())) {
				adicionaDevice(d);
			}
				
		}
		
		if(connectedD.getDevices().size() == 0) {
			adicionaDevice(d);
		}
		
		
		
		//Determinar um jeito p botar Ids diferentes
		//d.setId();
		

	}	
	
	public void instancia(javax.swing.JFrame jframe) {
		this.jframe = jframe;
	}
	
	public void adicionaDevice(Device d) {
		ui = (DeviceUI) this.jframe;
		connectedD.addDevice(d);
		ui.addNewDevice(d);
		textField.setText("");
		
		
	}

}
