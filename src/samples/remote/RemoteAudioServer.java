package samples.remote;

import com.cloudgarden.audio.*;

/**
 * This example demonstrates using an AudioServerSource object
 * to provide audio data received from a RemoteAudioClient to
 * an AudioLineSink.
 *<P>
 * Start up this class, then start a RemoteAudioClient on another
 * machine (with a command-line argument of this machine's name)
 * which will send audio data (using RMI) to this machine, which will
 * in turn be sent to the AudioLineSink (and heard through the default
 * audio output device on this machine).
 *
 */
public class RemoteAudioServer extends TestClientListener {
    
    public static void main(String[] args) {
	new RemoteAudioServer().start();
    }
    
    public void start() {
	try {
	    AudioServerSource server = new AudioServerSource("RemoteAudioServer",null,1099);
	    AudioSink sink = new AudioLineSink(11025,16,1,true,false);
	    //connect the output device to the AudioServerSource
	    server.setSink(sink);
	    server.addClientListener(this);
	    System.out.println("server fmt = "+server.getAudioFormat());
	    //and start playing audio data received from the remote client.
	    server.startSending();
	    //now just wait till the sink has no more data - when and END_OF_DATA is
	    //received from the client.
	    sink.drain();
	    System.out.println("all done");
	} catch(Exception e) {
	    e.printStackTrace();
	}
	System.exit(0);
    }
    
}

