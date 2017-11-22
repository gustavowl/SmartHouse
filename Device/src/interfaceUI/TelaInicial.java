package interfaceUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class TelaInicial extends JFrame {

	private JPanel contentPane;
	private DeviceManager devideM;
	private LogUI logUI;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TelaInicial frame = new TelaInicial();
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
	public TelaInicial() {
		devideM = new DeviceManager();
		logUI = new LogUI();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("TELA INICIAL");
		lblNewLabel.setBounds(182, 24, 100, 33);
		contentPane.add(lblNewLabel);
		
		JButton btnDevices = new JButton("Devices");
		btnDevices.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				devideM.setVisible(true);
			}
		});
		btnDevices.setBounds(156, 91, 141, 25);
		contentPane.add(btnDevices);
		
		JButton btnConfigurao = new JButton("Configuração");
		btnConfigurao.setBounds(156, 141, 141, 25);
		contentPane.add(btnConfigurao);
		
		JButton btnLog = new JButton("Log");
		btnLog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logUI.setVisible(true);
			}
		});
		btnLog.setBounds(156, 191, 141, 25);
		contentPane.add(btnLog);
	}
}
