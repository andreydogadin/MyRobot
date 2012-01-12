package samples.synthesis;

import com.cloudgarden.speech.userinterface.*;

import javax.speech.*;
import javax.speech.synthesis.*;
import java.util.*;
import java.net.*;
import java.io.*;
import javax.sound.sampled.*;
import com.cloudgarden.speech.*;
import com.cloudgarden.audio.*;

/**
 * Reads in the text of Alice In Wonderland, Chapter 1, and saves it to a file
 */
public class ReadAliceToWAVEFile {
    
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
            
            synth.addSpeakableListener(new SpeakableAdapter() {
                public void wordStarted(SpeakableEvent ev) {
                    System.out.println(ev.getText().substring(ev.getWordStart(),ev.getWordEnd()));
                }
            });
            
            synth.allocate();
            synth.waitEngineState(Synthesizer.ALLOCATED);
            synth.resume();
            
            Voice v = null;
            if(chooser != null) v = chooser.getVoice();
            //Get a female voice if the chooser was not created or no voice was selected
            if(v == null) new Voice(null,Voice.GENDER_FEMALE, Voice.AGE_DONT_CARE, null);
            
            System.out.println("Using voice "+v);
            SynthesizerProperties props = synth.getSynthesizerProperties();
            props.setVoice(v);
            props.setVolume(1.0f);
            props.setSpeakingRate(200.0f);
            
            CGAudioManager audioMan = (CGAudioManager)synth.getAudioManager();
            AudioFormat fmt;
	    fmt = new AudioFormat(11000,8,1,true,false);
            System.out.println("File Format = "+fmt);
            
            AudioFileSink sink = new AudioFileSink(new File("alice_ch1.wav"),fmt, AudioFileFormat.Type.WAVE);
            new AudioFormatConverter(audioMan,sink);
            audioMan.startSending();
            
            Date start = new Date();
            System.out.println("STARTING");
            synth.speak(new URL("file:resources\\alice_ch1.txt"),null);
            synth.waitEngineState(synth.QUEUE_EMPTY);
            audioMan.closeOutput();
            sink.drain();
            System.out.println("DONE");
            Date end = new Date();
            System.out.println("That took "+((end.getTime()-start.getTime())/1000)+" seconds");
            
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
