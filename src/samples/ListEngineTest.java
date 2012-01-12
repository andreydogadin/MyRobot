package samples;

import javax.speech.*;
import javax.speech.recognition.*;
import javax.speech.synthesis.*;
import java.util.*;

/**
 * Lists all the available Synthesizers and their Voices, says "hello world" with each of the Voices,
 * and lists all Recognizers (with SpeakerProfiles)
 */
public class ListEngineTest {
    
    public static void main(String[] args) {

        EngineList list = Central.availableSynthesizers(null);
        for(int i = 0; i<list.size(); i++) {
            System.out.println("Synthesizer number "+(i+1)+"\n"+list.elementAt(i));
            SynthesizerModeDesc desc = (SynthesizerModeDesc)list.elementAt(i);
            try {
                Synthesizer synth = Central.createSynthesizer(desc);
                synth.allocate();
                synth.resume();
                synth.waitEngineState(Synthesizer.ALLOCATED);
                SynthesizerProperties props = synth.getSynthesizerProperties();
                //Need to get the Descriptor after you create the engine
                //in order to get the voices - remove comments below to hear all the voices
                desc = (SynthesizerModeDesc)synth.getEngineModeDesc();
                Voice[] vs = desc.getVoices();
                for(int j=0;j<vs.length;j++) {
                    System.out.println("Found voice \""+vs[j].getName()+"\"");
//                    props.setVoice(vs[j]);
//                    synth.speak("hello world",null);
//                    synth.waitEngineState(synth.QUEUE_EMPTY);
                }
                synth.deallocate();
                synth.waitEngineState(Synthesizer.DEALLOCATED);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        
        list = Central.availableSynthesizers(new SynthesizerModeDesc(Locale.SIMPLIFIED_CHINESE));
        for(int i = 0; i<list.size(); i++) {
            System.out.println("Chinese Synthesizer number "+(i+1)+"\n"+list.elementAt(i));
        }
        
        list = Central.availableSynthesizers(new SynthesizerModeDesc(Locale.JAPANESE));
        for(int i = 0; i<list.size(); i++) {
            System.out.println("Japanese Synthesizer number "+(i+1)+"\n"+list.elementAt(i));
        }
        
        list = Central.availableRecognizers(new RecognizerModeDesc(Locale.ENGLISH,Boolean.TRUE));
        for(int i = 0; i<list.size(); i++) {
            System.out.println("English Recognizer number "+(i+1)+"\n"+list.elementAt(i));
        }
        /*
        list = Central.availableRecognizers(new RecognizerModeDesc(Locale.SIMPLIFIED_CHINESE,Boolean.TRUE));
        for(int i = 0; i<list.size(); i++) {
            System.out.println("Chinese Recognizer number "+(i+1)+"\n"+list.elementAt(i));
        }
         */
        System.exit(0);
    }
}