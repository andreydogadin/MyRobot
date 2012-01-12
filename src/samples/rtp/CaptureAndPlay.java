/*
 * CaptureTest.java
 *
 * Created on November 24, 2002, 10:01 AM
 */

package samples.rtp;

import javax.media.*;
import javax.media.protocol.*;
import javax.media.format.*;

import java.net.*;
import java.util.Vector;
import java.io.IOException;

/**
 * This class opens two RTP steams - one receives audio data from the url "rtp://<localhost>:12346/audio"
 * and plays it to the local output device, and the other sends audio data (captured from the
 * local audio capture device) to the url "rtp://<localhost>:12344/audio". 
 *<P>
 * This class should generally be started before running any of the other classes in this package,
 * since the other classes either send data to the url "rtp://<localhost>:12346/audio" or receive
 * data from the url "rtp://<localhost>:12346/audio", or do both.
 *<P>
 * You can also use JMFStudio to send to/receive from these urls.
 */
public class CaptureAndPlay {
    DataSink dsink;
    Processor dstProc, srcProc;
    Player player;

    public static void main(String[] args) {
	try {
	    String lh = InetAddress.getLocalHost().getHostAddress();
	    String outgoingUrl = "rtp://"+lh+":12344/audio";
	    String incomingUrl = "rtp://"+lh+":12346/audio";
	    CaptureAndPlay ctr = new CaptureAndPlay(AudioFormat.LINEAR,16000,16,1, outgoingUrl, incomingUrl);
	    ctr.start();
	} catch(Exception e) {
	    e.printStackTrace();
	}
    }
        
    public CaptureAndPlay(String encoding, double sampleRate, int sampleSizeInBite, int channels,
    String outgoingUrl, String incomingUrl)
    throws IOException, NoDataSinkException, NoDataSourceException,
    NoProcessorException, NoPlayerException, CannotRealizeException {
	
	//Create capture device for outgoing stream
	Vector devs = CaptureDeviceManager.getDeviceList(new AudioFormat(AudioFormat.LINEAR,16000,16,1));
	CaptureDeviceInfo info = (CaptureDeviceInfo)devs.elementAt(0);
	DataSource ds = Manager.createDataSource(info.getLocator());
	
	FileTypeDescriptor cd = new FileTypeDescriptor(FileTypeDescriptor.RAW_RTP);
	Format[] outFormats = {
	    //either of these options should work for the outgoing RTP stream
	    new AudioFormat(AudioFormat.MPEG_RTP)  //this one should send less data
	    //new AudioFormat(AudioFormat.ULAW_RTP, 8000, 8, 1) 
	};
	ProcessorModel pm = new ProcessorModel(ds, outFormats, cd);
	dstProc = Manager.createRealizedProcessor(pm);
	System.out.println("created outgoing RTP stream, sending on: "+outgoingUrl);
	
	MediaLocator dst = new MediaLocator(outgoingUrl);
	dsink = Manager.createDataSink(dstProc.getDataOutput(), dst);
	
	//Create player for incoming stream
	cd = new FileTypeDescriptor(FileTypeDescriptor.RAW);
	//format for the output device (eg, speakers)
	outFormats = new Format[] {
	    new AudioFormat(AudioFormat.LINEAR, 16000, 16, 1)
	};
	//processor to turn incoming stream into a playable 16kHz, 16bit linear stream
	pm = new ProcessorModel(new MediaLocator(incomingUrl), outFormats, cd);
	System.out.println("about to create processor for incoming RTP stream, listening on: "+incomingUrl);
	System.out.println("Start up the rtp sending stream now!");
	srcProc = Manager.createRealizedProcessor(pm);
	System.out.println("created speaker processor");
	
	player = Manager.createPlayer(srcProc.getDataOutput());
	System.err.println("player created: "+player);
	
    }
    
    public void start() throws IOException {
	dsink.open();
	dsink.start();
	dstProc.start();
	srcProc.start();
	player.start();
    }
    
    public void stop() throws IOException {
	dstProc.stop();
	dsink.close();
    }
    
}
