/*
 * RemoteDictationClient.java
 *
 * Created on September 7, 2001, 12:53 PM
 */

package samples.remote;

import com.cloudgarden.audio.*;

/**
 * Start this class to send audio data to a RemoteDictationServer - give it
 * an argument of the remote host name if not "localhost"
 */
public class RemoteDictationClient {
    
    private String server, recoID;
    private int port;
    
    /** Creates new RemoteDictationClient */
    public RemoteDictationClient(String server, int port, String recoID) {
        this.server = server;
        this.recoID = recoID;
        this.port = port;
    }
    
    public void start() {
        try {
            AudioSource source;
            source = new AudioLineSource(16000,16,1,true,false);
            //source = new AudioFileSource(new java.io.File("what_time.au"));
	    AudioClientSink sink = new AudioClientSink(recoID, server, port);
	    source.setSink(sink);
	    source.startSending();
	    Thread.currentThread().sleep(10000);
	    source.stopSending();
	    sink.closeConnection();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        String host = "localhost";
        if(args != null && args.length > 0 && args[0] != null) host=args[0];
        RemoteDictationClient rdc = new RemoteDictationClient(host,1099,"RemoteDictationServer1");
        rdc.start();
        System.exit(0);
    }
}