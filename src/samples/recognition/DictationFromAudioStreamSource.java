package samples.recognition;

import samples.*;
import javax.speech.*;
import javax.speech.recognition.*;
import javax.sound.sampled.*;
import java.io.*;
import com.cloudgarden.audio.*;
import com.cloudgarden.speech.*;
import com.cloudgarden.speech.userinterface.*;

/**
 * Receives input from a filtered line (demonstrates the AudioFilterSource
 * for reducing power-line hum).
 */
public class DictationFromAudioStreamSource {
    static Recognizer rec = null;
    
    public static void main(String[] args) {
	try {
	    RecognizerModeDesc desc = new RecognizerModeDesc(null,Boolean.TRUE);
	    SpeechEngineChooser chooser = SpeechEngineChooser.getRecognizerDialog(desc);
	    chooser.show();
	    desc = chooser.getRecognizerModeDesc();
	    rec = Central.createRecognizer(desc);
	    rec.addEngineListener(new TestEngineListener());
	    rec.addResultListener(new TestResultListener(rec,2,false));
	    
	    RecognizerAudioAdapter raud = new TestAudioListener();
	    CGAudioManager audioMan = (CGAudioManager)rec.getAudioManager();
	    audioMan.addAudioListener(raud);
	    
	    rec.allocate();
	    rec.waitEngineState(Recognizer.ALLOCATED);
	    
	    System.out.println("Using engine "+rec.getEngineModeDesc());
	    SpeakerManager speakerManager = rec.getSpeakerManager();
	    SpeakerProfile prof = chooser.getSpeakerProfile();
	    speakerManager.setCurrentSpeaker(prof);
	    System.out.println("Changed Current Profile to "+speakerManager.getCurrentSpeaker());
	    
	    DictationGrammar dictation;
	    dictation = rec.getDictationGrammar("dictation");
	    dictation.setEnabled(true);
	    
	    rec.suspend();
	    rec.commitChanges();
	    rec.waitEngineState(rec.LISTENING);
	    
	    audioMan = (CGAudioManager)rec.getAudioManager();
	    
	    //must suspend reco before calling setSource, and resume afterwards
	    rec.suspend();

	    //need to convert to the recognizer audio format
	    
	    AudioFormat fmt  = audioMan.getAudioFormat();
	    //fmt = new AudioFormat(11025,16,1,true, true);
	    //DataLine.Info ininfo = new DataLine.Info(TargetDataLine.class, fmt);
	    //TargetDataLine inline = (TargetDataLine) AudioSystem.getLine(ininfo);
	    //AudioLineSource source = new AudioLineSource(inline);
	    AudioSource source = new AudioLineSource(fmt);
	    AudioFileSink sink = new AudioFileSink(new File("test.wav"),fmt,AudioFileFormat.Type.WAVE);
	    source.setSink(sink);
	    source.startSending();
	    sink.addTransferListener(new TransferListener() {
		public void bytesTransferred(TransferEvent evt) {
		    System.out.println("transferred "+evt.getLength());
		}
	    });
	    System.out.println("speak now");
	    Thread.currentThread().sleep(4000);
	    source.stopSending();
	    sink.drain();
	    //AudioStreamSource source = new AudioStreamSource(new AudioInputStream(inline));
	    //AudioFileSource 
	    source = new AudioFileSource(new File("test.wav"));
	    
	    System.out.println("audioMan format = "+audioMan.getAudioFormat());
	    System.out.println("source format = "+source.getAudioFormat());
	    /*
	    source.addTransferListener(new TransferListener() {
		public void bytesTransferred(byte[] data, int offset, int len, int dirn) {
		    System.out.println("transferred "+len);
		}
	    });
	     */
	    
	    audioMan.setSource(source);
	    //new AudioFormatConverter(source, audioMan);
	    rec.requestFocus();
	    rec.resume();
	    rec.waitEngineState(rec.FOCUS_ON);
	    
	    //inline.open();
	    //inline.start();
	    source.startSending();
	    
	    rec.waitEngineState(Recognizer.DEALLOCATED);
	    //two recognitions and we deallocate
	    System.out.println("All done");
	    source.stopSending();
	    //wait for captured audio to finish playing (when playback is enabled)
	    //Thread.currentThread().sleep(2500);
	    
	} catch(Exception e) {
	    e.printStackTrace(System.out);
	} catch(Error e1) {
	    e1.printStackTrace(System.out);
	} finally {
	    try {
		rec.deallocate();
	    } catch(Exception e2) {
		e2.printStackTrace(System.out);
	    }
	    System.exit(0);
	}
    }
}
