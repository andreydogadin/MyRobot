package commands;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: andrey
 * Date: 03.06.12
 * Time: 20:32
 * To change this template use File | Settings | File Templates.
 */
public class SocketCommandSender extends CommandSender {
    private static Integer port = 7777;
    private ObjectOutputStream oos;

    public SocketCommandSender() {
        try{
            InetAddress host = InetAddress.getLocalHost();
            Socket socket = new Socket(host.getHostName(), this.port);
            this.oos = new ObjectOutputStream(socket.getOutputStream());

        } catch (Exception ex){
            ex.printStackTrace();
        }

    }

    public void sendMessage(String message) throws Exception {
        oos.writeObject(message);
    }

    public void close() {
        try {
            this.oos.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
