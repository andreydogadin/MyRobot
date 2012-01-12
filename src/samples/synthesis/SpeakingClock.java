package samples.synthesis;

import javax.speech.*;
import javax.speech.synthesis.*;
import java.util.*;
import java.text.*;
import com.cloudgarden.speech.userinterface.*;

/**
 * Demonstrates simple speech synthesis, but also the
 * addition of a word (with pronunciation)
 * to the VocabManager, and JSML tags in the speech string.
 */
public class SpeakingClock {
    
    public static void main(String[] args) {
        Synthesizer synth = null;
        try {
            SpeechEngineChooser chooser = SpeechEngineChooser.getSynthesizerDialog();
            chooser.show();
            SynthesizerModeDesc desc = chooser.getSynthesizerModeDesc();
            synth = Central.createSynthesizer(desc);
            
            desc = (SynthesizerModeDesc)synth.getEngineModeDesc();
            
            SpeakableListener spList = new TestSpeakableListener();
            synth.addSpeakableListener(spList);
            synth.allocate();
            synth.resume();
            synth.waitEngineState(Synthesizer.ALLOCATED);
            SynthesizerProperties props = synth.getSynthesizerProperties();
            
            props.setVoice(chooser.getVoice());
            props.setVolume(1.0f);
            props.setSpeakingRate(200.0f);
            
            boolean speaking = true;
            SimpleDateFormat df;
            df = new SimpleDateFormat("'the time is' h:mm 'and' ss 'seconds'");
            while(speaking) {
                Date d = new Date();
                if(d.getSeconds() % 10 == 0) {
                    synth.speak(df.format(d),null);
                    synth.waitEngineState(synth.QUEUE_EMPTY);
                }
                try {
                    System.out.print(" tick ... ");
                    Thread.currentThread().sleep(1000);
                } catch(Exception e) {}
            }
            
        } catch(Exception e) {
            e.printStackTrace(System.out);
        } catch(Error e1) {
            e1.printStackTrace(System.out);
        } finally {
            try {
                synth.deallocate();
                synth.waitEngineState(synth.DEALLOCATED);
                System.out.println("Deallocated");
            } catch(Exception e2) {}
            System.exit(0);
        }
    }
}
