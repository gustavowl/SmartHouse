package testeCam;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.sun.media.ui.ComboBox;

import Interfaces.ConexaoMidia;
import device.Camera;
import device.Device;

import javax.media.CannotRealizeException;
import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.Clock;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.swing.JButton;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;

public class VerMidia extends JFrame {

	private JPanel contentPane;
	JPanel panel = new JPanel();
	protected String dispositivoSelecionado;
	protected Player player;
	protected Component comp;
	protected JComboBox comboBox;
	private Camera cameraMidia;

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
		
		cameraMidia = new Camera();
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
				cameraMidia.conexaoAudio(dispositivoSelecionado = "" + comboBox.getSelectedIndex(), player);
			}
		});
		
		btnLigar.setBounds(36, 57, 117, 25);
		contentPane.add(btnLigar);
		
		//cameraMidia.getMidiasDisponiveis();
		//Pega os dispositivos detectados no PC e colocar num Vector 
	/*	for(int i = 0; i<cameraMidia.getMidiasDisponiveis().size(); i++) {
			
			if(nomeDispositivo.indexOf("image") != -1 || nomeDispositivo.indexOf("Image") == -1) {
				comboBox.addItem(nomeDispositivo);
			}
		}*/
		
		for(String nomeDTemp : cameraMidia.getMidiasDisponiveis()) {
			comboBox.addItem(nomeDTemp);
		}
		comboBox.setSelectedIndex(0);
		
		
		panel.setBounds(32, 94, 358, 172);
		contentPane.add(panel);
		
	}

	/*@Override
	public void conexaoImagem() {
		// TODO Auto-generated method stub
		
	}*/

	/*@Override
	public void conexaoAudio() {
		dispositivoSelecionado = "" + comboBox.getSelectedIndex();
		CaptureDeviceInfo device = CaptureDeviceManager.getDevice(dispositivoSelecionado);
		
		MediaLocator localizador = device.getLocator();
		try {
			player = Manager.createRealizedPlayer(localizador);
		} catch (NoPlayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CannotRealizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		player.start();
		
		if((comp = player.getVisualComponent()) != null) {
				panel.add(comp, BorderLayout.CENTER);
		}
		
	}*/

/*	@Override
	public void conexaoFilme() {
		// TODO Auto-generated method stub
		
	}*/
	
	public void setDeviceMedia(Camera camera) {
		cameraMidia = camera;
	}
}
