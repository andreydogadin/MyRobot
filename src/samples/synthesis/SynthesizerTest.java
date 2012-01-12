package samples.synthesis;

import samples.*;

import javax.speech.*;
import javax.speech.synthesis.*;
import java.net.*;
import java.io.*;
import com.cloudgarden.speech.userinterface.SpeechEngineChooser;
import com.cloudgarden.speech.*;

/**
 * Demonstrates setting voices and synthesizer properties,
 * reading from a file containing JSML tags, and detecting
 * word boundaries and bookmarks.
 */
public class SynthesizerTest {
    
    public static void main(String[] args) {
	Synthesizer synth = null;
	try {
	    
	    CGEngineProperties.useFixForInfovox330(true);
	    
	    SynthesizerModeDesc desc = new SynthesizerModeDesc(null,"SAPI5",null,null,null);
	    SpeechEngineChooser chooser = null;
	    try {
		chooser = SpeechEngineChooser.getSynthesizerDialog();
		chooser.show();
		desc = chooser.getSynthesizerModeDesc();
	    } catch(NoClassDefFoundError e) {
		System.out.println("Swing classes not found - continuing anyway");
	    }
	    
	    synth = Central.createSynthesizer(desc);
	    desc = (SynthesizerModeDesc)synth.getEngineModeDesc();
	    synth.addEngineListener(new TestEngineListener());
	    
	    SpeakableListener spList = new TestSpeakableListener();
	    synth.addSpeakableListener(spList);
	    
	    synth.allocate();
	    synth.resume();
	    synth.waitEngineState(Synthesizer.ALLOCATED);
	    SynthesizerProperties props = synth.getSynthesizerProperties();
	    props.setSpeakingRate(160.0f);
	    
	    System.out.println("Listing voices:");
	    Voice[] vs = desc.getVoices();
	    for(int i=0;i<vs.length;i++) {
		System.out.println("   Voice :"+vs[i].getName()+":");
	    }
	    
	    if(chooser != null) props.setVoice(chooser.getVoice());
	    
	    synth.speak("<emp>Hello</emp> great  big round World, and how are you today?",null);
	    
	    Thread.currentThread().sleep(2000);
	    synth.pause();
	    System.out.println("Pausing for two seconds.");
	    Thread.currentThread().sleep(2000);
	    synth.resume();
	    
	    synth.waitEngineState(synth.QUEUE_EMPTY);
	    
	    synth.speak("and Hello World again",null);
	    synth.waitEngineState(Synthesizer.QUEUE_EMPTY);
	    
	    synth.speak(" this sentence should be cancelled if it goes on for more than 2 seconds.",null);
	    Thread.currentThread().sleep(2000);
	    synth.cancelAll();
	    
	    synth.speak(" this sentence should be spoken in it's place.",null);
	    synth.waitEngineState(Synthesizer.QUEUE_EMPTY);
	    
	    synth.speak("Here are all my voices",null);
	    synth.waitEngineState(Synthesizer.QUEUE_EMPTY);
	    
	    String name;
	    for(int i=0;i<vs.length;i++) {
		props.setVoice(vs[i]);
		name = vs[i].getName();
		System.out.println("name="+name);
		synth.speak("Hello",null);
		synth.waitEngineState(Synthesizer.QUEUE_EMPTY);
	    }
	    
	    props.setVoice(chooser.getVoice());
	    synth.speak("Now I'll read from a file.",null);
	    synth.waitEngineState(Synthesizer.QUEUE_EMPTY);
	    URL jsml=new URL("file:///"+new File("resources\\testJSML.xml").getAbsolutePath());
	    System.out.println("speaking file contents");
	    synth.speak(jsml,null);
	    synth.waitEngineState(Synthesizer.QUEUE_EMPTY);
	    
	} catch(Exception e) {
	    e.printStackTrace(System.out);
	} catch(Error e1) {
	    e1.printStackTrace(System.out);
	} finally {
	    try {
		synth.deallocate();
		synth.waitEngineState(Synthesizer.DEALLOCATED);
	    } catch(Exception e2) {}
	    System.exit(0);
	}
    }
}
