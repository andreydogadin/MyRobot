/*
 */

package samples.remote;

import com.cloudgarden.audio.*;

/**
 * This example demonstrates sending audio data from a client machine to
 * a server machine using the com.cloudgarden.speech.audio package - it
 * does not use a speech engine. Start the RemoteAudioServer on the server
 * machine, then start the RemoteAudioClient with a command-line argument
 * of the server machine's network name. If no argument is provided, the
 * localhost is assumed.
 */
public class RemoteAudioClient {
    
    private String server, recoID;
    private int port;
    
    public RemoteAudioClient(String server, int port, String recoID) {
        this.server = server;
        this.recoID = recoID;
        this.port = port;
    }
    
    public void start() {
        try {
            AudioSource source = new AudioLineSource(16000,16,1,true,false);
	    AudioClientSink client = new AudioClientSink(recoID, server, port);
	    System.out.println("server format = "+client.getAudioFormat());
	    AudioFormatConverter conv = new AudioFormatConverter(source, client);
	    source.startSending();
	    System.out.println("transmitting for 5 seconds");
	    Thread.currentThread().sleep(5000);
	    System.out.println("transmission over");
	    source.stopSending();
	    client.closeConnection();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        String host = "localhost";
        if(args != null && args.length > 0 && args[0] != null) host=args[0];
        RemoteAudioClient rdc = new RemoteAudioClient(host,1099,"RemoteAudioServer");
        rdc.start();
        System.exit(0);
    }
}