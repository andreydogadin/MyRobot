package samples;

import javax.speech.*;
import javax.speech.recognition.*;
import javax.speech.synthesis.*;

/**
 * Prints engine events (Recognizer and Synthesizer) to System.out
 */
public class TestEngineListener implements SynthesizerListener, 
					   RecognizerListener {
    public void  engineAllocated(EngineEvent e) {
	System.out.println(e.getSource()+" engineAllocated");
    }
    public void  engineAllocatingResources(EngineEvent e) {
	System.out.println(e.getSource()+" engineAllocatingResources");
    }
    public void  engineDeallocated(EngineEvent e) {
	System.out.println(e.getSource()+" engineDeallocated");
    }
    public void  engineDeallocatingResources(EngineEvent e) {
	System.out.println(e.getSource()+" engineDeallocatingResources");
    }
    public void  engineError(EngineErrorEvent e) {
	System.out.println(e.getSource()+" engineError");
    }
    public void  enginePaused(EngineEvent e) {
	System.out.println(e.getSource()+" enginePaused");
    }
    public void  engineResumed(EngineEvent e) {
	System.out.println(e.getSource()+" engineResumed...");
    }           
    public void  recognizerProcessing(RecognizerEvent e) {
	System.out.println(e.getSource()+" recognizerProcessing");
    }
    public void  recognizerListening(RecognizerEvent e) {
	System.out.println(e.getSource()+" recognizerListening");
    }           
    public void recognizerSuspended(RecognizerEvent e) {
	System.out.println(e.getSource()+" recognizerSuspended");
    }           
    public void changesCommitted(RecognizerEvent e) {
	System.out.println(e.getSource()+" changesCommitted");
    }           
    public void focusGained(RecognizerEvent e) {
	System.out.println(e.getSource()+" focusGained");
    }           
    public void focusLost(RecognizerEvent e) {
	System.out.println(e.getSource()+" focusLost");
    }           
    public void  queueEmptied(SynthesizerEvent e) {
	System.out.println(e.getSource()+" queueEmptied");
    }
    public void  queueUpdated(SynthesizerEvent e) {
	System.out.println(e.getSource()+" queueUpdated");
    }
}
