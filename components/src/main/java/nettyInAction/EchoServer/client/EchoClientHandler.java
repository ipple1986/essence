package nettyInAction.EchoServer.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;

public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        System.out.println("[Client]Recevied from Server:" + byteBuf.toString(CharsetUtil.UTF_8));
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception{
        String message = "Hello Work Netty";
        ctx.writeAndFlush(Unpooled.copiedBuffer(message,CharsetUtil.UTF_8));
        System.out.println("[Client]Sending messger from Client: ".concat(message));
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable) throws Exception{
        throwable.printStackTrace();
        ctx.close();
    }
}
