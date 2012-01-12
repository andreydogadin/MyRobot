package robot.media.audio.recognition;

import javax.speech.recognition.RecognizerAudioAdapter;
import javax.speech.recognition.RecognizerAudioEvent;

/**
 * Prints audio events to System.out
 */
public class AudioListener extends RecognizerAudioAdapter {

    boolean level;

    public AudioListener() {
        level = true;
    }

    public AudioListener(boolean showLevel) {
        level = showLevel;
    }

    public void speechStarted(RecognizerAudioEvent e) {
        //System.out.println("Speech started");
    }

    public void speechStopped(RecognizerAudioEvent e) {
        //System.out.println("Speech stopped");
    }

    public void audioLevel(RecognizerAudioEvent e) {
        if (level) System.out.println("AudioLevel " + e.getAudioLevel());
    }

}
