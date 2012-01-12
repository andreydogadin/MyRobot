package samples.synthesis;

import javax.speech.*;
import javax.speech.synthesis.*;
import java.io.*;
import java.net.*;

import com.cloudgarden.audio.*;
import com.cloudgarden.speech.*;
import com.cloudgarden.speech.userinterface.SpeechEngineChooser;

import javax.media.protocol.*;

/**
 * Demonstrates the new methods in AudioManager to save speech to
 * an MPEG file using an AudioMediaURLSink
 */
public class SpeakToMPEGFile  {
    
    public static void main(String args[]) {
	SpeakToMPEGFile mp3 = new SpeakToMPEGFile();
	mp3.speak();
    }
    
    public void speak() {
	Synthesizer synth = null;
	try {
	    SpeechEngineChooser chooser = SpeechEngineChooser.getSynthesizerDialog();
	    chooser.show();
	    SynthesizerModeDesc desc = chooser.getSynthesizerModeDesc();
	    synth = Central.createSynthesizer(desc);
	    
	    CGAudioManager audioMan = (CGAudioManager)synth.getAudioManager();
	    TestSpeakableListener listener = new TestSpeakableListener();
	    synth.addSpeakableListener(listener);
	    
	    synth.allocate();
	    synth.waitEngineState(synth.ALLOCATED);
	    
	    synth.resume();
	    
	    SynthesizerProperties props = synth.getSynthesizerProperties();
	    String name;
	    //            name = "Microsoft Mike, SAPI5, Microsoft";
	    //            name = "English-American: Sandy (Child), SAPI4, IBM"   ;
	    //            name = "Adult Female #1 British English (L&H), SAPI4, Lernout & Hauspie";
	    //            name = "Mike, SAPI4, Microsoft"   ;
	    //            Voice v1 = new Voice(name,Voice.GENDER_DONT_CARE, Voice.AGE_DONT_CARE, "");
	    Voice v1 = chooser.getVoice();
	    props.setVoice(v1);
	    
	    synth.speakPlainText("Getting ready",null);
	    //Wait till all done or you'll speak to the file...
	    synth.waitEngineState(Synthesizer.QUEUE_EMPTY);
	    
	    System.out.println("Synthesizer Format = "+audioMan.getAudioFormat());
	    
	    AudioMediaURLSink sink = new AudioMediaURLSink(new URL("file:resources\\hello_world.mp3"), FileTypeDescriptor.MPEG_AUDIO);
	    audioMan.setSink(sink);
	    
	    System.out.println("Ready to speak");
	    
	    try {
		synth.speak("Hello World,",null);
		synth.speak("and how are you today?",null);
		synth.waitEngineState(Synthesizer.QUEUE_EMPTY);
		
		System.out.println("\nqueue empty");
		//need to close the output file otherwise the results are uncertain
		audioMan.closeOutput();
		System.out.println("\ndone");
		
	    } catch(Exception e) {
		//If the file cannot be opened (eg if it is already opened by
		//another application) we will get an IOException
		e.printStackTrace();
	    }
	    
	    //...and now speak to the default audio device
	    //synth.addSpeakableListener(listener);
	    try {
		audioMan.setDefaultOutput();
	    } catch(IOException e) {
		e.printStackTrace();
	    }
	    synth.speakPlainText("All done",null);
	    synth.waitEngineState(Synthesizer.QUEUE_EMPTY);
	    
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    try {
		synth.deallocate();
		//wait till we are deallocated...
		synth.waitEngineState(Engine.DEALLOCATED);
	    } catch(Exception e2) {
		e2.printStackTrace();
	    }
	    System.out.println("All Done - now play the audio file");
	    System.out.println("resources\\hello_world.mp3 to hear if it worked");
	}
	//...before closing up shop
	System.exit(0);
    }
}

