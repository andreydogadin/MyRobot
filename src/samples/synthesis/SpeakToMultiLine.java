package samples.synthesis;

import javax.speech.*;
import javax.speech.synthesis.*;
import javax.sound.sampled.*;

import com.cloudgarden.speech.*;
import com.cloudgarden.speech.userinterface.*;
import com.cloudgarden.audio.*;

/**
 * An example which splits synthesized speech to three AudioLineSinks
 * and starts them at staggered intervals, so you can hear the three identical
 * but separate streams.
 */
public class SpeakToMultiLine {
    static Synthesizer synth = null;
    
    public static void main(String args[]) {
	try {
	    SpeechEngineChooser chooser = SpeechEngineChooser.getSynthesizerDialog();
	    chooser.show();
	    SynthesizerModeDesc desc = chooser.getSynthesizerModeDesc();
	    synth = Central.createSynthesizer(desc);
	    CGAudioManager audioMan = (CGAudioManager)synth.getAudioManager();
	    synth.allocate();
	    synth.resume();
	    TestSpeakableListener tsl = new TestSpeakableListener();
	    synth.addSpeakableListener(tsl);
	    
	    SynthesizerProperties props = synth.getSynthesizerProperties();
	    props.setVoice(chooser.getVoice());
	    
	    synth.speakPlainText("ready",null);
	    synth.waitEngineState(Synthesizer.QUEUE_EMPTY);
	    
	    AudioPipe[] pipes = new AudioPipe[3];
	    AudioFormat fmt;
	    fmt = audioMan.getAudioFormat();
	    System.out.println("Line format = "+fmt);
	    
	    int bps = (int)(2*fmt.getSampleRate());
	    AudioSplitter splitter = new AudioSplitter();
	    audioMan.setSink(splitter);
	    
	    for(int i=0;i<3;i++) {
		pipes[i] = new AudioPipe();
		//buffer must be big enough to hold 2*1.0 secs worth of data
		//(the time between the first line starting and the third line starting)
		//at 22000*2 bytes/sec
		pipes[i].setBufferSize(2*bps);
		pipes[i].setSink(new AudioLineSink(fmt));
		splitter.addSink(pipes[i]);
		pipes[i].setPaused(true);
	    }

	    audioMan.addTransferListener(new TransferListener() {
		public void bytesTransferred(TransferEvent evt) {
		    System.out.println("transferred "+evt.getLength());
		}
	    });
	    
	    synth.speakPlainText("Hello great big round world",null);
	    synth.speakPlainText("and how are you this fine day?",null);

	    audioMan.startSending();
	    
	    for(int i=0;i<3;i++) {
		pipes[i].setPaused(false);
		Thread.currentThread().sleep(1000);
	    }
	    System.out.println("waiting for empty queue");
	    synth.waitEngineState(Synthesizer.QUEUE_EMPTY);
	    System.out.println("queue empty");
	    audioMan.closeOutput();
	    System.out.println("output closed");
	    
	    //need to wait because output to stream occurs much faster than the
	    //SourceDataLine plays it
	    System.out.println("about to drain");
	    pipes[2].getSink().drain(); //wait for last line to finish
	    System.out.println("drained");
	    for(int i=0;i<3;i++) ((AudioLineSink)pipes[i].getSink()).close();
	    System.out.println("closed");
	    //...and now speak to the default audio device
	    
	    audioMan.setSink(null);
	    System.out.println("set output to default");
	    
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

