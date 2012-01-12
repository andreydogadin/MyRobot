/*
 */

package samples.remote;

import com.cloudgarden.audio.*;

import javax.sound.sampled.*;

/**
 * Demonstrates sending and receiving speech to/from a remote recognizer/synthesizer
 * using the local AudioClientSource/Sink objects. The new AudioMediaLineSource/Sink
 * classes are used (instead of AudioLineSource/Sink) since the javax.sound package
 * caused problems when a TargetDataLine and SourceDataLine were opened
 * simultaneously (ie, the audio data was not captured in the correct format).
 * The javax.robot.media classes (which AudioMediaLineSource/Sink use) do not have this problem.
 */
public class WhatTimeIsItClient {
    
    private String server, serverID;
    private int port;
    
    public WhatTimeIsItClient(String server, int port, String serverID) {
	this.server = server;
	this.serverID = serverID;
	this.port = port;
    }
    
    public void start() {
	//It seems like the javax.robot.media capture and play devices
	//(these are used by AudioMediaLineSource/Sink) don't 
	//support most unsigned formats.
	AudioFormat micFmt = new AudioFormat(8000,8,1,false,true);
	AudioFormat spkrFmt = new AudioFormat(8000,8,1,false,true);
	
	AudioMediaLineSource source = null;
	AudioMediaLineSink sink = null;
	try {
	    
	    System.out.println("\nSpeaker fmt="+spkrFmt);
	    System.out.println("\nMic fmt="+micFmt);
	    
	    sink = new AudioMediaLineSink(spkrFmt);
	    sink.addTransferListener(new TestTransferListener());
	    
	    source = new AudioMediaLineSource(micFmt);
	    //source.addTransferListener(new TestTransferListener());
	    
	    AudioClientSource audioReceiver = new AudioClientSource(serverID+"Syn", server, port);
	    AudioFormatConverter conv1 = new AudioFormatConverter(audioReceiver, sink);
	    
	    audioReceiver.startSending();
	    
	    AudioClientSink trans = new AudioClientSink(serverID+"Rec", server, port);
	    AudioFormatConverter conv2 = new AudioFormatConverter(source, trans);
	    
	    audioReceiver.addServerListener(new ServerListener() {
		public void serverClosed() {
		    System.out.println("Server has been closed");
		    System.exit(0);
		}
	    });
	    
	    source.startSending();
	    
	    System.out.println("Say \"what date/time is it\" ");
	    int numSecs = 20;
	    for(int i=0;i<numSecs;i++) {
		System.out.println("Counting down "+(numSecs-i)+"...");
		Thread.currentThread().sleep(1000);
	    }
	    System.out.println("all done");
	    audioReceiver.stopSending();
	    audioReceiver.closeConnection();
	    
	} catch(Exception e) {
	    e.printStackTrace();
	}
	System.exit(0);
    }
    
    public static void main(String[] args) {
	String host = "localhost";
	if(args != null && args.length > 0 && args[0] != null) host=args[0];
	WhatTimeIsItClient client = new WhatTimeIsItClient(host,1099,"TimeServer");
	client.start();
    }
}