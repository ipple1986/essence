package nettyInAction.Transports;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

public class JdkApiOio {
    public static void serve(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        for(;;){
            final Socket socket = serverSocket.accept();
            System.out.println("Accept connection from :"+socket);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        OutputStream outputStream =  socket.getOutputStream();
                        outputStream.write("Hello".getBytes(Charset.forName("UTF-8")));
                        outputStream.flush();
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
    public static void main(String ... args) throws IOException {
        serve(999);
    }
}
