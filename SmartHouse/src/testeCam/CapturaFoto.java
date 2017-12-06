package testeCam;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.media.Buffer;
import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Player;
import javax.media.control.FrameGrabbingControl;
import javax.media.format.VideoFormat;
import javax.media.util.BufferToImage;
import javax.swing.JButton;
import javax.swing.JOptionPane;
public class CapturaFoto extends javax.swing.JFrame {
    private static final long serialVersionUID = 1L;
    public static Player player = null;
    public CaptureDeviceInfo di = null;  //  @jve:decl-index=0:
    public MediaLocator ml = null;  //  @jve:decl-index=0:
    public JButton capture = null;
    public Buffer buf = null;
    public Image img = null;
    public VideoFormat vf = null;
    public BufferToImage btoi = null;
    public ImagePanel imgpanel = null;
    private JButton captura = null;
    public CapturaFoto() {
        super();
        initComponents();
        initialize();
        this.setSize(600,530);
        setLocation(170, 05);
    }
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(600,500);
        this.add(getCaptura(), BorderLayout.SOUTH);
        this.setVisible(true);
        String str2 = "vfw//0";
        di = CaptureDeviceManager.getDevice(str2);
        ml =  new MediaLocator("vfw://0");
        try {
            player = Manager.createRealizedPlayer(ml);
            player.start();
            Component comp;
            if ((comp = player.getVisualComponent()) != null) {
                add(comp, BorderLayout.NORTH);
            }
            add(captura, BorderLayout.SOUTH);
            } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void gravaImg (Image imagem) throws IOException{
        String caminho = "C:/photos"+"00"+".JPG";
   try {
            ImageIO.write((RenderedImage) imagem, "jpg", new File(caminho));
            JOptionPane.showMessageDialog(this, "Imagem Capturada!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "não foi possivel encontrar " + "o dispositivo para a captura da imagem.");
            e.printStackTrace();
        }
    }
    private JButton getCaptura() {
        if (captura == null) {
            captura = new JButton("Captura");
            captura.setBounds(10, 10, 600, 500);
            captura.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    FrameGrabbingControl fgc = (FrameGrabbingControl) player
                            .getControl("javax.media.control.FrameGrabbingControl");
                    buf = fgc.grabFrame();
                    btoi = new BufferToImage((VideoFormat) buf.getFormat());
                    img = btoi.createImage(buf);
                    try {
                        gravaImg(img);
                    } catch (IOException ex) {
                        Logger.getLogger(CapturaFoto.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
        }
        return captura;
    }
    class ImagePanel extends javax.swing.JPanel {
        private static final long serialVersionUID = 1L;
        public Image myimg = null;
        public ImagePanel() {
            JOptionPane.showMessageDialog(null, "setando a classe");
            setLayout(null);
            setSize(630,530);
        }
        public void setImage(Image img) {
            this.myimg = img;
            repaint();
        }
        @Override
        public void paint(Graphics g) {
            if (myimg != null) {
                g.drawImage(myimg, 0, 0, this);
            }
        }
    }
private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Capturar Foto");
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 607, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 489, Short.MAX_VALUE)
        );
        pack();
    }                       
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CapturaFoto().setVisible(true);
            }
        });
    }
}
