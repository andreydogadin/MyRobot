package samples.audio;

import com.cloudgarden.audio.*;
import javax.sound.sampled.*;
import javax.media.protocol.FileTypeDescriptor;

import java.io.File;
import java.net.*;

/**
 * Demonstrates reading from a wave file and writing to an mp3 file.
 */
public class WAVtoMPEG {
    
    public static void main(String[] args) {
	try {
	    AudioFormat ffmt = new AudioFormat(16000,16,1,true,false);
	    AudioFileSource fileSrc = new AudioFileSource(new File("resources\\hello_world.wav"));
	    
	    AudioMediaURLSink fileSink = new AudioMediaURLSink(new URL("file:resources\\hello_world.mp3"), FileTypeDescriptor.MPEG_AUDIO);
	    fileSrc.setSink(fileSink);
	    
	    //Start the whole thing going!
	    fileSrc.startSending();
	    
	    //and wait for the output files to be written.
	    fileSink.drain();
	    
	    System.out.println("All done");
	} catch(Exception e) {
	    e.printStackTrace();
	}
	System.exit(0);
    }
}
