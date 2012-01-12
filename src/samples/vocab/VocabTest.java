package samples.vocab;

import samples.*;
import samples.recognition.TestResultListener;
import samples.synthesis.*;
import javax.speech.*;
import javax.speech.recognition.*;
import javax.speech.synthesis.*;
import java.util.*;
import java.text.*;

import com.cloudgarden.speech.userinterface.*;

/**
 * Demonstrates changing the pronunciation of "time" to "aw r" (hour)
 * for both speech synthesis and recognition. Note: some engines may
 * not recognize this phonetic spelling - please consult the Microsoft SAPI 5.0 Help
 * documentation under "American English Phoneme Representation"
 * or the "sapi4Pronunciations.html" or "sapi5Pronunciations.html" files.
 */
public class VocabTest {
    public static Recognizer rec = null;
    public static Synthesizer synth = null;
    static VocabManager vm;
    static Word hour;
    
    public static void main(String[] args) {
        try {
            
            RecognizerModeDesc desc = new RecognizerModeDesc(null,Boolean.TRUE);
            SpeechEngineChooser chooser = null;
            try {
                chooser = SpeechEngineChooser.getAllEnginesDialog(desc,null);
                chooser.show();
            } catch(NoClassDefFoundError e) {
                System.out.println("Swing classes not found - continuing anyway");
            }
            if(chooser != null) desc = chooser.getRecognizerModeDesc();
            rec = Central.createRecognizer(desc);

            rec.addEngineListener(new TestEngineListener());
            rec.addResultListener(new TestResultListener(rec,3,false));
            rec.addResultListener(new ResultAdapter() {
                public void resultAccepted(final ResultEvent e) {
                    
                    Runnable lt = new Runnable() {
                        public void run() {
                            try {
                                FinalRuleResult r =  (FinalRuleResult)(e.getSource());
                                String tags[] = r.getTags();
                                SimpleDateFormat df;
                                Date d = new Date();
                                if(tags[0].equals("TIME")) {
                                    df = new SimpleDateFormat("h:mm a, zzzz");
                                    synth.speak("The time is "+df.format(d),null);
                                } else if(tags[0].equals("DATE")) {
                                    df = new SimpleDateFormat("EEE, MMM d, yyyy");
                                    synth.speak("The date is "+df.format(d),null);
                                } else return;
                                
                                synth.waitEngineState(Synthesizer.QUEUE_EMPTY);
                                rec.pause();
                                synth.getVocabManager().removeWord(hour);
                                
                                rec.getVocabManager().removeWord(hour);
                                rec.deallocate();
                                
                            } catch(Exception e1) {
                                e1.printStackTrace(System.out);
                            }
                        }
                    };
                    (new Thread(lt)).start();
                }
            } );
            
            RecognizerAudioAdapter raud = new TestAudioListener();
            rec.getAudioManager().addAudioListener(raud);
            
            rec.allocate();            
            rec.waitEngineState(Recognizer.ALLOCATED);
            
            SpeakerManager speakerManager = rec.getSpeakerManager();
            if(chooser != null) {
                SpeakerProfile prof = chooser.getSpeakerProfile();
                speakerManager.setCurrentSpeaker(prof);
            }
            System.out.println("Current Profile is "+speakerManager.getCurrentSpeaker());
            
            //Demonstrating adding a new pronunciation for an existing word
            //Capitalization is important!
            //For the phonetic symbols, refer to the Microsoft SAPI 5.0 Help
            //documentation under "American English Phoneme Representation"
            //or the "sapi4Pronunciations.html" or "sapi5Pronunciations.html" files.
	    
            //Test replacing "time" with "hour"
            String pron = "aw r";
            hour = new Word("time","time",new String[] {pron}, Word.NOUN);
            Word[] words;
            
            System.out.println("\nUsing recognizer "+rec.getEngineModeDesc());
            vm = rec.getVocabManager();
            System.out.println("Getting Recognizer's pronunciations of 'resume'");
            Word[] rwords = vm.getWords("resume");
            if(rwords != null) {
                for(int i=0;i<rwords.length;i++) System.out.println(rwords[i].toString());
            }
            
            vm.addWord(hour);
            
            words = vm.listProblemWords();
            if(words != null && words.length != 0) {
                for(int i=0;i<words.length;i++)
                    System.out.println("Problem with '"+words[i].getWrittenForm()+
                    "' ,  pronunciation='"+words[i].getPronunciations()[0]+"'");
            }
	    
            RuleSequence rs1 = new RuleSequence();
            RuleToken rt1 = new RuleToken("what");
            rs1.append(rt1);
            RuleTag rt2 = new RuleTag(new RuleToken("time"),"TIME");
            RuleTag rt3 = new RuleTag(new RuleToken("date"),"DATE");
            RuleAlternatives ra1 = new RuleAlternatives();
            ra1.append(rt2);
            ra1.append(rt3);
            rs1.append(ra1);
            RuleToken rt4 = new RuleToken("is it");
            rs1.append(rt4);
            RuleGrammar gram = rec.newRuleGrammar("time");
            gram.setRule("testRule",rs1,true);
            
            gram.setEnabled(true);
            
            RecognizerProperties rprops = rec.getRecognizerProperties();
            rprops.setResultAudioProvided(false);

	    SynthesizerModeDesc sdesc = chooser.getSynthesizerModeDesc();
	    synth = Central.createSynthesizer(sdesc);
	    
	    synth.addEngineListener(new TestEngineListener());
            
            synth.allocate();
            synth.waitEngineState(Synthesizer.ALLOCATED);

            synth.resume();
            
	    vm = synth.getVocabManager();
            vm.addWord(hour);
            words = vm.listProblemWords();
            if(words != null && words.length != 0) {
                for(int i=0;i<words.length;i++)
                    System.out.println("Problem with '"+words[i].getWrittenForm()+
                    "' ,  pronunciation='"+words[i].getPronunciations()[0]+"'");
            }
            
            SynthesizerProperties sprops = synth.getSynthesizerProperties();
            if(chooser != null) sprops.setVoice(chooser.getVoice());
            //Voice v = new Voice("Mary",Voice.GENDER_DONT_CARE, Voice.AGE_DONT_CARE, null);
            //sprops.setVoice(v);
            
            System.out.println("Using voice "+sprops.getVoice());
            rwords = synth.getVocabManager().getWords("resume");
            System.out.println("Getting Synthesizer's pronunciations of 'resume'");
            if(rwords != null) {
                for(int i=0;i<rwords.length;i++) System.out.println(rwords[i].toString());
            }
            
            SpeakableListener spList = new TestSpeakableListener();
            synth.addSpeakableListener(spList);
            
            SynthesizerProperties props = synth.getSynthesizerProperties();
            sprops.setVolume(1.0f);
            sprops.setSpeakingRate(200.0f);
            //            synth.speak("Synth is set up",null);
            
            rec.commitChanges();
            
            rec.requestFocus();
            rec.resume();
            
            //rec.waitEngineState(Recognizer.DEALLOCATED);
            //one recognition and we deallocate
            
            //See resultAccepted method for note about possible deadlock
            //synth.waitEngineState(Synthesizer.QUEUE_EMPTY);
            //synth.getVocabManager().removeWord(hour);
            
        } catch(Exception e) {
            e.printStackTrace(System.out);
        } catch(Error e1) {
            e1.printStackTrace(System.out);
        } finally {
            try {
                synth.deallocate();
                rec.deallocate();
                rec.waitEngineState(Synthesizer.DEALLOCATED);
                synth.waitEngineState(Synthesizer.DEALLOCATED);
            } catch(Exception e2) {
                e2.printStackTrace(System.out);
            }
            System.exit(0);
        }
    }
}
