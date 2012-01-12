package samples.remote;

import javax.speech.*;
import javax.speech.synthesis.*;
import javax.sound.sampled.*;
import java.util.*;
import java.net.*;

import com.cloudgarden.audio.*;
import com.cloudgarden.speech.*;

/**
 * Demonstrates sending synthesized audio data to a remote client. 
 */
public class RemoteSynthesisServer extends TestClientListener {
    
    public static void main(String[] args) {
	new RemoteSynthesisServer().start();
    }
    
    public void start() {
        Synthesizer synth = null;
        try {
            synth = Central.createSynthesizer(new SynthesizerModeDesc(Locale.US));
            CGAudioManager audioMan = (CGAudioManager)synth.getAudioManager();
            synth.allocate();
            synth.resume();
            
            SynthesizerProperties props = synth.getSynthesizerProperties();
            Voice v1 = new Voice("Microsoft Mary",Voice.GENDER_DONT_CARE,  Voice.AGE_DONT_CARE, "");
            props.setVoice(v1);

            InetAddress allowedClient = null;
            
            //replace "localhost" with name or ip address of client machine, or
            //comment out block (and leave allowedClient = null) to allow any
            //client to receive data. Only one client should receive the data - if
            //you wish multiple clients to receive the data, use the AudioSplitter
            //class to split the audio data from the CGAudioManager into multiple
            //AudioOutputSockets, and have one client per socket.
            try {
                allowedClient = InetAddress.getByName("localhost");
            } catch(UnknownHostException uhe) {
                uhe.printStackTrace();
            }
            
	    allowedClient = null;
	    
            System.out.println("allowedClient="+allowedClient);
            
            AudioServerSink sink = new AudioServerSink("RemoteSynthesisServer1", allowedClient,1099);
	    sink.addClientListener(this);
	    
	    //convert the synthesizer output to 8kHz, 8 bit before it is fed to the sink
	    AudioFormat fmt = new AudioFormat(8000,8,1,true,false);
            sink.setAudioFormat(fmt);
	    AudioFormatConverter conv = new AudioFormatConverter(audioMan, sink);
	    
	    System.out.println("sink format = "+sink.getAudioFormat()+"\nsynth format = "+audioMan.getAudioFormat());
            System.out.println("Start up the RemoteSynthesisClient now!");

            synth.speak("Hello world, what's the weather like over there?",null);
            synth.waitEngineState(synth.QUEUE_EMPTY);
            System.out.println("Waiting till we can close the output stream");
            audioMan.closeOutput();
            sink.drain();
            System.out.println("sink drained");
            //System.out.println("...waiting 9 secs for RemoteSynthesisClient to get the audio");
            //Thread.currentThread().sleep(9000);
            
        } catch(Exception e) {
            e.printStackTrace(System.out);
        } catch(Error e1) {
            e1.printStackTrace(System.out);
        } finally {
            try {
                synth.deallocate();
            } catch(Exception e2) {
                e2.printStackTrace(System.out);
            }
            System.exit(0);
        }
    }
    
}

