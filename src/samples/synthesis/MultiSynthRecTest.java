package samples.synthesis;

import javax.speech.*;
import javax.speech.recognition.*;
import javax.speech.synthesis.*;

/**
 * Creates multiple recognizers and synthesizers at once - note, for the Dragon
 * engine, only one recognizer can be created - this test will fail on trying to allocate
 * the second recognizer.
 */
public class MultiSynthRecTest {
    
    public static void main(String[] args) {
	Synthesizer synth = null;
	try {
	    int numRecs = 4;
	    Recognizer[] recs = new Recognizer[numRecs];
	    for(int i=0;i<numRecs;i++)  {
		//recs[i] = Central.createRecognizer(new RecognizerModeDesc("SAPI4","English",null,Boolean.FALSE,null,null));
		
		recs[i] = Central.createRecognizer(new RecognizerModeDesc("SAPI5","English",null,Boolean.FALSE,null,null));
		
		//Dragon engine does not allow multiple instances to be created!
		//recs[i] = Central.createRecognizer(new RecognizerModeDesc("Dragon",null,null,Boolean.FALSE,null,null));
		
		recs[i].allocate();
		recs[i].waitEngineState(Recognizer.ALLOCATED);
		//CGAudioManager aman = (CGAudioManager)recs[i].getAudioManager();
		//new AudioFormatConverter(new AudioFileSource(new java.io.File("resources/hello_world.wav")), aman);
		recs[i].resume();
		recs[i].waitEngineState(Recognizer.LISTENING);
		System.out.println("Made recognizer "+i+" "+recs[i]);
		RuleGrammar gram = recs[i].newRuleGrammar("time");
		for(int g=0;g<10;g++) {
		    gram = recs[i].newRuleGrammar("time"+g);
		    gram.setRule("testRule",new RuleToken("hello"),true);
		    gram.setEnabled(true);
		    System.out.println("Made grammar "+g+" for recognizer "+i);
		}
		System.out.println("committing changes");
		recs[i].commitChanges();
		recs[i].waitEngineState(Recognizer.LISTENING);
		System.out.println("committed changes");
	    }
	    
	    int numSyns = 5;
	    Synthesizer[] syns = new Synthesizer[numSyns];
	    for(int i=0;i<numSyns;i++) {
		//The Microsoft SAPI4 engines can all speak at the same time. 
		//The Microsoft SAPI5 engines are more polite and wait till they are the only one speaking.
		syns[i] = Central.createSynthesizer(new SynthesizerModeDesc(null,"Microsoft SAPI4",null,Boolean.FALSE,null));
		//syns[i] = Central.createSynthesizer(new SynthesizerModeDesc(null,"Loquendo SAPI5",null,Boolean.FALSE,null));
		if(syns[i] == null) {
		    syns[i] = Central.createSynthesizer(new SynthesizerModeDesc(null,null,null,Boolean.FALSE,null));
		}
		syns[i].allocate();
		syns[i].resume();
		syns[i].waitEngineState(Synthesizer.ALLOCATED);
		System.out.println("Made synthesizer "+i+" "+syns[i].getEngineModeDesc().getModeName());
	    }
	    
	    for(int i=0;i<numSyns;i++) {
		SpeakableListener spList = new TestSpeakableListener();
		syns[i].addSpeakableListener(spList);
		try { Thread.currentThread().sleep(500); } catch(Exception e) {}
		syns[i].speak("<emp>Hello</emp> World, can you <emp>hear</emp> me?",null);
	    }
	    
	    for(int i=0;i<numSyns;i++) {
		syns[i].waitEngineState(synth.QUEUE_EMPTY);
		syns[i].deallocate();
		syns[i].waitEngineState(Synthesizer.DEALLOCATED);
		System.out.println("Deallocated synth "+i);
	    }
	    
	    for(int i=0;i<numRecs;i++)  {
		recs[i].deallocate();
		recs[i].waitEngineState(Engine.DEALLOCATED);
		System.out.println("Deallocated recognizer "+i);
	    }
	    
	} catch(Exception e) {
	    e.printStackTrace(System.out);
	} catch(Error e1) {
	    e1.printStackTrace(System.out);
	} finally {
	    try {
		synth.deallocate();
		synth.waitEngineState(synth.DEALLOCATED);
		System.out.println("Shutting down");
	    } catch(Exception e2) {}
	    System.exit(0);
	}
    }
}
