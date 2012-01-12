package samples.remote;

import samples.*;
import samples.recognition.*;
import javax.speech.*;
import javax.speech.recognition.*;

import com.cloudgarden.audio.*;
import com.cloudgarden.speech.*;

/**
 * This example demonstrates using an AudioServerSource object
 * to provide audio data to a CGAudioManager attached to a Recognizer
 * which it receives from a remote client.
 *<P>
 * Start up this class, then start a RemoteDictationClient on another
 * machine (after setting the host machine's name) which will send
 * audio data (using RMI) to the machine the RemoteDictationServer
 * (and Recognizer) is on.
 *
 */
public class RemoteDictationServer extends TestClientListener {
    static Recognizer rec = null;
    
    public static void main(String[] args) {
	new RemoteDictationServer().start();
    }
    
    public void start() {
	try {
	    rec = Central.createRecognizer(null);
	    rec.addEngineListener(new TestEngineListener());
	    rec.addResultListener(new TestResultListener(rec,3,false));
	    
	    RecognizerAudioAdapter raud = new TestAudioListener();
	    rec.getAudioManager().addAudioListener(raud);
	    rec.allocate();
	    rec.waitEngineState(Recognizer.ALLOCATED);
	    
	    CGAudioManager audioMan = (CGAudioManager)rec.getAudioManager();
	    AudioServerSource source = new AudioServerSource("RemoteDictationServer1",null,1099);
	    source.addClientListener(this);
	    source.addClientListener(new ClientAdapter() {
		public void clientRemoved(String client) {
		    try {
			if(!rec.testEngineState(rec.DEALLOCATED)) rec.deallocate();
		    } catch(Exception e) {
			e.printStackTrace();
		    }
		}
	    });
	    
	    audioMan.setSource(source);
	    source.startSending();
	    
	    RecognizerProperties props = rec.getRecognizerProperties();
	    props.setNumResultAlternatives(5);
	    
	    //Retain audio so it can be played back later (see ResultListener)
	    //but note that for the last-recognized result the audio will be cut short.
	    props.setResultAudioProvided(true);
	    
	    DictationGrammar dictation;
	    dictation = rec.getDictationGrammar("dictation");
	    dictation.setEnabled(true);
	    
	    rec.commitChanges();
	    rec.requestFocus();
	    System.out.println("\nGetting input from "+source+" for 20 seconds");
	    System.out.println("\nStart up RemoteDictationClient now !\n");
	    
	    final CGAudioManager am = audioMan;
	    final AudioServerSource fsrc = source;
	    
	    Thread defaultThread = new Thread() {
		public void run() {
		    try {
			sleep(20000);
		    } catch(Exception e) {}
		    try {
			fsrc.closeServer();
			if(!rec.testEngineState(rec.DEALLOCATED)) rec.deallocate();
		    } catch(Exception e) {
			e.printStackTrace();
		    }
		}
	    };
	    defaultThread.start();
	    
	    rec.waitEngineState(Recognizer.DEALLOCATED);
	    //three recognitions, or 20 seconds, and we deallocate
	    
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

