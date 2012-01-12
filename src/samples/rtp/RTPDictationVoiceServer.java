/*
 * RTPVoiceServer.java
 *
 * Created on November 24, 2002, 1:39 PM
 */

package samples.rtp;

import javax.speech.*;
import javax.speech.synthesis.*;
import javax.speech.recognition.*;

import com.cloudgarden.audio.*;
import com.cloudgarden.speech.*;

import javax.media.protocol.*;
import javax.media.*;
import javax.media.format.*;

import java.net.*;

import samples.*;


/**
 * Creates a Recognizer which listens to an incoming RTP stream and replies
 * with synthesized speech on a separate RTP stream - to send the
 * RTP stream to the recognizer and hear it's response, start up the CaptureAndPlay class,
 * then speak into the microphone - the recognizer should respond
 * with what it thinks you said.
 */
public class RTPDictationVoiceServer {
    
    Synthesizer synth;
    Recognizer reco;
    
    public static String lh;
    public static String rtpRecoUrl;
    public static String rtpSynthUrl ;
    CGAudioManager synAudioMan, recoAudioMan;
    Processor recoProc, synProc;
    DataSink recoDataSink, synDataSink;
    
    public RTPDictationVoiceServer() {
    }
    
    public static void main(String[] args) {
	try {
	    lh = InetAddress.getLocalHost().getHostAddress();
	    rtpRecoUrl = "rtp://"+lh+":12344/audio";
	    rtpSynthUrl = "rtp://"+lh+":12346/audio";
	    RTPDictationVoiceServer server = new RTPDictationVoiceServer();
	    server.setupSynth();
	    server.speak("Hello world");
	    server.listen();
	    server.close();
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
	    recoAudioMan.addTransferListener(new TransferListener() {
		public void bytesTransferred(TransferEvent e) {
		    System.out.print("R");
		}
	    });
	    recoAudioMan.addAudioListener(new TestAudioListener());
	    
	    DictationGrammar dict = reco.getDictationGrammar("dictation");
	    dict.setEnabled(true);
	    
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
		    String txt = "";
		    FinalResult res1 = (FinalResult)ev.getSource();
		    if(res1.getGrammar() instanceof DictationGrammar) {
			FinalDictationResult fdr = (FinalDictationResult)res1;
			ResultToken[] toks = fdr.getBestTokens();
			for(int i=0;i<toks.length; i++) txt += toks[i].getSpokenText()+" ";
			speak("I heard, "+txt);
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
	    
	    CGAudioManager audioMan = (CGAudioManager)synth.getAudioManager();
	    audioMan.addTransferListener(new TransferListener() {
		public void bytesTransferred(TransferEvent e) {
		    System.out.print("S");
		}
	    });
	    audioMan.addAudioListener(new TestAudioListener());
	    
	    synth.allocate();
	    synth.waitEngineState(synth.ALLOCATED);
	    
	    //javax.sound.sampled.AudioFormat fmt = audioMan.getAudioFormat();
	    //System.out.println("Synthesizer Format = "+fmt);
	    synth.resume();
	    
	    FileTypeDescriptor cd = new FileTypeDescriptor(FileTypeDescriptor.RAW_RTP);
	    Format[] outFormats = {
		new AudioFormat(AudioFormat.MPEG_RTP)
		//new AudioFormat(javax.robot.media.format.AudioFormat.ULAW_RTP, 8000, 8, 1)
	    };
	    DataSource ds = audioMan.getDataSource();
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
	System.out.println("deallocated");
	recoProc.stop();
	recoDataSink.close();
	synAudioMan.closeOutput();
	try {
	    synAudioMan.drain();
	    reco.forceFinalize(true);
	    reco.deallocate();
	    synth.deallocate();
	    reco.waitEngineState(Engine.DEALLOCATED);
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
