package samples.rtp;

import java.io.*;

import javax.media.protocol.*;
import javax.media.*;

/**
 * Plays file to RTP stream - to hear output stream, start up
 * CaptureAndPlay class, then start this class.
 */
public class FileToRTP {
    
    public static void main(String args[]) {
	try {
	    String fileName = "resources\\hello_world.wav";

	    String lh = java.net.InetAddress.getLocalHost().getHostAddress();
	    String rtpUrl = "rtp://"+lh+":12346/audio";
	    
	    DataSource ds = Manager.createDataSource(new File(fileName).toURL());
	    System.out.println("DataSource = "+ds);
	    FileTypeDescriptor cd = new FileTypeDescriptor(FileTypeDescriptor.RAW_RTP);
	    
	    javax.media.Format[] outFormats = {
		new javax.media.format.AudioFormat(javax.media.format.AudioFormat.ULAW_RTP, 8000, 8, 1)
	    };
	    ProcessorModel pm = new ProcessorModel(ds, outFormats, cd);
	    Processor p = Manager.createRealizedProcessor(pm);
	    
	    DataSink dsink = Manager.createDataSink(p.getDataOutput(), new MediaLocator(rtpUrl));
	    
	    dsink.open();
	    dsink.start();
	    p.start();
	    System.out.println("playing file to rtp client");
	    
	    //let file play out
	    Thread.currentThread().sleep(10000);
	    System.out.println("all done");
	    p.stop();
	    dsink.close();
	    
	} catch(Exception e) {
	    e.printStackTrace(System.out);
	}
	
	//...before closing up shop
	System.exit(0);
    }
    
}

