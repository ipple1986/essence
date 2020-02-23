package nettyInAction.Transports;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class JdkApiNio {
    public static void serve(int port) throws IOException {
        //打开管道，配置非阻塞
        ServerSocketChannel serverSocketChannel  = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(Boolean.FALSE);
        //得到ServerSocket，进行绑定
        ServerSocket serverSocket = serverSocketChannel.socket();
        serverSocket.bind(new InetSocketAddress(port));
        //打开Selector,注册事件
        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        //定义发送信息
        final ByteBuffer message = ByteBuffer.wrap("Hello".getBytes(Charset.forName("UTF-8")));
        for(;;){
            selector.select();//监听事件的到来
            //迭代所有事件进行处理
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iteraor = keys.iterator();
            while(iteraor.hasNext()){
                SelectionKey key = iteraor.next();
                iteraor.remove();
                if(key.isAcceptable()){//ServerSocketChannel已经连接上了，获取客户通道，进行配置与注册事件
                    ServerSocketChannel serverSocketChannel1 = (ServerSocketChannel)key.channel();
                    SocketChannel clientChannel = serverSocketChannel.accept();//获取客户通道
                    //配置Nio与注册事件,并attach message给SelectionKey.OP_WRITE
                    clientChannel.configureBlocking(Boolean.FALSE);
                    clientChannel.register(selector,SelectionKey.OP_READ|SelectionKey.OP_WRITE,message.duplicate());
                    System.out.println("Accepted connection from client:"+clientChannel);
                }
                if(key.isWritable()){//向客户channel写出信息
                    SocketChannel socketChannel = (SocketChannel)key.channel();
                    ByteBuffer byteBuffer = (ByteBuffer)key.attachment();
                    while(byteBuffer.hasRemaining()){
                        if(socketChannel.write(byteBuffer)==0)break;
                    }
                    socketChannel.close();

                }

            }
        }
    }
}
