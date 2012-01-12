package samples.audio;

import com.cloudgarden.audio.*;
import javax.sound.sampled.*;

import java.io.File;

/**
 * Demonstrates copying an mp3 file to a gsm file, then playing the gsm file
 * to an AudioLineSink
 */
public class AudioStreamSourceTest {
    
    public static void main(String[] args) {
	try {
	    //copy from mp3 to gsm
	    AudioFormat fmt = new AudioFormat(16000,16,1,true,false);
	    DataLine.Info ininfo = new DataLine.Info(TargetDataLine.class, fmt);
	    TargetDataLine inline = (TargetDataLine) AudioSystem.getLine(ininfo);
	    inline.open();
	    inline.start();
	    AudioStreamSource source = new AudioStreamSource(new AudioInputStream(inline));
	    AudioFileSink sink = new AudioFileSink(new File("test.wav"),fmt, AudioFileFormat.Type.WAVE);
	    sink.setSource(source);
	    source.startSending();
	    System.out.println("recording for 3 secs");
	    Thread.currentThread().sleep(3000);
	    source.stopSending();
	    sink.drain();
	    System.out.println("All done");
	} catch(Exception e) {
	    e.printStackTrace();
	}
	System.exit(0);
    }
}
