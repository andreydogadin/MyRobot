/*
 * RemoteDictationClient.java
 *
 * Created on September 7, 2001, 12:53 PM
 */

package samples.remote;

import com.cloudgarden.audio.*;

/**
 * Start this class to receive audio data from a RemoteSynthesisServer - give it
 * an argument of the remote host name if not "localhost"
 */
public class RemoteSynthesisClient {
    
    private String server, synID;
    private int port;
    
    /** Creates new RemoteDictationClient */
    public RemoteSynthesisClient(String server, int port, String synID) {
        this.server = server;
        this.synID = synID;
        this.port = port;
    }
    
    public void start() {
        try {
            AudioSink sink = new AudioLineSink(16000,16,1,true,true);
	    AudioClientSource client = new AudioClientSource(synID, server, port);
	    System.out.println("server format = "+client.getAudioFormat());
	    System.out.println("line format = "+sink.getAudioFormat());
	    AudioFormatConverter conv = new AudioFormatConverter( client, sink);
	    client.startSending();
	    sink.drain();
            System.out.println("Audio finished");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        String host = "localhost";
        if(args != null && args.length > 0 && args[0] != null) host=args[0];
        RemoteSynthesisClient rsc = new RemoteSynthesisClient(host,1099,"RemoteSynthesisServer1");
        rsc.start();
        System.exit(0);
    }
}