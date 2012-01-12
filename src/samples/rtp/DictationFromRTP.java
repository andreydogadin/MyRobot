package samples.rtp;

import samples.*;
import samples.recognition.*;

import javax.speech.*;
import javax.speech.recognition.*;
import java.io.*;

import com.cloudgarden.audio.*;
import com.cloudgarden.speech.*;

import javax.media.protocol.*;
import javax.media.*;
import javax.media.format.*;

/**
     * Allocates a recognizer, listening on an RTP port, and
     * sends the contents of a WAV file to the RTP port the recognizer is listening on.
     */
public class DictationFromRTP {
    static Recognizer reco = null;
    static String rtpUrl = null;
    
    public static void main(String args[]) {
	try {
	    String lh = java.net.InetAddress.getLocalHost().getHostAddress();
	    rtpUrl = "rtp://"+lh+":12344/audio";
	    reco = Central.createRecognizer(new EngineModeDesc(null,null, java.util.Locale.ENGLISH , null));
	    reco.allocate();
	    reco.waitEngineState(reco.ALLOCATED);
	    
	    CGAudioManager audioMan = (CGAudioManager)reco.getAudioManager();
	    audioMan.addTransferListener(new TransferListener() {
		public void bytesTransferred(TransferEvent e) {
		    System.out.println(e.toString());
		}
	    });
	    DictationGrammar dict = reco.getDictationGrammar("dictation");
	    dict.setEnabled(true);
	    
	    //must be called before recognizer is resumed (for SAPI5 engines)!
	    reco.getRecognizerProperties().setResultAudioProvided(true);
	    
	    DataSink dsink = audioMan.getDataSink();
	    reco.commitChanges();
	    reco.requestFocus();
	    reco.resume();
	    reco.waitEngineState(reco.LISTENING);
	    reco.addResultListener(new TestResultListener(reco, 1, true));
	    reco.addEngineListener(new TestEngineListener());
	    audioMan.addAudioListener(new TestAudioListener());
	    
	    javax.sound.sampled.AudioFormat fmt = audioMan.getAudioFormat();
	    
	    FileTypeDescriptor cd;
	    cd = new FileTypeDescriptor(FileTypeDescriptor.RAW);
	    Format[] outFormats = {
		new AudioFormat(AudioFormat.LINEAR,
		fmt.getFrameRate(), fmt.getSampleSizeInBits(), fmt.getChannels(),
		AudioFormat.LITTLE_ENDIAN,AudioFormat.SIGNED)
	    };
	    System.out.println("DataSink = "+dsink);
	    
	    //start the RTP server sending audio data from a file
	    speakToRTP();
	    
	    ProcessorModel pm = new ProcessorModel(new MediaLocator(rtpUrl), outFormats, cd);
	    Processor p = Manager.createRealizedProcessor(pm);
	    System.out.println("RTP connection established");
	    
	    dsink.setSource(p.getDataOutput());
	    p.start();
	    
	    dsink.open();
	    dsink.start();
	    
	    p.addControllerListener(new ControllerAdapter() {
		public void controllerUpdate(javax.media.ControllerEvent evt) {
		    System.out.println("reco processor evt "+evt);
		}
	    });
	    
	    System.out.println("listening");
	    
	    reco.waitEngineState(reco.DEALLOCATED);
	    System.out.println("deallocated");
	    p.stop();
	    dsink.close();
	    
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    try {
		reco.deallocate();
		//wait till we are deallocated...
		reco.waitEngineState(Engine.DEALLOCATED);
	    } catch(Exception e2) {
		e2.printStackTrace();
	    }
	}
	//...before closing up shop
	System.exit(0);
    }
    
    /**
     * Sends the contents of a WAV file to an RTP url (which is listened to by the Recognizer)
     */
    public static void speakToRTP() {
	Runnable r = new Runnable() {
	    public void run() {
		try {
		    DataSource ds = Manager.createDataSource(new File("resources\\hello_world.wav").toURL());
		    System.out.println("DataSource = "+ds);
		    FileTypeDescriptor cd = new FileTypeDescriptor(FileTypeDescriptor.RAW_RTP);
		    
		    javax.media.Format[] outFormats = {
			new javax.media.format.AudioFormat(javax.media.format.AudioFormat.LINEAR, 16000, 16, 1)
			//new javax.robot.media.format.AudioFormat(javax.robot.media.format.AudioFormat.ULAW_RTP, 8000, 8, 1)
		    };
		    ProcessorModel pm = new ProcessorModel(ds, outFormats, cd);
		    Processor p = Manager.createRealizedProcessor(pm);
		    
		    final DataSink dsink = Manager.createDataSink(p.getDataOutput(), new MediaLocator(rtpUrl));
		    
		    dsink.open();
		    dsink.start();
		    p.addControllerListener(new ControllerAdapter() {
			public void controllerUpdate(javax.media.ControllerEvent evt) {
			    if(evt instanceof EndOfMediaEvent) {
				try {
				    System.out.println("all done");
				    //need to tell recognizer that all data has finished
				    dsink.close();
				    System.out.println("closed");
				    //wait for recognizer to recognize
				    Thread.currentThread().sleep(10000);
				    //and if it still hasn't force it to finalize result and deallocate
				    if(!reco.testEngineState(reco.DEALLOCATED)) {
					System.out.println("forcing finalize");
					reco.forceFinalize(true);
					System.out.println("deallocating");
					reco.deallocate();
				    }
				} catch(Exception e) {
				    e.printStackTrace();
				}
			    }
			}
		    });
		    
		    p.start();
		    System.out.println("playing file to rtp client");
		} catch(Exception e) {
		    e.printStackTrace(System.out);
		}
	    }
	};
	new Thread(r).start();
    }
    
}

