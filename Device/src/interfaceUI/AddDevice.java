package interfaceUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

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
	JComboBox<String> comboBox;
	javax.swing.JFrame jframe;
	DeviceUI ui;
	private ArrayList<Device> devicesNaRede;

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
		
		JLabel lblNome = new JLabel("Nome:");
		lblNome.setBounds(88, 41, 65, 15);
		panel.add(lblNome);
		
		JLabel lblTipo = new JLabel("Devices na Rede:");
		lblTipo.setBounds(12, 90, 141, 15);
		panel.add(lblTipo);
		
		comboBox = new JComboBox();
		comboBox.setBounds(156, 85, 160, 24);
		
		//Antes era um comboBox com os tipos de devices,
		//agora que é framework, vao ser os devices da rede
		/*for(String d : connectedD.DEVICESSUPPORTED) {
			comboBox.addItem(d);
		}*/
		
		Device camera10 = new Camera();
		Device thermostat20 = new Thermostat();
		Device lampada30 = new Lampada();
		
		devicesNaRede = new ArrayList<Device>();
		devicesNaRede.add(camera10);
		devicesNaRede.add(thermostat20);
		devicesNaRede.add(lampada30);
		 
		for(Device d : devicesNaRede) {
			comboBox.addItem(d.getNome());
		}
		panel.add(comboBox);
		
		textField = new JTextField();
		textField.setBounds(156, 39, 160, 19);
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
				dispose();
			}
		});
		btnCancelar.setBounds(258, 211, 117, 25);
		panel.add(btnCancelar);
		
		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Device refreshSimulator = new WallSocket();
				refreshSimulator.setNome("Refresh Simulator");
				devicesNaRede.add(refreshSimulator);
				comboBox.addItem(refreshSimulator.getNome());
			}
		});
		btnRefresh.setBounds(156, 138, 101, 25);
		panel.add(btnRefresh);
		
		
	}
	
	public ConnectedDevices getConnectedD() {
		return connectedD;
	}

	public void setConnectedD(ConnectedDevices connectedD) {
		this.connectedD = connectedD;
	}
	
	public void addDevice() {
		Device deviceTemp = null;
		int indexCombo = 0;
		try {
			for(Device d : devicesNaRede) {
				if(comboBox.getSelectedItem().equals(d.getNome())) {
					d.setNome(textField.getText());
					deviceTemp = d;
					indexCombo = comboBox.getSelectedIndex();
				}
			}
			boolean existeNaLista = false;
			for(Device d : connectedD.getDevices()) {
				if(deviceTemp.getNome().equals(d.getNome())) {
					existeNaLista = true;
				}
					
			}
			
			if(!existeNaLista) {
				adicionaDevice(deviceTemp);
				devicesNaRede.remove(deviceTemp);
				comboBox.removeItemAt(indexCombo);
			}
		}
		catch (Exception e) {
			new ErrorWindow(true);
			// TODO: handle exception
		}
	}
	
	/*Funcao usada antes de ser um FrameWork*/
	/*public void addDevice() {
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
		
		//Verifica se ja existe vom esse nome
		boolean existeNaLista = false;
		for(Device deviceTemp : connectedD.getDevices()) {
			if(deviceTemp.getNome().equals(d.getNome())) {
				existeNaLista = true;
			}
				
		}
		
		if(!existeNaLista) {
			adicionaDevice(d);
		}
		
		
		
		//Determinar um jeito p botar Ids diferentes
		//d.setId();
		

	}	*/
	
	public void instancia(javax.swing.JFrame jframe) {
		this.jframe = jframe;
	}
	
	public void adicionaDevice(Device d) {
		ui = (DeviceUI) this.jframe;
		connectedD.addDevice(d);
		ui.addNewDevice(d);
		textField.setText("");
		ui.verificaBotao();
		
		
	}
}
