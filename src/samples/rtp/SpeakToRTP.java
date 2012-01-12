package samples.rtp;

import javax.speech.*;
import javax.speech.synthesis.*;
import java.util.*;
import java.text.*;

import com.cloudgarden.audio.*;
import com.cloudgarden.speech.*;

import javax.media.protocol.*;
import javax.media.*;

/**
 * Uses a synthesizer to send audio data to an rtp url
 * <P>
 * To hear the output, run the CaptureAndPlay class then run this example.
 */
public class SpeakToRTP {
    Synthesizer synth = null;
    static String rtpUrl;
    
    public static void main(String args[]) {
	try {
	    String lh = java.net.InetAddress.getLocalHost().getHostAddress();
	    rtpUrl = "rtp://"+lh+":12346/audio";
	    System.out.println("Localhost IP address = "+lh);
	} catch(Exception e) {
	    e.printStackTrace();
	}
	SpeakToRTP strtp = new SpeakToRTP();
	strtp.run();
    }
    
    private void speak(String text) {
	System.out.println("speaking: "+text);
	synth.speakPlainText(text, null);
	try {
	    synth.waitEngineState(synth.QUEUE_EMPTY);
	} catch(InterruptedException e) { }
    }
    
    public void run() {
	try {
	    synth = Central.createSynthesizer(new EngineModeDesc(null,null, java.util.Locale.ENGLISH, null));
	    
	    CGAudioManager audioMan = (CGAudioManager)synth.getAudioManager();
	    audioMan.addTransferListener(new TransferListener() {
		public void bytesTransferred(TransferEvent e) {
		    //System.out.println("transferred "+e);
		}
	    });
	    synth.allocate();
	    synth.waitEngineState(synth.ALLOCATED);
	    
	    //javax.sound.sampled.AudioFormat fmt = audioMan.getAudioFormat();
	    //System.out.println("Synthesizer Format = "+fmt);
	    synth.resume();
	    
	    try {
		FileTypeDescriptor cd;
		cd = new FileTypeDescriptor(FileTypeDescriptor.RAW_RTP);
		javax.media.Format[] outFormats = {
		    new javax.media.format.AudioFormat(javax.media.format.AudioFormat.MPEG_RTP)
		    //new javax.robot.media.format.AudioFormat(javax.robot.media.format.AudioFormat.ULAW_RTP, 8000, 8, 1)
		};
		DataSource ds = audioMan.getDataSource();
		System.out.println("DataSource = "+ds);
		
		//must call this method to keep the synthesizer output stream open
		//after the synthesizer has finished an utterance - otherwise RTP stream
		//becomes garbled.
		((CGPullBufferDataSource)ds).setKeepOpen(true);
		
		ProcessorModel pm = new ProcessorModel(ds, outFormats, cd);
		Processor p = Manager.createRealizedProcessor(pm);
		
		MediaLocator dst = new MediaLocator(rtpUrl);
		DataSink dsink = Manager.createDataSink(p.getDataOutput(), dst);
		
		dsink.open();
		dsink.start();
		p.start();
		
		int num = 10;
		for(int i=0;i<num;i++) {
		    try {
			DateFormat df = new SimpleDateFormat("h:mm a, zzzz");
			speak(i+" The time is "+df.format(new Date()));
			Thread.currentThread().sleep(6000);
		    } catch(Exception e) {
			e.printStackTrace();
		    }
		}
		
		speak("all done");
		audioMan.closeOutput();
		audioMan.drain();
		Thread.currentThread().sleep(2000);
		p.stop();
		dsink.close();
		
	    } catch(Exception e) {
		e.printStackTrace(System.out);
	    }
	    
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    try {
		synth.deallocate();
		//wait till we are deallocated...
		synth.waitEngineState(Engine.DEALLOCATED);
	    } catch(Exception e2) {
		e2.printStackTrace();
	    }
	}
	//...before closing up shop
	System.exit(0);
    }
    
}

