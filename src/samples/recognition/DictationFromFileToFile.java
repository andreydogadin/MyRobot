package samples.recognition;

import samples.*;
import javax.sound.sampled.*;
import javax.speech.*;
import javax.speech.recognition.*;
import java.io.*;
import com.cloudgarden.audio.*;
import com.cloudgarden.speech.*;
import com.cloudgarden.speech.userinterface.SpeechEngineChooser;

/**
 * Reads audio data from a file, and uses an AudioSplitter to send the
 * data to a recognizer as well as another file, saving the audio data
 * in the same format as the recognizer, which may be different from the
 * input file.
 */
public class DictationFromFileToFile {
    static Recognizer rec = null;
    
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
	    
	    rec.allocate();
	    rec.waitEngineState(Recognizer.ALLOCATED);
	    
	    RecognizerProperties props = rec.getRecognizerProperties();
	    props.setNumResultAlternatives(5);
	    props.setResultAudioProvided(true);
	    
	    DictationGrammar dictation;
	    dictation = rec.getDictationGrammar("dictation");
	    dictation.setEnabled(true);
	    //Deallocate after one recognition.
	    dictation.addResultListener(new TestResultListener(rec,1,true));
	    
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
	    
	    //must suspend reco before calling setSource, and resume afterwards
	    rec.suspend();

	    //Get input from a file
	    AudioFileSource source = new AudioFileSource(new File("resources\\hello_world.wav"));

	    //convert from the file's audio format to the recognizer's audio format
	    AudioFormatConverter conv = new AudioFormatConverter();
	    conv.setAudioFormat(audioMan.getAudioFormat());
	    conv.setSource(source);
	    System.out.println("audioMan format = "+audioMan.getAudioFormat());

	    //send the output from the converter into a splitter
	    AudioSplitter splitter = new AudioSplitter();
	    splitter.setSource(conv );
	    
	    //add the AudioManager to the splitter
	    splitter.addSink(audioMan);

	    // add a file sink to the splitter (since the data is coming from the converter, this
	    // file will have the same audio format as the recognizer, and so may be different from
	    // the input file's format.
	    AudioFormat fmt = audioMan.getAudioFormat();
	    AudioFileFormat ff = new AudioFileFormat(AudioFileFormat.Type.WAVE, fmt, AudioSystem.NOT_SPECIFIED);
	    AudioFileSink sink = new AudioFileSink(new File("reco-copy.wav"),ff);
	    splitter.addSink(sink);
	    
	    //start the recognizer
	    rec.resume();
	    rec.requestFocus();
	    rec.waitEngineState(rec.FOCUS_ON);
	    
	    //start the source (which starts all the intermediate sources - the converter and splitter)
	    source.startSending();
	    System.out.println("sending");
	    //wait till the 
	    audioMan.drain();
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
