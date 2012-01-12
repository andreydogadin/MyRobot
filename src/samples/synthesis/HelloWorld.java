package samples.synthesis;

import samples.*;
import com.cloudgarden.speech.userinterface.*;

import javax.speech.*;
import javax.speech.synthesis.*;

/**
 * Demonstrates simple speech synthesis
 */
public class HelloWorld {
    
    public static void main(String[] args) {
	Synthesizer synth = null;
	try {
	    
	    SynthesizerModeDesc desc = null;
	    SpeechEngineChooser chooser = null;
	    try {
		chooser = SpeechEngineChooser.getSynthesizerDialog();
		chooser.show();
		desc = chooser.getSynthesizerModeDesc();
	    } catch(NoClassDefFoundError e) {
		System.out.println("Can't find Swing - try using Java 2 to see the SpeechEngineChooser");
	    }
	    synth = Central.createSynthesizer(desc);
	    ((com.cloudgarden.speech.CGEngineProperties)
	    synth.getSynthesizerProperties()).setEventsInNewThread(false);
	    synth.addEngineListener(new TestEngineListener());
            SpeakableListener spList = new TestSpeakableListener();
	    //synth.addSpeakableListener(spList);
	    
	    synth.allocate();
	    synth.resume();
	    synth.waitEngineState(Synthesizer.ALLOCATED);
	    
	    Voice v = null;
	    if(chooser != null) v = chooser.getVoice();
	    //Get a female voice if the chooser was not created or no voice was selected
	    if(v == null) new Voice(null,Voice.GENDER_FEMALE, Voice.AGE_DONT_CARE, null);
	    
	    System.out.println("Using voice "+v);
	    SynthesizerProperties props = synth.getSynthesizerProperties();
	    props.setVoice(v);
	    props.setVolume(1.0f);
	    props.setSpeakingRate(200.0f);
	    
	    synth.speak("",null);
	    synth.waitEngineState(synth.QUEUE_EMPTY);
	    synth.speak("Hello world!",spList);
	    synth.waitEngineState(synth.QUEUE_EMPTY);
	    synth.speak("How are you?",spList);
	    synth.waitEngineState(synth.QUEUE_EMPTY);
	    
	} catch(Exception e) {
	    e.printStackTrace(System.out);
	} catch(Error e1) {
	    e1.printStackTrace(System.out);
	} finally {
	    try {
		synth.deallocate();
		synth.waitEngineState(synth.DEALLOCATED);
	    } catch(Exception e2) {}
	    System.exit(0);
	}
    }
}
