package samples.audio;

import com.cloudgarden.audio.*;
import javax.sound.sampled.*;

import java.io.File;

/**
 * Demonstrates the AudioFormatConverter's capabilities, converting from 8 to 16kHz with and without
 * linear interpolation.
 */
public class ConversionTest {
    
    public static void main(String[] args) {
        try {
            AudioFileSource src = new AudioFileSource(new File("resources\\hello_world.wav"));
            AudioFormat fmt = new AudioFormat(16000,16,1,true, false);
            AudioLineSink sink = new AudioLineSink(fmt);
            System.out.println("converting from "+src.getAudioFormat()+"\nto "+sink.getAudioFormat());
            System.out.println("with interpolation");
            AudioFormatConverter conv = new AudioFormatConverter(src, sink);
            conv.useLinearInterpolation(true);
            src.startSending();
            src.drain();
            sink.drain();
            
            System.out.println("no interpolation");
            src.reopen();
            sink = new AudioLineSink(fmt);
            conv = new AudioFormatConverter(src, sink);
            conv.useLinearInterpolation(false);
            src.startSending();
            src.drain();
            sink.drain();
            System.out.println("All done");
        } catch(Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
