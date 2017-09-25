package InterfaceG;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Main extends JFrame {

	private JPanel contentPane;
	private JLabel label = new JLabel("");

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
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
	public Main() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnLigar = new JButton("Ligar");
		btnLigar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Image img = new ImageIcon(this.getClass().getResource("/icons8-Luz acesa-50.png")).getImage();
				label.setIcon(new ImageIcon(img));
			}
		});
		btnLigar.setBounds(95, 200, 117, 25);
		contentPane.add(btnLigar);
		
		JButton btnDesligafr = new JButton("Desligar");
		btnDesligafr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Image img = new ImageIcon(this.getClass().getResource("/icons8-Luz apagada-40.png")).getImage();
				label.setIcon(new ImageIcon(img));
			}
		});
		btnDesligafr.setBounds(254, 200, 117, 25);
		contentPane.add(btnDesligafr);
		
		Image img = new ImageIcon(this.getClass().getResource("/icons8-Luz apagada-40.png")).getImage();
		label.setIcon(new ImageIcon(img));
		label.setBounds(207, 63, 68, 99);
		contentPane.add(label);
	}
}
