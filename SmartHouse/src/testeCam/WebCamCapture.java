package testeCam;

import java.applet.Applet;  
import java.awt.BorderLayout;  
import java.awt.Component;  
import java.awt.Graphics;  
import java.awt.Image;  
import java.awt.Panel;  
import java.awt.event.ActionEvent;  
import java.awt.event.ActionListener;  
import java.awt.image.RenderedImage;  
import java.io.File;  
import java.io.IOException;  
import javax.imageio.ImageIO;  
import javax.media.Buffer;  
import javax.media.CaptureDeviceInfo;  
import javax.media.Manager;  
import javax.media.MediaLocator;  
import javax.media.Player;  
import javax.media.cdm.CaptureDeviceManager;  
import javax.media.control.FrameGrabbingControl;  
import javax.media.format.VideoFormat;  
import javax.media.util.BufferToImage;  
import javax.swing.JButton;  
import javax.swing.JComponent;  
import javax.swing.JOptionPane;  
public class WebCamCapture extends Applet implements ActionListener {  
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
    public void init() {  
        System.out.println("Inicializou");  
        this.setLayout(new BorderLayout());  
        this.setSize(320, 550);  
        this.setVisible(true);  
        imgpanel = new ImagePanel();  
        capture = new JButton("Capture");  
        capture.addActionListener(this);  
        // This may differ check the jmf registry for  
        // correct entry  
        String str2 = "vfw//0";  
        di = CaptureDeviceManager.getDevice(str2);  
        ml =  new MediaLocator("vfw://0");  
        System.out.println("executou di e ml");
        try {  
            player = Manager.createRealizedPlayer(ml);  
            player.start();  
            Component comp;  
            if ((comp = player.getVisualComponent()) != null) {  
                add(comp, BorderLayout.NORTH);  
            }  
            add(capture, BorderLayout.CENTER);  
            add(imgpanel, BorderLayout.SOUTH);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
    public void paint(Graphics g) {  
    }  
    public static void playerclose() {  
        player.close();  
        player.deallocate();  
    }  
    public void actionPerformed(ActionEvent e) {  
        JComponent c = (JComponent) e.getSource();  
        if (c == capture) {  
            // Grab a frame  
            FrameGrabbingControl fgc = (FrameGrabbingControl) player  
                    .getControl("javax.media.control.FrameGrabbingControl");  
            buf = fgc.grabFrame();  
            // Convert it to an image  
            btoi = new BufferToImage((VideoFormat) buf.getFormat());  
            img = btoi.createImage(buf);  
            // show the image  
            imgpanel.setImage(img);  
            gravaImg(img);  
        }  
    }  
    public void gravaImg (Image imagem){  
        String caminho = "C:\\Documents and Settings\\Patrick\\Desktop" +  
                "\\PontoEletronico\\Fotos\\Patrick1.jpg";  
        try {  
            ImageIO.write((RenderedImage) imagem, "PNG", new File(caminho));  
        } catch (IOException e) {  
            JOptionPane.showMessageDialog(null, "não foi possivel encontrar " +  
                    "o dispositivo para a captura da imagem.");  
            e.printStackTrace();  
        }  
    }  
    class ImagePanel extends Panel {  
        private static final long serialVersionUID = 1L;  
        public Image myimg = null;  
        public ImagePanel() {  
            System.out.println("setando a classe");  
            setLayout(null);  
            setSize(320, 240);  
        }  
        public void setImage(Image img) {  
            this.myimg = img;  
            repaint();  
        }  
        public void paint(Graphics g) {  
            if (myimg != null) {  
                g.drawImage(myimg, 0, 0, this);  
            }  
        }  
    }  
}
