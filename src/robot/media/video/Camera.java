package robot.media.video;

import javax.imageio.ImageIO;
import javax.media.*;
import javax.media.control.FrameGrabbingControl;
import javax.media.format.VideoFormat;
import javax.media.format.YUVFormat;
import javax.media.protocol.DataSource;
import javax.media.util.BufferToImage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: andrey.dogadin
 * Date: 8/11/11
 * Time: 1:52 PM
 * To change this template use File | Settings | File Templates.
 * --
 */
public class Camera {
    private static JPanel panel = new JPanel();
    private static JFrame myFrame = new JFrame();
    private static final Format VIDEO_FORMAT = new YUVFormat();
    private static Player player = null;

    public Camera() {
        CaptureDeviceInfo dev = CaptureDeviceManager.getDevice("vfw:Microsoft WDM Image Capture (Win32):1");
        MediaLocator videoMediaLocator = dev.getLocator();
        DataSource videoDataSource = null;
        panel = new JPanel();
        panel.setLayout(new FlowLayout());
        myFrame = new JFrame();
        myFrame.setVisible(true);
        myFrame.setSize(300, 300);
        myFrame.getContentPane().add(panel);
        myFrame.addWindowListener(
                new WindowAdapter() {
                    public void windowClosing(WindowEvent event) {
                        player.close();
                        myFrame.dispose();
                    }
                }
        );
        try {
            videoDataSource = javax.media.Manager.createDataSource(videoMediaLocator);
            videoDataSource.connect();
            player = Manager.createRealizedPlayer(videoDataSource);
            player.start();

            player = Manager.createPlayer(videoDataSource);
            player.addControllerListener(
                    new ControllerAdapter() {
                        public void controllerUpdate(ControllerEvent event) {
                            if (event instanceof RealizeCompleteEvent) {
                                panel.add(player.getVisualComponent());
                                panel.add(player.getControlPanelComponent());
                                myFrame.validate();

                            }

                        }

                    }

            );
            player.start();
        } catch (Exception ex) {
            this.Release();
            ex.printStackTrace();
        }
    }

    public void Release() {
        if (player != null) player.close();
        if (myFrame != null) myFrame.dispose();
    }

    public void getPicture(String fileName) {

        try {

            FrameGrabbingControl fgc = (FrameGrabbingControl) player.getControl("javax.robot.media.control.FrameGrabbingControl");
            Buffer buf = fgc.grabFrame();
            BufferToImage btoi = new BufferToImage((VideoFormat) buf.getFormat());
            Image img = btoi.createImage(buf);
            if (img != null)
                saveImagetoFile(img, fileName);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    public void saveImagetoFile(Image img, String fileName) throws IOException {

        int w = img.getWidth(null);
        int h = img.getHeight(null);
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = bi.createGraphics();
        g2.drawImage(img, 0, 0, null);
        g2.dispose();
        String fileType = fileName.substring(fileName.indexOf('.') + 1);
        ImageIO.write(bi, fileType, new File(fileName));
    }
}
