package samples.audio;

import com.cloudgarden.audio.*;
import javax.sound.sampled.*;
import javax.media.protocol.FileTypeDescriptor;

import java.io.File;
import java.net.*;

/**
 * Demonstrates reading from one file and writing to two different ones with different formats.
 * Uses the AudioMediaURLSource, AudioSplitter, AudioFormatConverter classes.
 */
public class FileToFiles {
    
    public static void main(String[] args) {
	try {
	    AudioSink fileSink1, fileSink2;
	    
	    //Set up the input file - wav...
	    //AudioFileSource fileSrc = new AudioFileSource(new File("resources\\hello_world.wav"));
	    
	    //..or mp3 (need to specify a format for the raw output data)
	    AudioFormat ffmt = new AudioFormat(16000,16,1,true,false);
	    AudioMediaURLSource fileSrc = new AudioMediaURLSource(new URL("file:resources\\hello_world.mp3"), ffmt);
	    
	    AudioSplitter split = new AudioSplitter();
	    split.setSource(fileSrc);
	    
	    //test out the AudioMediaURLSink and saving different file formats
	    //fileSink1 = new AudioMediaURLSink(new URL("file:test.mp3"), FileTypeDescriptor.MPEG_AUDIO);
	    fileSink1 = new AudioMediaURLSink(new URL("file:test.gsm"), FileTypeDescriptor.GSM);
	    //fileSink1 = new AudioMediaURLSink(new URL("file:test.aif"), FileTypeDescriptor.AIFF);
	    //fileSink1 = new AudioMediaURLSink(new URL("file:test.qt"), FileTypeDescriptor.QUICKTIME);

	    split.addSink(fileSink1);
	    
	    AudioFormat fmt = new AudioFormat(16000,16,1,true,true);
	    fileSink2 = new AudioFileSink(new File("test.wav"), fmt, AudioFileFormat.Type.WAVE);

	    //convert the audio format between the splitter and the output file
	    AudioFormatConverter conv = new AudioFormatConverter();
	    split.addSink(conv);
	    conv.setSink(fileSink2);
	    
	    //Start the whole thing going!
	    fileSrc.startSending();
	    
	    //and wait for the output files to be written.
	    fileSink1.drain();
	    fileSink2.drain();
	    
	    System.out.println("All done");
	} catch(Exception e) {
	    e.printStackTrace();
	}
	System.exit(0);
    }
}
