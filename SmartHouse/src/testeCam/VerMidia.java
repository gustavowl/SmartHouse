package testeCam;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.sun.media.ui.ComboBox;

import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.Clock;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Player;
import javax.swing.JButton;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class VerMidia extends JFrame {

	private JPanel contentPane;
	JPanel panel = new JPanel();
	protected String dispositivoSelecionado;
	protected Player player;
	protected Component comp;
	protected JComboBox comboBox;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VerMidia frame = new VerMidia();
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
	public VerMidia() {
		//Player player = null;
		//comboBox = new JComboBox();
		
	//	Component comp = null;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		comboBox = new JComboBox();
		comboBox.setBounds(36, 12, 354, 24);
		contentPane.add(comboBox);
		comboBox.removeAll();
		
		JButton btnSaida = new JButton("Desligar");
		btnSaida.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				player.stop();
				player.close();
				player.deallocate();
				panel.remove(comp);
			}
		});
		btnSaida.setBounds(180, 57, 117, 25);
		contentPane.add(btnSaida);
		
		JButton btnLigar = new JButton("Reproduzir");
		
		btnLigar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					dispositivoSelecionado = "" + comboBox.getSelectedIndex();
					CaptureDeviceInfo device = CaptureDeviceManager.getDevice(dispositivoSelecionado);
					
					MediaLocator localizador = device.getLocator();
					player = Manager.createRealizedPlayer(localizador);
					player.start();
					
					if((comp = player.getVisualComponent()) != null) {
							panel.add(comp, BorderLayout.CENTER);
					}
				}catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		
		btnLigar.setBounds(36, 57, 117, 25);
		contentPane.add(btnLigar);
		
		//Pega os dispositivos detectados no PC e colocar num Vector 
		Vector listaDispositivos = null;
		listaDispositivos = CaptureDeviceManager.getDeviceList(null);
		for(int i = 0; i<listaDispositivos.size(); i++) {
			CaptureDeviceInfo info = (CaptureDeviceInfo) listaDispositivos.get(i);
			String  nomeDispositivo = info.getName().toString();
			
			if(nomeDispositivo.indexOf("image") != -1 || nomeDispositivo.indexOf("Image") == -1) {
				comboBox.addItem(nomeDispositivo);
			}
		}
		comboBox.setSelectedIndex(0);
		
		
		panel.setBounds(32, 94, 358, 172);
		contentPane.add(panel);
		
	}
}
