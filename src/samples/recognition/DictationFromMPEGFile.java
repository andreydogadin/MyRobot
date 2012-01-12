package samples.recognition;

import samples.*;
import javax.speech.*;
import javax.speech.recognition.*;

import com.cloudgarden.audio.*;
import com.cloudgarden.speech.*;
import com.cloudgarden.speech.userinterface.*;
import javax.media.*;
import javax.media.protocol.*;
import java.net.URL;

/**
 * Tests dictation using audio data obtained from an mp3 file
 */
public class DictationFromMPEGFile {
    static Recognizer rec = null;
    
    public static void main(String[] args) {
	try {
	    //Show only those recognizers which support dictation
	    RecognizerModeDesc desc = new RecognizerModeDesc(null,Boolean.TRUE);
	    SpeechEngineChooser chooser = SpeechEngineChooser.getRecognizerDialog(desc);
	    chooser.show();
	    
	    desc = chooser.getRecognizerModeDesc();
	    rec = Central.createRecognizer(desc);
	    rec.addEngineListener(new TestEngineListener());
	    
	    //Add a ResultListener which deallocates after one recognition and plays back the audio
	    rec.addResultListener(new TestResultListener(rec,1,true));
	    
	    RecognizerAudioAdapter raud = new TestAudioListener();
	    CGAudioManager audioMan = (CGAudioManager)rec.getAudioManager();
	    audioMan.addAudioListener(raud);

	    audioMan.addTransferListener(new TransferListener() {
		public void bytesTransferred(TransferEvent evt) {
		    System.out.println("transferred "+evt.getLength());
		}
	    });
	    	    	    
	    rec.allocate();
	    rec.waitEngineState(Recognizer.ALLOCATED);
	    
	    final DictationGrammar dictation = rec.getDictationGrammar("dictation");
	    dictation.setEnabled(true);
	    
	    System.out.println("Using engine "+rec.getEngineModeDesc());
	    SpeakerManager speakerManager = rec.getSpeakerManager();
	    SpeakerProfile prof = chooser.getSpeakerProfile();
	    speakerManager.setCurrentSpeaker(prof);
	    System.out.println("Changed Current Profile to "+speakerManager.getCurrentSpeaker());
	    
	    final RecognizerProperties props = rec.getRecognizerProperties();
	    props.setNumResultAlternatives(5);
	    //Retain audio so it can be played back later (see ResultListener)
	    props.setResultAudioProvided(true);
	    
	    boolean useDataSink = false;
	    AudioSource src = null;
	    DataSink sink = null;
	    DataSource ds = null;
	    
	    // useDataSink switches between using the getDataSink method and using
	    // a AudioMediaURLSource object (slightly simpler).
	    if(useDataSink) {
		sink = audioMan.getDataSink();
		ds = Manager.createDataSource(new URL("file:resources\\hello_world.mp3"));
		sink.setSource(ds);
	    } else {
		src = new AudioMediaURLSource(new URL("file:resources\\hello_world.mp3"), audioMan.getAudioFormat());
		audioMan.setSource(src);
	    }
	    
	    rec.suspend();
	    rec.commitChanges();
	    rec.requestFocus();
	    //wait till changes committed (signalled by LISTENING state)
	    rec.waitEngineState(rec.LISTENING);
	    
	    if(useDataSink) {
		sink.open();
		ds.start();
		sink.start();
	    } else {
		src.startSending();
	    }
	    
	    audioMan.drain();
	    System.out.println("AudioManager drained");
	    
	    if(!useDataSink) src.stopSending();
	    
	    //deallocate after 15 seconds - in case nothing was recognized
	    Thread killThread = new Thread() {
		public void run() {
		    try {
			sleep(10000);
			// Sometimes for SAPI4 engines (ie. IBM's ViaVoice engines)
			// recognition won't complete till the grammar is disabled!
			if(((CGEngineProperties)props).getSapiVersion() == 4) {
			    System.out.println("disabling dictation after audio data finished");
			    dictation.setEnabled(false);
			    if(!rec.testEngineState(rec.DEALLOCATED) &&
			    !rec.testEngineState(rec.DEALLOCATING_RESOURCES)) {
				rec.commitChanges();
				rec.waitEngineState(rec.LISTENING);
			    }
			}
			sleep(5000);
			System.out.println("forcing finalize");
			rec.forceFinalize(true);
			System.out.println("deallocating");
			rec.deallocate();
		    } catch(Exception e) {}
		}
	    };
	    killThread.start();
	    
	    System.out.println("waiting for deallocation");
	    rec.waitEngineState(Recognizer.DEALLOCATED);
	    
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
