package samples.recognition;

import samples.*;
import javax.speech.*;
import javax.speech.recognition.*;
import java.io.*;
import com.cloudgarden.audio.*;
import com.cloudgarden.speech.*;
import com.cloudgarden.speech.userinterface.SpeechEngineChooser;

/**
 * Tests running a dictation grammar against input from a wave file.
 */
public class DictationFromFile {
    static Recognizer rec = null;
    static  DictationGrammar dictation;
    
    public static void main(String[] args) {
	try {
	    
	    RecognizerModeDesc desc = new RecognizerModeDesc(null,Boolean.TRUE);
	    SpeechEngineChooser chooser = null;
	    
	    try {
		chooser = SpeechEngineChooser.getRecognizerDialog(desc);
		chooser.show();
	    } catch(NoClassDefFoundError e) {
		System.out.println("Swing classes not found - continuing anyway");
	    }
	    if(chooser != null) desc = chooser.getRecognizerModeDesc();
	    
	    rec = Central.createRecognizer(desc);
	    rec.addEngineListener(new TestEngineListener());
	    
	    System.out.println("STARTING TEST");
	    
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
	    
	    dictation = rec.getDictationGrammar("dictation");
	    dictation.setEnabled(true);
	    //Set the ResultListener to  play back the audio and deallocate after one recognition.
	    dictation.addResultListener(new TestResultListener(rec,1,true));
	    RecognizerProperties props = rec.getRecognizerProperties();
	    //Retain audio so it can be played back later (see ResultListener)
	    props.setResultAudioProvided(true);
	    props.setNumResultAlternatives(4);
	    
	    System.out.println("Using engine "+rec.getEngineModeDesc());
	    SpeakerManager speakerManager = rec.getSpeakerManager();
	    if(chooser != null) {
		SpeakerProfile prof = chooser.getSpeakerProfile();
		speakerManager.setCurrentSpeaker(prof);
	    } else {
		SpeakerProfile[] profs = speakerManager.listKnownSpeakers();
		speakerManager.setCurrentSpeaker(profs[0]);
	    }
	    
	    System.out.println("Current Profile is "+speakerManager.getCurrentSpeaker());
	    
	    AudioFileSource source = new AudioFileSource(new File("resources\\hello_world.wav"));
	    
	    System.out.println("file fmt="+source.getAudioFormat());
	    System.out.println("rec fmt="+audioMan.getAudioFormat());
	    
	    //convert to the recognizer audio format
	    new AudioFormatConverter(source, audioMan, true);
	    
	    // need to use an AudioConverter as above - the following line used
	    // in place of the above line will throw an Exception if the AudioManager
	    // and source have different AudioFormats
	    //audioMan.setSource(source);
	    
	    rec.commitChanges();
	    rec.requestFocus();
	    rec.waitEngineState(rec.LISTENING);
	    
	    source.startSending();
	    System.out.println("sending");
	    
	    source.drain();
	    System.out.println("drained");
	    
	    //deallocate after 10 seconds - in case nothing was recognized
	    Thread killThread = new Thread() {
		public void run() {
		    try {
			sleep(10000);
			System.out.println("Given up waiting for an Accepted Result");
			System.out.println("disabling dictation after audio data finished");
			dictation.setEnabled(false);
			if(!rec.testEngineState(rec.DEALLOCATED) &&
			!rec.testEngineState(rec.DEALLOCATING_RESOURCES)) {
			    rec.commitChanges();
			    rec.waitEngineState(rec.LISTENING);
			    sleep(5000);
			}
			System.out.println("Forcing finalize\n");
			//forceFinalize causes a RESULT_ACCEPTED event to be sent, and
			//the ResultListener will deallocate the recognizer
			rec.forceFinalize(true);
			System.out.println("Forced finalize\n");
			rec.deallocate();
			System.out.println("deallocating\n");
		    } catch(Exception e) {
			e.printStackTrace();
		    }
		}
	    };
	    killThread.start();
	    
	    rec.waitEngineState(Recognizer.DEALLOCATED);
	    //one recognition and the ResultListener deallocates
	    
	    System.out.println("All done");
	    
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
