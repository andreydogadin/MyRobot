/*
 * WAVFileWriter.java
 *
 * Created on July 15, 2002, 2:25 PM
 */

package samples.synthesis;

import javax.speech.synthesis.*;
import java.io.*;
import javax.sound.sampled.*;
import com.cloudgarden.audio.*;
import com.cloudgarden.speech.*;

/**
 * Utility class for saving synthesized speech to a WAV file.
 *
 * Used by the VoicePad application.
 *
 *@see samples.userinterface.VoicePad
 */
public class WAVFileWriter {
    
    /** Creates a new instance of WAVFileWriter */
    public WAVFileWriter() {
    }
    
    public void speakToFile(Synthesizer synth, String text, File file) throws IOException {
	AudioFormat fmt;
	CGAudioManager audioMan = (CGAudioManager)synth.getAudioManager();
	
	    /*
	    if(audioMan.canSetAudioFormat()) {
		fmt = new AudioFormat(11025,16,1,true,false);
		audioMan.setAudioFormat(fmt);
	    }
	    fmt = audioMan.getAudioFormat();
	    System.out.println("File Format = "+fmt);
	     */
	
	fmt = new AudioFormat(16000,16,1,true,false);
	AudioFileFormat ff;
	ff = new AudioFileFormat(AudioFileFormat.Type.WAVE, fmt, AudioSystem.NOT_SPECIFIED);
	
	try {
	    AudioFileSink sink = new AudioFileSink(file,ff);
	    //audioMan.setSink(sink);
	    new AudioFormatConverter(audioMan, sink);
	    audioMan.startSending();
	    
	    synth.speak(text,null);
	    
	    synth.waitEngineState(Synthesizer.QUEUE_EMPTY);
	    System.out.println("\nqueue empty");
	    //need to close the output file otherwise the results are uncertain
	    audioMan.closeOutput();
	    sink.drain();
	    
	    System.out.println("\nflushed");
	} catch(Exception e) {
	    //If the file cannot be opened (eg if it is already opened by
	    //another application) we will get an IOException
	    e.printStackTrace();
	}
    }
}
