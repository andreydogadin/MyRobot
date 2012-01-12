package samples.recognition;

import samples.*;
import javax.speech.*;
import javax.speech.recognition.*;
import java.io.*;
import com.cloudgarden.audio.*;
import com.cloudgarden.speech.*;
import com.cloudgarden.speech.userinterface.SpeechEngineChooser;

/**
 * Tests out recognition of various words or phrases (programmed as rules in a RuleGrammar)
 * from a wave file.
 */
public class CommandsFromFile {
    static Recognizer rec = null;
    
    public static void main(String[] args) {
	try {
	    
	    RecognizerModeDesc desc = new RecognizerModeDesc(null,null);
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
	    
	    rec.allocate();
	    rec.waitEngineState(Recognizer.ALLOCATED);
	    
	    RecognizerProperties props = rec.getRecognizerProperties();
	    props.setNumResultAlternatives(5);
	    //props.setCompleteTimeout(0.1f);
	    //props.setIncompleteTimeout(0.2f);
	    props.setConfidenceLevel(0.2f);
	    
	    //Retain audio so it can be played back later (see ResultListener)
	    props.setResultAudioProvided(true);
	    
	    RuleGrammar gram = rec.newRuleGrammar("testGram");
	    RuleToken r1 = new RuleToken("hello world");
	    RuleToken r2 = new RuleToken("and how are you today");
	    RuleToken r3 = new RuleToken("how are you");
	    RuleToken r4 = new RuleToken("hello world and how are you today");
	    RuleToken r5 = new RuleToken("world");
	    RuleToken r6 = new RuleToken("and how are you");
	    RuleToken r7 = new RuleToken("today");
	    gram.setRule("hi",new RuleTag(r1, "HI1"),true);
	    gram.setRule("whatsup",new RuleTag(r2, "HI2"),true);
	    gram.setRule("whatsup2",new RuleTag(r3, "HI3"),true);
	    gram.setRule("whatsup3",new RuleTag(r4, "HI4"),true);
	    gram.setRule("whatsup4",new RuleTag(r5, "HI5"),true);
	    gram.setRule("whatsup5",new RuleTag(r6, "HI6"),true);
	    gram.setRule("whatsup6",new RuleTag(r7, "HI7"),true);
	    gram.setEnabled(true);
	    gram.addResultListener(new TestResultListener(rec,1,false));
	    
	    //DictationGrammar dictation = rec.getDictationGrammar("dictation");
	    //dictation.setEnabled(true);

	    rec.suspend();
	    rec.commitChanges();
	    //wait till changes committed (signalled by LISTENING state)
	    rec.waitEngineState(rec.LISTENING);
	    
	    System.out.println("Using engine "+rec.getEngineModeDesc());
	    SpeakerManager speakerManager = rec.getSpeakerManager();
	    if(chooser != null) {
		SpeakerProfile prof = chooser.getSpeakerProfile();
		speakerManager.setCurrentSpeaker(prof);
	    }
	    System.out.println("Current Profile is "+speakerManager.getCurrentSpeaker());
	    //let the engine decide who is speaking (only works for SAPI4 engines)
	    
	    ((com.cloudgarden.speech.CGEngineProperties)props).allowGuessingOfSpeaker(true);
	    	    
	    //must suspend reco before calling setSource, and resume afterwards
	    rec.suspend();
	    AudioFileSource source = new AudioFileSource(new File("resources\\hello_world.wav"));
	    new AudioFormatConverter(source, audioMan );
	    rec.requestFocus();
	    rec.resume();
	    rec.waitEngineState(rec.FOCUS_ON);

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
			if(!rec.testEngineState(rec.DEALLOCATED) &&
			!rec.testEngineState(rec.DEALLOCATING_RESOURCES)) {
			    System.out.println("Forcing finalize\n");
			    rec.forceFinalize(true);
			    rec.deallocate();
			}
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
