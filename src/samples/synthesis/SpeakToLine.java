package samples.synthesis;

import javax.speech.*;
import javax.speech.synthesis.*;
import javax.sound.sampled.*;

import com.cloudgarden.speech.*;
import com.cloudgarden.audio.*;
import com.cloudgarden.speech.userinterface.*;

/**
 * Demonstrates the new methods in AudioManager to play speech to
 * a line.
 */
public class SpeakToLine {
    public static void main(String args[]) {
	Synthesizer synth = null;
	try {
	    SpeechEngineChooser chooser = SpeechEngineChooser.getSynthesizerDialog();
	    chooser.show();
	    SynthesizerModeDesc desc = chooser.getSynthesizerModeDesc();
	    synth = Central.createSynthesizer(desc);
	    CGAudioManager audioMan = (CGAudioManager)synth.getAudioManager();
	    TestSpeakableListener tsl = new TestSpeakableListener();
	    synth.addSpeakableListener(tsl);
	    synth.allocate();
	    synth.resume();
	    
	    SynthesizerProperties props = synth.getSynthesizerProperties();
	    props.setVoice(chooser.getVoice());
	    
	    synth.speakPlainText("ready",null);
	    synth.waitEngineState(Synthesizer.QUEUE_EMPTY);
	    
	    try {
		AudioFormat fmt = new AudioFormat(8000,8,1,true,false);
		AudioLineSink sink = new AudioLineSink(fmt);
		
		new AudioFormatConverter(audioMan, sink, false, true);
		
		System.out.println("Synth format = "+audioMan.getAudioFormat());
		System.out.println("Line format = "+fmt);
		audioMan.addTransferListener(new TransferListener() {
		    public void bytesTransferred(TransferEvent evt) {
			System.out.println("transferred "+evt.getLength());
		    }
		});
		audioMan.startSending();
		
		synth.speakPlainText("Hello world",null);
		synth.waitEngineState(synth.QUEUE_EMPTY);
		
		audioMan.closeOutput();
		sink.drain();
		
		//Speak to the line again, but this time don't smooth in the conversion
		sink = new AudioLineSink(fmt);
		new AudioFormatConverter(audioMan, sink, false, false);
		
		audioMan.startSending();
		synth.speakPlainText("How are you this fine day?",null);
		
		synth.waitEngineState(Synthesizer.QUEUE_EMPTY);
		System.out.println("\nqueue empty");
		
		//need to close the output from the AudioManager so the sink will drain
		audioMan.closeOutput();
		
		//and wait for the line to finish handling the data
		sink.drain();
		System.out.println("sink drained");
	    } catch(Exception e) {
		e.printStackTrace();
	    }
	    
	    
	    //...and now speak to the default audio device
	    
	    try {
		audioMan.setSink(null);
		//audioMan.setDefaultOutput();
	    } catch(Exception e) {
		e.printStackTrace();
	    }
	    
	    synth.speakPlainText("and now I'm all done",null);
	    
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
	}
	//...before closing up shop
	System.exit(0);
    }
}

