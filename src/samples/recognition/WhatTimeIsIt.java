package samples.recognition;

import samples.*;
import samples.synthesis.*;
import com.cloudgarden.speech.userinterface.*;
import javax.speech.*;
import javax.speech.recognition.*;
import javax.speech.synthesis.*;
import java.util.*;
import java.text.*;

/**
 * Replies with the date or the time depending on whether it recognizes
 * "what time is it" or "what date is it".
 */
public class WhatTimeIsIt {
    public static Recognizer rec = null;
    public static Synthesizer synth = null;
    
    public static void main(String[] args) {
	try {
	    
	    SpeechEngineChooser chooser = SpeechEngineChooser.getAllEnginesDialog();
	    chooser.show();
	    RecognizerModeDesc desc = chooser.getRecognizerModeDesc();
	    
	    System.out.println("Choosing recognizer "+desc);
	    rec = Central.createRecognizer(desc);
	    System.out.println("Using recognizer "+rec.getEngineModeDesc());
	    rec.addEngineListener(new TestEngineListener());
	    
	    rec.addResultListener(new ResultAdapter() {
		public void resultAccepted(final ResultEvent e) {
		    //start in a new thread so that Mouth moves.
		    Runnable r = new Runnable() {
			public void run() {
			    try {
				FinalRuleResult r = (FinalRuleResult)(e.getSource());
				System.out.println("Got result "+r);
				String tags[] = r.getTags();
				if(tags == null) return;
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
				rec.waitEngineState(Recognizer.PAUSED);
				rec.deallocate();
			    } catch(Exception e1) {
				e1.printStackTrace(System.out);
			    }
			}
		    };
		    new Thread(r).start();
		}
	    }
	    );
	    
	    RecognizerAudioAdapter raud = new TestAudioListener();
	    rec.getAudioManager().addAudioListener(raud);
	    
	    rec.allocate();
	    
	    rec.waitEngineState(Recognizer.ALLOCATED);
	    
	    SpeakerProfile prof = chooser.getSpeakerProfile();
	    rec.getSpeakerManager().setCurrentSpeaker(prof);
	    
	    RuleSequence rs1 = new RuleSequence();
	    RuleToken rt1 = new RuleToken("what");
	    RuleTag rt2 = new RuleTag(new RuleToken("time"),"TIME");
	    RuleTag rt3 = new RuleTag(new RuleToken("date"),"DATE");
	    RuleToken rt4 = new RuleToken("is it");
	    rs1.append(rt1);
	    RuleAlternatives ra1 = new RuleAlternatives();
	    ra1.append(rt2);
	    ra1.append(rt3);
	    rs1.append(ra1);
	    rs1.append(rt4);
	    RuleGrammar gram = rec.newRuleGrammar("time");
	    gram.setRule("testRule",rs1,true);
	    
	    gram.setEnabled(true);
	    
	    SynthesizerModeDesc sdesc = chooser.getSynthesizerModeDesc();
	    synth = Central.createSynthesizer(sdesc);
	    synth.addEngineListener(new TestEngineListener());
	    
	    synth.allocate();
	    synth.waitEngineState(Synthesizer.ALLOCATED);
	    synth.resume();
	    
	    SynthesizerProperties sprops = synth.getSynthesizerProperties();
	    sprops.setVoice(chooser.getVoice());
	    
	    SpeakableListener spList = new TestSpeakableListener();
	    synth.addSpeakableListener(spList);
	    
	    SynthesizerProperties props = synth.getSynthesizerProperties();
	    sprops.setVolume(1.0f);
	    sprops.setSpeakingRate(200.0f);
	    
	    rec.commitChanges();
	    
	    rec.requestFocus();
	    rec.resume();
	    
	    //wait till we have recognized a result and deallocated
	    rec.waitEngineState(Recognizer.DEALLOCATED);
	    
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
