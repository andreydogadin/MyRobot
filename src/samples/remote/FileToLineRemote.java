package samples.remote;

import com.cloudgarden.audio.*;

import javax.media.protocol.FileTypeDescriptor;

/**
 * Demonstrates network transmission of audio data in compressed (GSM) and 
 * uncompressed (RAW) formats - for ease of demonstration incorporates both 
 * server and client since both run on the localhost. (Note, MPEG_AUDIO does
 * not work - no suitable Processor exists to convert from raw to MPEG_AUDIO
 * format).
 */
public class FileToLineRemote extends TestClientListener {
    
    boolean compress = true;
    
    public FileToLineRemote() {    }
    
    public static void main(String[] args) {
	FileToLineRemote rem = new FileToLineRemote();
	//rem.test();
	
	System.out.println("transmitting in uncompressed format\n");
	rem.compress(false);
	rem.serverToClient();
	System.out.println("transmitting in compressed format\n");
	rem.compress(true);
	rem.serverToClient();
	
	System.out.println("transmitting in uncompressed format\n");
	rem.compress(false);
	rem.clientToServer();
	 
	System.out.println("transmitting in compressed format\n");
	rem.compress(true);
	rem.clientToServer();
	
	System.out.println("All done");
	System.exit(0);
    }
    
    public void compress(boolean compress) {
	this.compress = compress;
    }
    
    /**
     * Tests out the transfer of compressed data without a network
     */
    public void test() {
	try {
	    total = 0;
	    //set up the "server" side
	    AudioFileSource src = new AudioFileSource(new java.io.File("resources\\hello_world.wav"));
	    AudioMediaFormatConverter acs = new AudioMediaFormatConverter(src, FileTypeDescriptor.GSM);
	    acs.setAudioFormat(null);
	    //set up the "client" side
	    AudioLineSink sink = new AudioLineSink(11025,16,1,true,false);
	    AudioMediaFormatConverter acs2 = new AudioMediaFormatConverter(acs, sink);
	    
	    //start the "server" side
	    src.startSending();
	    System.out.println("server sending data");
	    //wait for the sink to drain, and release client from server
	    sink.drain();
	} catch(Exception e) {
	    e.printStackTrace();
	}
    }
    
    public void serverToClient() {
	try {
	    total = 0;
	    //set up the "server" side
	    AudioFileSource src = new AudioFileSource(new java.io.File("resources\\hello_world.wav"));
	    System.out.println("file format = "+src.getAudioFormat());
	    AudioServerSink server = new AudioServerSink("speechserver", java.net.InetAddress.getLocalHost(),1199);
	    System.out.println("server set up");
	    
	    if(compress) {
		server.setContentType(FileTypeDescriptor.GSM);
		server.setAudioFormat(null);
	    } else {
		server.setContentType(FileTypeDescriptor.RAW);
		server.setAudioFormat(src.getAudioFormat());
	    }
	    
	    server.addClientListener(this);
	    AudioMediaFormatConverter acs = new AudioMediaFormatConverter(src, server);
	     
	    //start the "server" side
	    src.startSending();
	    System.out.println("SERVER SENDING DATA TO CLIENT");
	    
	    //set up the "client" side
	    AudioLineSink sink = new AudioLineSink(11025,16,1,true,false);
	    AudioClientSource client = new AudioClientSource("speechserver", "localhost",1199);
	    System.out.println("client set up");
	    AudioMediaFormatConverter acs2 = new AudioMediaFormatConverter(client,sink);
	    //start the "client" side
	    client.startSending();
	    
	    //wait for the sink to drain, and release client from server
	    sink.drain();
	    client.closeConnection();
	    
	    //and close the server
	    server.closeServer();
	    
	} catch(Exception e) {
	    e.printStackTrace();
	}
    }
    
    public void clientToServer() {
	try {
	    total = 0;
	    //set up the "server" side
	    AudioServerSource server = new AudioServerSource("speechserver", java.net.InetAddress.getLocalHost(),1199);
	    server.addClientListener(this);
	    AudioLineSink sink = new AudioLineSink(11025,16,1,true,true);
	    AudioMediaFormatConverter acs = new AudioMediaFormatConverter(server, sink);
	    System.out.println("server set up");
	    
	    //set up the "client" side
	    AudioFileSource src = new AudioFileSource(new java.io.File("resources\\hello_world.wav"));
	    System.out.println("file format = "+src.getAudioFormat());
	    AudioClientSink client = new AudioClientSink("speechserver", "localhost",1199);
	    if(compress) {
		client.setContentType(FileTypeDescriptor.GSM);
		client.setAudioFormat(null);
	    } else {
		client.setContentType(FileTypeDescriptor.RAW);
		client.setAudioFormat(src.getAudioFormat());
	    }
	    System.out.println("client set up");
	    AudioMediaFormatConverter acs2 = new AudioMediaFormatConverter(src, client);

	    //start the "server" side
	    server.startSending();
	    
	    //start the "client" side
	    src.startSending();
	    
	    System.out.println("CLIENT SENDING DATA TO SERVER");
	    
	    //wait for the sink to drain, and release client from server
	    sink.drain();
	    client.closeConnection();
	    
	    //and close the server
	    server.closeServer();
	    
	} catch(Exception e) {
	    e.printStackTrace();
	}
    }
    
}
