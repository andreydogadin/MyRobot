package robot.media.audio.recognition;

import robot.MyRobot;

import javax.speech.EngineErrorEvent;
import javax.speech.EngineEvent;
import javax.speech.recognition.RecognizerEvent;
import javax.speech.recognition.RecognizerListener;
import javax.speech.synthesis.SynthesizerEvent;
import javax.speech.synthesis.SynthesizerListener;

/**
 * Prints engine events (Recognizer and Synthesizer) to System.out
 */
public class EngineListener implements SynthesizerListener, RecognizerListener {

    public void engineAllocated(EngineEvent e) {
        MyRobot.getInstance().setRecognitionState("engineAllocated");
    }

    public void engineAllocatingResources(EngineEvent e) {
        MyRobot.getInstance().setRecognitionState("engineAllocatingResources");
    }

    public void engineDeallocated(EngineEvent e) {
        MyRobot.getInstance().setRecognitionState("engineDeallocated");
    }

    public void engineDeallocatingResources(EngineEvent e) {
        MyRobot.getInstance().setRecognitionState("engineDeallocatingResources");
    }

    public void engineError(EngineErrorEvent e) {
        MyRobot.getInstance().setRecognitionState("engineError");
    }

    public void enginePaused(EngineEvent e) {
        MyRobot.getInstance().setRecognitionState("enginePaused");
    }

    public void engineResumed(EngineEvent e) {
        MyRobot.getInstance().setRecognitionState("engineResumed");
    }

    public void recognizerProcessing(RecognizerEvent e) {
        MyRobot.getInstance().setRecognitionState("recognizerProcessing");
    }

    public void recognizerListening(RecognizerEvent e) {
        MyRobot.getInstance().setRecognitionState("recognizerListening");
    }

    public void recognizerSuspended(RecognizerEvent e) {
        MyRobot.getInstance().setRecognitionState("recognizerSuspended");
    }

    public void changesCommitted(RecognizerEvent e) {
        MyRobot.getInstance().setRecognitionState("hangesCommitted");
    }

    public void focusGained(RecognizerEvent e) {
        MyRobot.getInstance().setRecognitionState("focusGained");
    }

    public void focusLost(RecognizerEvent e) {
        MyRobot.getInstance().setRecognitionState("focusLost");
    }

    public void queueEmptied(SynthesizerEvent e) {
        MyRobot.getInstance().setRecognitionState("queueEmptied");
    }

    public void queueUpdated(SynthesizerEvent e) {
        MyRobot.getInstance().setRecognitionState(e.getSource() + " queueUpdated");
    }
}
