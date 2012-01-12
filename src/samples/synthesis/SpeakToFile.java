package samples.synthesis;

import javax.speech.*;
import javax.speech.synthesis.*;
import java.io.*;
import javax.sound.sampled.*;
import com.cloudgarden.audio.*;
import com.cloudgarden.speech.*;
import com.cloudgarden.speech.userinterface.SpeechEngineChooser;

/**
 * Demonstrates the new methods in AudioManager to save speech to
 * a wave file.
 */
public class SpeakToFile {
    public static void main(String args[]) {
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
	    props.setSpeakingRate(120);
	    
	    //Use something like this to set a voice without using the chooser
	    //String name = "Mike, SAPI4, Microsoft"   ;
	    //Voice v1 = new Voice(name,Voice.GENDER_DONT_CARE, Voice.AGE_DONT_CARE, "");
	    Voice v1 = chooser.getVoice();
	    props.setVoice(v1);
	    synth.speakPlainText("Getting ready",null);
	    //Wait till all done or you'll speak to the file...
	    synth.waitEngineState(Synthesizer.QUEUE_EMPTY);
	    
	    AudioFormat fmt;
	    
	    //could set the file's format to that of the synthesizer (better results) or a specific format
	    //fmt = new AudioFormat(11000,8,1,true,false);
	    //fmt = new AudioFormat(16000,16,1,true,false);
	    fmt = audioMan.getAudioFormat();
	    
	    System.out.println("File Format = "+fmt);
	    System.out.println("Synth Format = "+audioMan.getAudioFormat());
	    
	    AudioFileFormat ff;
	    ff = new AudioFileFormat(AudioFileFormat.Type.WAVE, fmt, AudioSystem.NOT_SPECIFIED);
	    
	    try {
		AudioFileSink sink = new AudioFileSink(new File("resources\\hello_world.wav"),ff);
		
		//connect synthesizer to sink with an AudioFormatConverter (if formats different)
		new AudioFormatConverter(audioMan, sink, true);
		
		//or, if formats are the same, just join source and sink
		//sink.setSource(audioMan);
		
		audioMan.startSending();
		
		synth.speak("Hello World, and how are you today?",null);
		
		synth.waitEngineState(Synthesizer.QUEUE_EMPTY);
		System.out.println("\nqueue empty");
		//need to close the output file otherwise the results are uncertain
		audioMan.closeOutput();
		sink.drain();
		
		System.out.println("\nflushed");
	    } catch(Exception e) {
		//If the file cannot be opened (eg if it is already opened by
		//another application) we will get an IOException
		e.printStackTrace();
	    }
	    
	    //Speak to a different file...
	    try {
		ff = new AudioFileFormat(AudioFileFormat.Type.WAVE, fmt, AudioSystem.NOT_SPECIFIED);
		AudioFileSink sink = new AudioFileSink(new File("reply.wav"),ff);
		audioMan.setSink(sink);
		
		synth.speakPlainText("pretty good, thanks, and you?",null);
		
		audioMan.startSending();
		synth.waitEngineState(Synthesizer.QUEUE_EMPTY);
		audioMan.closeOutput();
		sink.drain();
		//If the "All done" words are not spoken next, uncomment the sleep line to
		//let the AudioFileSink drain properly... this is a temporary workaround.
		//Thread.currentThread().sleep(200);
		System.out.println("\nflushed");
	    } catch(Exception e) {
		e.printStackTrace();
	    }
	    
	    //...and now speak to the default audio device
	    
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
	    System.out.println("All Done - now play the audio files");
	    System.out.println("resources\\hello_world.wav and reply.wav to hear if it worked");
	}
	//...before closing up shop
	System.exit(0);
    }
}

