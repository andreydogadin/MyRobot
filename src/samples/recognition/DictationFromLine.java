package samples.recognition;

import samples.*;
import javax.speech.*;
import javax.speech.recognition.*;
import javax.sound.sampled.*;

import com.cloudgarden.audio.*;
import com.cloudgarden.speech.*;
import com.cloudgarden.speech.userinterface.*;

/**
 * Receives input from a filtered line (demonstrates the AudioFilterSource
 * for reducing power-line hum).
 */
public class DictationFromLine {
    static Recognizer rec = null;
    
    public static void main(String[] args) {
        try {
            RecognizerModeDesc desc = new RecognizerModeDesc(null,Boolean.TRUE);
            SpeechEngineChooser chooser = SpeechEngineChooser.getRecognizerDialog(desc);
            chooser.show();
            desc = chooser.getRecognizerModeDesc();
            rec = Central.createRecognizer(desc);
            //            rec = Central.createRecognizer(null);
            rec.addEngineListener(new TestEngineListener());
            //Set the ResultListener to not play back the audio
            rec.addResultListener(new TestResultListener(rec,2,false));
            
            CGAudioManager audioMan = (CGAudioManager)rec.getAudioManager();
            audioMan.addAudioListener(new TestAudioListener());
            
            rec.allocate();
            rec.waitEngineState(Recognizer.ALLOCATED);
            System.out.println("Using engine "+rec.getEngineModeDesc());
            SpeakerManager speakerManager = rec.getSpeakerManager();
            SpeakerProfile prof = chooser.getSpeakerProfile();
            speakerManager.setCurrentSpeaker(prof);
            System.out.println("Changed Current Profile to "+speakerManager.getCurrentSpeaker());
            
            DictationGrammar dictation;
            dictation = rec.getDictationGrammar("dictation");
            dictation.setEnabled(true);
            
            rec.suspend();
            rec.commitChanges();
            rec.waitEngineState(rec.LISTENING);
            
            //audioMan = (CGAudioManager)rec.getAudioManager();
            
            AudioFormat fmt  = audioMan.getAudioFormat();
            AudioLineSource source = null;
            try {
                source = new AudioLineSource(fmt);
                audioMan.setSource(source);
            } catch(Exception e) {
                System.out.println("unable to create DataLine with format "+fmt);
                fmt = new AudioFormat(16000,16,1,true, false);
                System.out.println("trying to create DataLine with format "+fmt);
                source = new AudioLineSource(fmt);
                new AudioFormatConverter(source, audioMan);
            }
            
            rec.requestFocus();
            source.startSending();
            rec.waitEngineState(Recognizer.DEALLOCATED);
            //must close the line or jre 1.2.2 causes an error.
            source.stopSending();
            source.close();
            //source.getLine().stop();
            //source.getLine().close();
            //two recognitions and we deallocate
            System.out.println("All done");
            //wait for captured audio to finish playing (when playback is enabled)
            //Thread.currentThread().sleep(2500);
            
        } catch(Exception e) {
            e.printStackTrace(System.out);
        } catch(Error e1) {
            e1.printStackTrace(System.out);
        } finally {
            try {
                rec.deallocate();
            } catch(Exception e2) {
                e2.printStackTrace(System.out);
            }
            System.exit(0);
        }
    }
}
