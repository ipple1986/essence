package nettyInAction.EchoServer.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class EchoServer {
    public static void main(String ...args){
        //usring NIO Transport
        EventLoopGroup bootStrapEventLoopGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bootStrapEventLoopGroup)//配置主EvenLoop组，也可配置子EventLoop组，用于处理事件（连接，读写数据）
                .channel(NioServerSocketChannel.class)//设置服务端Channel类
                .localAddress("127.0.0.1",9999)//设置地址与端口
                .childHandler(new ChannelInitializer<SocketChannel>() {//创建连接初始化处理器，获取当前连接所在的管道，在最后活加自定义handler
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new EchoServerHandler());
                    }
                });
        try {
            ChannelFuture serverChannelFuture = serverBootstrap.bind().sync();//等待绑定同步完成
            serverChannelFuture.channel().closeFuture().sync(); //同步等待SocketServerChannel关闭
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            bootStrapEventLoopGroup.shutdownGracefully();//release all resources and threads
        }
    }
}
