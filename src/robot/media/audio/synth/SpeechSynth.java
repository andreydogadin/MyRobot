package robot.media.audio.synth;

import javax.speech.Central;
import javax.speech.EngineList;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.SynthesizerProperties;
import javax.speech.synthesis.Voice;

/**
 * Created by IntelliJ IDEA.
 * User: andrey.dogadin
 * Date: 8/12/11
 * Time: 10:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class SpeechSynth {
    private static Synthesizer synth = null;
    private static Voice voice = null;

    public SpeechSynth() {
        InitSynth();
    }

    private static void InitSynth() {
        try {
            EngineList list = Central.availableSynthesizers(null);
            SynthesizerModeDesc desc = null;
            for (int i = 0; i < list.size(); i++) {
                desc = (SynthesizerModeDesc) list.elementAt(i);
                Voice[] voices = desc.getVoices();
                for (int j = 0; j < voices.length; j++) {
                    if (voices[j].getName().indexOf("SAPI5") > 0) {
                        voice = voices[j];
                        break;
                    }
                }
            }

            if (voice == null) {
                System.out.println("Unable to find a SAPI5 voice - quitting");
                System.exit(0);
            }

            synth = Central.createSynthesizer(desc);
            synth.allocate();
            synth.resume();
            synth.waitEngineState(Synthesizer.ALLOCATED);
            System.out.println("Using voice " + voice);
            SynthesizerProperties props = synth.getSynthesizerProperties();
            props.setVoice(voice);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void read(String text) {
        if (synth == null) InitSynth();
        try {
            synth.speak(text, null);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void Release() {
        try {
            synth.waitEngineState(synth.QUEUE_EMPTY);
            synth.deallocate();
            synth.waitEngineState(synth.DEALLOCATED);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
