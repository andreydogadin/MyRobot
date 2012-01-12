package samples.audio;

import com.cloudgarden.audio.*;
import javax.sound.sampled.*;
import javax.media.protocol.FileTypeDescriptor;

import java.net.*;

/**
 * Demonstrates copying an mp3 file to a gsm file, then playing the gsm file
 * to an AudioLineSink
 */
public class FileToFileToLine {
    
    public static void main(String[] args) {
	try {
	    //copy from mp3 to gsm
	    AudioFormat ffmt = new AudioFormat(16000,16,1,true,false);
	    AudioMediaURLSource fileSrc = new AudioMediaURLSource(new URL("file:resources\\hello_world.mp3"), ffmt);
	    AudioMediaURLSink fileSink = new AudioMediaURLSink(new URL("file:resources\\hello_world.gsm"), FileTypeDescriptor.GSM);
	    fileSrc.setSink(fileSink);
	    System.out.println("about to convert from mp3 to gsm");
	    fileSrc.startSending();
	    fileSink.drain();
	    System.out.println("resources\\hello_world.mp3 converted into resources\\hello_world.gsm");
	    
	    //Set up the input gsm file
	    AudioMediaURLSource src = new AudioMediaURLSource(new URL("file:resources\\hello_world.gsm"), ffmt);
	    
	    AudioSink sink;
	    sink = new AudioLineSink(ffmt);
	    src.setSink(sink);
	    
	    // Uncomment the following to get the "raw" GSM data from the source (which won't sound good
	    // but does demonstrate how to get the compressed GSM data if you need it).
	    //src.setContentType(FileTypeDescriptor.GSM);
	    //src.setAudioFormat(null);
	    
	    //Start the whole thing going!
	    src.startSending();
	    System.out.println("playing resources\\hello_world.gsm");
	    sink.drain();
	    
	    System.out.println("All done");
	} catch(Exception e) {
	    e.printStackTrace();
	}
	System.exit(0);
    }
}
