/*
 * RTPVoiceServer.java
 *
 * Created on November 24, 2002, 1:39 PM
 */

package samples.rtp;

import javax.speech.*;
import javax.speech.synthesis.*;
import javax.speech.recognition.*;
import java.util.*;
import java.text.SimpleDateFormat;

import com.cloudgarden.audio.*;
import com.cloudgarden.speech.*;

import javax.media.protocol.*;
import javax.media.*;
import javax.media.format.*;

import samples.*;

/**
 * Creates a Recognizer which listens to an incoming RTP stream and replies
 * with synthesized speech on a separate RTP stream - to send the
 * RTP stream to the recognizer and hear it's response, start up the CaptureAndPlay class, then
 * this class, then say "what date/time is it", or "goodbye computer".
 */
public class RTPCommandVoiceServer {
    
    Synthesizer synth;
    Recognizer reco;
    public static String rtpRecoUrl;
    public static String rtpSynthUrl;
    CGAudioManager synAudioMan, recoAudioMan;
    Processor recoProc, synProc;
    DataSink recoDataSink, synDataSink;
    
    /** Creates a new instance of RTPCommandVoiceServer */
    public RTPCommandVoiceServer() {
    }
    
    public static void main(String[] args) {
	try {
	    String lh = java.net.InetAddress.getLocalHost().getHostAddress();
	    rtpRecoUrl = "rtp://"+lh+":12344/audio";
	    rtpSynthUrl = "rtp://"+lh+":12346/audio";
	    RTPCommandVoiceServer server = new RTPCommandVoiceServer();
	    server.setupSynth();
	    server.speak("Hello world");
	    server.listen();
	    //server.close();
	} catch(Exception e) {
	    e.printStackTrace();
	}
    }
    
    /**
     * Allocates a recognizer, listening on an RTP port.
     */
    public void listen() {
	try {
	    reco = Central.createRecognizer(new EngineModeDesc(null, null, java.util.Locale.ENGLISH, null));
	    
	    reco.addEngineListener(new TestEngineListener());
	    
	    reco.allocate();
	    reco.waitEngineState(reco.ALLOCATED);
	    
	    recoAudioMan = (CGAudioManager)reco.getAudioManager();
	    recoAudioMan.addAudioListener(new TestAudioListener());
	    recoAudioMan.addTransferListener(new TransferListener() {
		public void bytesTransferred(TransferEvent e) {
		    System.out.print("R"+e.getLength()+" ");
		}
	    });
	    
	    RuleGrammar gram = reco.newRuleGrammar("gram1");
	    Rule r = gram.ruleForJSGF("goodbye computer {QUIT} | what time is it {TIME} | what date is it {DATE}");
	    gram.setRule("rule1",r, true);
	    gram.setEnabled(true);
	    
	    //must be called before recognizer is resumed (for SAPI5 engines)!
	    reco.getRecognizerProperties().setResultAudioProvided(true);
	    
	    recoDataSink = recoAudioMan.getDataSink();
	    reco.commitChanges();
	    reco.requestFocus();
	    reco.resume();
	    reco.waitEngineState(reco.LISTENING);
	    reco.addResultListener(new ResultAdapter() {
		public void resultUpdated(ResultEvent ev) {
		    System.out.println("updated "+ev);
		}
		public void resultAccepted(ResultEvent ev) {
		    System.out.println("accepted "+ev);
		    FinalResult res1 = (FinalResult)ev.getSource();
		    FinalRuleResult frr = (FinalRuleResult)res1;
		    String[] tags = frr.getTags();
		    if(tags == null) return;
		    String tag = tags[0];
		    
		    //Uncomment this to hear what recognizer heard.
			/*
			try {
			    reco.pause();
			    frr.getAudio().play();
			    reco.resume();
			} catch(Exception e) {
			    e.printStackTrace();
			}
			 */
		    
		    if(tag.equals("QUIT")) {
			speak("goodbye");
			close();
			return;
		    } else if(tag.equals("TIME")) {
			SimpleDateFormat df = new SimpleDateFormat("h:mm a, zzzz");
			speak("The time is "+df.format(new Date()));
		    } else if(tag.equals("DATE")) {
			SimpleDateFormat df = new SimpleDateFormat("dd MM yyyy");
			speak("The date is "+df.format(new Date()));
		    }
		}
	    });
	    
	    javax.sound.sampled.AudioFormat fmt = recoAudioMan.getAudioFormat();
	    
	    FileTypeDescriptor cd;
	    cd = new FileTypeDescriptor(FileTypeDescriptor.RAW);
	    Format[] outFormats = {
		new AudioFormat(AudioFormat.LINEAR,
		fmt.getFrameRate(), fmt.getSampleSizeInBits(), fmt.getChannels(),
		AudioFormat.LITTLE_ENDIAN,AudioFormat.SIGNED)
	    };
	    System.out.println("DataSink = "+recoDataSink+" rtpRecoUrl="+rtpRecoUrl);
	    
	    ProcessorModel pm = new ProcessorModel(new MediaLocator(rtpRecoUrl), outFormats, cd);
	    recoProc = Manager.createRealizedProcessor(pm);
	    System.out.println("RTP connection established");
	    
	    recoDataSink.setSource(recoProc.getDataOutput());
	    recoProc.start();
	    
	    recoDataSink.open();
	    recoDataSink.start();
	    
	    System.out.println("listening");
	    
	    reco.waitEngineState(reco.DEALLOCATED);
	    
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
    
    private void speak(String text) {
	System.out.println("speaking: "+text);
	synth.speakPlainText(text, null);
	try {
	    synth.waitEngineState(synth.QUEUE_EMPTY);
	} catch(InterruptedException e) { }
    }
    
    public void setupSynth() {
	try {
	    synth = Central.createSynthesizer(new EngineModeDesc(null,null, java.util.Locale.ENGLISH, null));
	    
	    synth.addEngineListener(new TestEngineListener());
	    
	    synAudioMan = (CGAudioManager)synth.getAudioManager();
	    synAudioMan.addTransferListener(new TransferListener() {
		public void bytesTransferred(TransferEvent e) {
		    System.out.print("S"+e.getLength()+" ");
		}
	    });
	    synAudioMan.addAudioListener(new TestAudioListener());
	    
	    synth.allocate();
	    synth.waitEngineState(synth.ALLOCATED);
	    
	    //javax.sound.sampled.AudioFormat fmt = audioMan.getAudioFormat();
	    //System.out.println("Synthesizer Format = "+fmt);
	    synth.resume();
	    
	    FileTypeDescriptor cd = new FileTypeDescriptor(FileTypeDescriptor.RAW_RTP);
	    Format[] outFormats = {
		new AudioFormat(AudioFormat.MPEG_RTP)
		//new AudioFormat(AudioFormat.ULAW_RTP, 8000, 8, 1)
	    };
	    DataSource ds = synAudioMan.getDataSource();
	    System.out.println("DataSource = "+ds+" rtpSynthUrl="+rtpSynthUrl);
	    
	    ProcessorModel pm = new ProcessorModel(ds, outFormats, cd);
	    synProc = Manager.createRealizedProcessor(pm);
	    System.out.println("created synth processor");
	    
	    MediaLocator dst = new MediaLocator(rtpSynthUrl);
	    synDataSink = Manager.createDataSink(synProc.getDataOutput(), dst);
	    
	    synDataSink.open();
	    synDataSink.start();
	    synProc.start();
	    
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
    
    public void close() {
	System.out.println("closing");
	try {
	    recoProc.stop();
	    recoDataSink.close();
	    synth.waitEngineState(Synthesizer.QUEUE_EMPTY);
	    synAudioMan.closeOutput();
	    synAudioMan.drain();
	    reco.deallocate();
	    reco.waitEngineState(Engine.DEALLOCATED);
	    synth.deallocate();
	    synth.waitEngineState(Engine.DEALLOCATED);
	    Thread.currentThread().sleep(2000);
	} catch(Exception e2) {
	    e2.printStackTrace();
	}
	synProc.stop();
	synDataSink.close();
	System.out.println("all done");
	//...before closing up shop
	System.exit(0);
    }
}
