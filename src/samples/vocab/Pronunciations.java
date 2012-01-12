package samples.vocab;

import samples.*;
import samples.synthesis.*;
import com.cloudgarden.speech.userinterface.*;

import javax.speech.*;
import javax.speech.synthesis.*;
import java.util.*;

/**
 * Demonstrates simple speech synthesis, but also the
 * addition of a word (with pronunciation)
 * to the VocabManager, and JSML tags in the speech string, and
 * oddities of the SAPI4 engines.
 */
public class Pronunciations {
    
    public static void main(String[] args) {
        Synthesizer synth = null;
        try {
            Locale locale;
            //This will load L&H SAPI4 German TTS engines (if installed)
            //locale = Locale.GERMAN;

            //This loads the Microsoft SAPI4 and SAPI5 US English engines
            locale = Locale.US;
            
            //This loads the L&H SAPI4 British English TTS engines
            //locale = Locale.UK;
            
            SynthesizerModeDesc desc = new SynthesizerModeDesc(locale);
            
            SpeechEngineChooser chooser = null;
            try {
                chooser = SpeechEngineChooser.getSynthesizerDialog();
                chooser.show();
                desc = chooser.getSynthesizerModeDesc();
            } catch(NoClassDefFoundError e) {
                e.printStackTrace();
            }
            
            synth = Central.createSynthesizer(desc);
            synth.addEngineListener(new TestEngineListener());
            synth.addSpeakableListener(new TestSpeakableListener());

            synth.allocate();
            synth.resume();
            synth.waitEngineState(Synthesizer.ALLOCATED);
            
            Voice v = null;
            if(locale.equals(Locale.US)) {
                //load a SAPI5 engine...
                //v = new Voice("Microsoft Mary",Voice.GENDER_FEMALE,	Voice.AGE_DONT_CARE, null);
                //...or a Microsoft SAPI4 engine
                v = new Voice("Mary",Voice.GENDER_FEMALE, Voice.AGE_DONT_CARE, null);
            } else if(locale.equals(Locale.UK)) {
                //...or a L&H SAPI4 TTS engine
                v = new Voice("Adult Female #1 British English (L&H)",Voice.GENDER_FEMALE, Voice.AGE_DONT_CARE, null);
            } else {
                v = new Voice("",Voice.GENDER_DONT_CARE, Voice.AGE_DONT_CARE, null);
            }
           if(chooser != null) v = chooser.getVoice();
            
            System.out.println("Using voice "+v);
            
            SynthesizerProperties props = synth.getSynthesizerProperties();
            props.setVoice(v);
            props.setVolume(1.0f);
            props.setSpeakingRate(200.0f);

            String pron;
            //These two pronounciations (for "guys and goyls") can be used, and have the same result
            pron = "g ay z ah n d g oy l z";
	    pron = "aw r";
            //pron = "\u0067\u0251\u026A\u007A\u0020\u028C\u006E\u0064\u0020\u0067\u0254\u026A\u006C\u007A";
            
            Word world = new Word("world","world",new String[] {pron}, Word.NOUN);
            VocabManager vm = synth.getVocabManager();
            //Note: addWord doesn't work with the L&H SAPI4 TTS engines
            vm.addWord(world);

            Word[] words = vm.listProblemWords();
            if(words != null && words.length != 0) {
                for(int i=0;i<words.length;i++) 
                    System.out.println("Problem with '"+words[i].getWrittenForm()+
                    "' ,  pronunciation='"+words[i].getPronunciations()[0]+"'");
            }
            
            //this works with the L&H SAPI4 TTS engines (but doesn't sound quite like "howdy")
            //synth.speak("<sayas phon=\"\u0068\u006F\u0064\u0069\">hello</sayas> world!",null);
            
            //this also works with the L&H SAPI4 TTS engines (you can mix unicode with Microsoft's phonetic symbols)
            synth.speak("<sayas phon=\"\u0068ow\u0064\u0069\">hello</sayas> world!",null);

            synth.speak("and hello world again",null);
            // This demonstrates that Microsofts's SAPI4 engines ignore the SAYAS tag ...
            //   (they say "hello guys and goyls, and hello guys and goyls again")
            // ...and that the L&H engines ignore the addWord call
            //   (they say "howdy world, and hello world again")
            //(Switch between Microsoft and L&H by changing the local from US to UK)
            synth.waitEngineState(synth.QUEUE_EMPTY);

            vm.removeWord(world);
            
            Word[] rwords = synth.getVocabManager().getWords("resume");
            System.out.println("Getting pronunciations of 'resume'");
            for(int i=0;i<rwords.length;i++) System.out.println("Got words "+rwords[i]);
            
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
