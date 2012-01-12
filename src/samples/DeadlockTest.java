package samples;

import samples.synthesis.*;

import javax.speech.*;
import javax.speech.synthesis.*;
import java.util.*;

import com.cloudgarden.speech.CGEngineProperties;

/**
 * Demonstrates how to produce deadlock via the waitEngineState method and
 * the CGEngineCentral setEventsInNewThread method.
 *
 * Experiment by including the line: 
 * ((CGEngineProperties)props).setEventsInNewThread(false);
 * in the code below.
 */
public class DeadlockTest {
    static Synthesizer synth = null;
    
    public static void main(String[] args) {
        try {
            SynthesizerModeDesc desc = new SynthesizerModeDesc(Locale.ENGLISH);
            synth = Central.createSynthesizer(desc);
            
            synth.addEngineListener(new TestEngineListener());
            synth.addSpeakableListener(new TestSpeakableListener());

            synth.addEngineListener(new SynthesizerAdapter() {
                public void queueUpdated(SynthesizerEvent e) {
                    System.out.println("wait till queue empty...");
                    try {
                        synth.waitEngineState(synth.QUEUE_EMPTY);
                    } catch(InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    System.out.println("...waiting ended");
                }
            });
            
            synth.allocate();
            synth.resume();
            synth.waitEngineState(Synthesizer.ALLOCATED);
            SynthesizerProperties props = synth.getSynthesizerProperties();
            
            //SAPI5 engine...
            //Voice v = new Voice("Microsoft Mary",Voice.GENDER_FEMALE,	Voice.AGE_DONT_CARE, null);
            //...or SAPI4 engine
            Voice v = new Voice("Mary",Voice.GENDER_FEMALE, Voice.AGE_DONT_CARE, null);
            props.setVoice(v);
            
            //This starts all events for this Synthesizer in the current Thread.
            //With this call, the waitEngineState call will prevent *any* other
            //speech events from being sent since it will halt the current thread
            
            ((CGEngineProperties)props).setEventsInNewThread(true);

            synth.speak("Hello World!",null);
            synth.waitEngineState(synth.QUEUE_EMPTY);
            System.out.println("all done");
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
