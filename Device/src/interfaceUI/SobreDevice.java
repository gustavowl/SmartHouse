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

public class SobreDevice extends JFrame {

	private JPanel contentPane;
	javax.swing.JFrame jframe;
	DeviceManager ui;
	private JLabel nomeInfo;
	private JLabel tipoInfo;
	private Device deviceSobre;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SobreDevice frame = new SobreDevice();
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
	public SobreDevice() {
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
		lblNome.setBounds(57, 41, 70, 15);
		panel.add(lblNome);
		
		JLabel lblTipo = new JLabel("Tipo:");
		lblTipo.setBounds(57, 90, 70, 15);
		panel.add(lblTipo);
		
		JButton btnCancelar = new JButton("Fechar");
		btnCancelar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnCancelar.setBounds(145, 185, 117, 25);
		panel.add(btnCancelar);
		
		nomeInfo = new JLabel("");
		nomeInfo.setBounds(183, 41, 147, 15);
		panel.add(nomeInfo);
		
		tipoInfo = new JLabel("");
		tipoInfo.setBounds(183, 90, 147, 15);
		panel.add(tipoInfo);
				
				
	}
	
	public void instancia(javax.swing.JFrame jframe) {
		this.jframe = jframe;
	}
	
	public void setUi() {
		ui = (DeviceManager) this.jframe;
		deviceSobre = null;
		
		//Pega o nome do device selecionado (Pegar por String pq o jList eh de String!)
		String nomeDevice = (String) ui.getModel().getElementAt(ui.getList().getSelectedIndex());
	
		//Pega a Instancia do Device atraves da procura pelo Nome
		for(Device deviceTemp : ui.getConnectedD().getDevices()) {
			if(nomeDevice.equals(deviceTemp.getNome())) {
				
				deviceSobre = deviceTemp;
				break;
			}
		}
				
		
		if(deviceSobre instanceof Camera) {
			deviceSobre.setTipo("Camera");
		}
		if(deviceSobre instanceof Lampada) {
			deviceSobre.setTipo("Lampada");
		}
		if(deviceSobre instanceof Tag) {
			deviceSobre.setTipo("Tag");
		}
		if(deviceSobre instanceof Thermostat) {
			deviceSobre.setTipo("Thermostato");
		}
		if(deviceSobre instanceof WallSocket) {
			deviceSobre.setTipo("WallSocket");
		}
		tipoInfo.setText(deviceSobre.getTipo());
		

		nomeInfo.setText(deviceSobre.getNome());
		tipoInfo.setText(deviceSobre.getTipo());
	}
	
}
