package interfaceUI;

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
import java.util.Timer;
import java.util.TimerTask;
import java.awt.event.ActionEvent;

public class JanelaLampada extends JFrame {

	private JPanel contentPane;
	private JLabel label = new JLabel("");
	private boolean ligada = false;
	private boolean lampState;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JanelaLampada frame = new JanelaLampada();
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
	public JanelaLampada() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(600, 200, 450, 300);
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
		btnLigar.setVisible(false);
		
		JButton btnDesligafr = new JButton("Desligar");
		btnDesligafr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Image img = new ImageIcon(this.getClass().getResource("/icons8-Luz apagada-40.png")).getImage();
				label.setIcon(new ImageIcon(img));
			}
		});
		btnDesligafr.setBounds(254, 200, 117, 25);
		contentPane.add(btnDesligafr);
		btnDesligafr.setVisible(false);
		
		
		Image img = new ImageIcon(this.getClass().getResource("/icons8-Luz apagada-40.png")).getImage();
		label.setIcon(new ImageIcon(img));
		label.setBounds(207, 63, 68, 99);
		contentPane.add(label);
	}
	
	public void ligar() {
		Image img = new ImageIcon(this.getClass().getResource("/icons8-Luz acesa-50.png")).getImage();
		label.setIcon(new ImageIcon(img));
		ligada = true;
	}
	
	public void desligar() {
		Image img = new ImageIcon(this.getClass().getResource("/icons8-Luz apagada-40.png")).getImage();
		label.setIcon(new ImageIcon(img));
		ligada = false;
	}

	public boolean isLigada() {
		return ligada;
	}

	public void setLigada(boolean ligada) {
		this.ligada = ligada;
	}

	public void setLampState(boolean lampState) {
		if (lampState) {
			ligar();
		}
		else {
			desligar();
		}
	}
	
	
}
