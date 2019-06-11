### 1.Netty异步与事件驱动
##### java.net socket本地阻塞
```text
SeverSocket serverSocket = new ServerSocket(portNumber);
Socket clientSocket = serverSocket.accept();
BufferedReader in = new BufferedReader(new InputStremReader(clientSocket.getInputStream()));
PrintWriter out = new PrintWrite(clientSocket.getOutputStream(),true);
String request
while((request = in.readLine())!=null){
    if(request.equals("Done") break;
    response = processRequest(request);
    out.print(response);
}
```
```text
* 同一时间只能接收一个连接处理
* 每个clientSocket使用一个线程来处理，有以下问题
> 1.每个线程处于休眼，等待IO输入/输出,造成浪费
> 2.每个线程需要创建64KB-1MB的栈内存
> 3.JVM线程数有限
> 4.连接达到一定限制（10万连接数），就会使上下文切换耗时越长
```

##### java 1.4java.nio socket本地阻塞
````text
* 通过setsockopt()设置sockets；当读写时，无数据直接返回
* 通过非阻塞sockets,使用本地系统事件通知机制API，监听数据是否已准备好被读/写（Selector）
* 即便有可选的方案：Selector,想要实现可靠，不出错的网络应用也是极其困难的，代码逻辑复杂
````
Netty核心组件
* Channels通道： 一个打开的连接硬件设备，文件，socket，可进行多种IO（读写）处理的资源（资源）
* Callbacks回调：封装代码逻辑，提供给调用者后，在某个时间点进行调用。触发某种事件时ChannelHandler的回调会被执行
```text
public class ConnectHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client " + ctx.channel().remoteAddress() + " connected");
    }
}
```
* Futures 另一种通知应用的方式，往ChannelFuture添加ChannelFutureListener,解决JDK中自带Future阻塞等待/处理结果的问题 
//Netty每个IO Outbount都返回ChannelFuture，非阻塞
```text
Channel channel = ...;
// Does not block
ChannelFuture future = channel.connect(new InetSocketAddress("192.168.0.1", 25));
future.addListener(new ChannelFutureListener() {
    @Override
    public void operationComplete(ChannelFuture future) {
        if (future.isSuccess()){
            ByteBuf elaborate = Unpooled.copiedBuffer("Hello",Charset.defaultCharset());
            ChannelFuture wf = future.channel().writeAndFlush(buffer);
            ....
        } else {
            Throwable cause = future.cause();
            cause.printStackTrace();
        }
    }
});
ps :callbacks futures 相互补助，组成netty的关键构建块（逻辑）
```
* 事件与处理器（通知）：使用不同的事件通知代表 改变/操作的状态
```text
处理器（日志，数据转换，流程控制，应用逻辑）
inbound事件（连接激活与否，数据读，用户事件，错误事件）
outbound事件（打开关闭连接，写/刷数据到socket）
```



底层使用一个EventLoop线程分配给每一个Channel,处理所有IO事件
```text
* 注册感兴趣的事件
* 将事件分发给ChannelHandler
* 执行更多的动作
```


Channel -> ChannelPipe -> chain of ChannelHandler
协议
数据处理层
线程模型
### 2.EchoServer
参考项目代码
### 3.Netty核心组件与设计
设计
```text
* 基于NIO的异步/事件驱动
* 从网络层解藕出来应用逻辑
```

#### Channel/EventLoop/ChannelFutrue
* Channel //Sockets
```text
* 依赖于底层网络传输的基本IO处理（绑定，连接，读入，写出）.
* Channel提供简单API，减少对Sockets的复杂操作
```
预定义专用的Channel
````text
* EmbeddedChannel
* LocalServerChannel
* NioStcpChannel
* NioSocketChannel
* NioServerSocketChannel
* NioDatagramChannel
````
* EventLoop //控制流程，多线程，并发
```text
* EventLoopGroup包含一个或多个EventLoop
* 一个EventLoop绑定一个线程
* EventLoop里的所有IO处理，都由该线程处理
* 一个EventLoop可包含一个或多个Channel,Channel只能属于一个EventLoop
```
* ChannelFuture //异步通知
```text
* Netty的操作都是异步的，即返回ChannelFuture
* ChannelFuture + ChannelFutureListener 实现异常通知

```

#### ChannelHandler/ChannelPipe
执行应用逻辑+管理数据流
```text
* ChannelHandler //根据事件通知，操作应用逻辑
* ChannelPipe //一个Channel创建时被分配到指定的Channel管道 ,它包含一个Inbound的ChannelHandler链，和一个OutBound的Channel处理链
* ChannelHandlerContext//绑定了ChannelHandler + ChannelPipe,通过ctx将事件传递给下一个handler
```
写出数据的方式
```text
* 直接通过ChannelHandlerContext,写到Channel:触发ChannelOutBoundHandler链中最后一个处理器的执行
* 写出到关联handler的ctx,触发下一个ChannelOutBoundHandler的执行
```
各种适配器类，以满足不同默认需求
```text
ChannelHandlerAdapter
ChannelInboundHandlerAdapter
ChannelOutboundHandlerAdapter
ChannelDuplexHandlerAdapter
```
子类ChannelHandler
```text
SimpleChannelInboundHandler<T> extend ChannelInboundHandlerAdapter
encodes:数据从outbound写出去，需要编码
decodes:数据从inbound读出来，需要解码

```

#### Bootsrap
提供网络层的配置
````text
Bootstrap //客户端提供connect ,只需要1个EventLoopGroup
ServerBoostrap //服务端提供绑定网络地址与端口，需要2个EventLoopGroup,
//前一个EventLoop操作ServerSocketChannel，负责为每一个连接创建SocketChannel
//并从后一个EventLoopGroup分配EventLoop给创建的客户端SocketChannel
````
### 3.传输协议（OIO/NIO/NettyOIO/NettyNIO）
3.1通过例子实现不同版本服务端
```text
* JdkApi OIO
* JdkApi NIO
* Netty OIO
* Netty NIO
```
具体参考项目代码
3.2 Transport API（Channel）
Channel（可比较排序）包含
```text
* ChannelConfig //配置信息设置
* ChannelPipe //所处管道
```
ChannelHandlers的工作
```text
* 提供数据格式转换如encoders/decoders
* 异常通知
* Channel激活与否通知
* 通知Channel注册进/注销EventLoop
* 提供用户自定义事件的通知
```
Channel类的方法
```text
* eventLoop
* isActive
* pipeline
* localAddress
* remoteAddress
* flush
* write
* writeAndFlush
```
Netty自带的传输协议
```text
* NIO //基于Selector模式，每一个EventLoop使用一个Selector,管理EventLoop上绑定的所有Channel
* EPOLL  // 基于Linux版本JDK实现底层epoll的NIO API,如果你的应用跑在linux可以考虑这种高性能的NIO
* OIO // 基于java.net阻塞API，Netty可以通过设置socket超时时间，实现NIO，一超时进行下一次eventloop
* Local //客户端/服务端在同一个jvm里
* Embbed //channelhandler里嵌套辅助的channelhandler,用于debug或测试
```
说明
```text
* nio/epoll支持零拷贝，将文件系统数据直接传给网络，不用从内枋空间复制给用户空间，再发给网络，如ftp/http协议。
* 对于数据加密压缩的OS文件系统不支持
```

### 4.字节缓存 类（ByteBuf）
ByteBuf
```text
* 用户可自定义扩展
* 容量自动扩充//最大Integer.MAX_VALUE
* 读写切换不用调flip
* 读写使用不同索引//readerIndex writerIndex,write/read开头方法操作索引，set/get没用修改索引 
* 支持方法链式写法
* 支持引用计数
* 支持池化
```
ByteBuf使用模式
* 堆ByteBuf //堆分配置
```text
ByteBuf heapBuf = ...;
if (heapBuf.hasArray()) {//有堆内存分配
byte[] array = heapBuf.array();
int offset = heapBuf.arrayOffset() + heapBuf.readerIndex();
int length = heapBuf.readableBytes();
handleArray(array, offset, length);
}
```
* 直接ByteBuf
```text
ByteBuf directBuf = ...;
if (!directBuf.hasArray()) {//无堆分配，直接从ByteBuf读取到数组
int length = directBuf.readableBytes();
byte[] array = new byte[length];
directBuf.getBytes(directBuf.readerIndex(), array);
handleArray(array, 0, length);
}
```
* 复合ByteBuf
```text
CompositeByteBuf//添加删除ByteBuf,如果只有一个ByteBufy,调用hasArray，返回ByteBuff的hasArray结果 ，否则返回false
 ByteBuf header
 ByteBuf body
```
使用
```text
CompositeByteBuf messageBuf = Unpooled.compositeBuffer();
ByteBuf headerBuf = ...; // can be backing or direct
ByteBuf bodyBuf = ...; // can be backing or direct
messageBuf.addComponents(headerBuf, bodyBuf);
.....
messageBuf.removeComponent(0); // remove the header
for (ByteBuf buf : messageBuf) {
System.out.println(buf.toString());
}
```
```text
CompositeByteBuf compBuf = Unpooled.compositeBuffer();
int length = compBuf.readableBytes();
byte[] array = new byte[length];
compBuf.getBytes(compBuf.readerIndex(), array);
handleArray(array, 0, array.length);
```

ByteBuf字节级别操作

访问数据(不修改readerIndex/writeIndex)
```text
ByteBuf buffer = ...;
for (int i = 0; i < buffer.capacity(); i++) {
byte b = buffer.getByte(i);
System.out.println((char) b);
}
```
顺序访问
```text
ByteBuf buffer = ...;
for (int i = buffer.readerIndex(); i < buffer..writerIndex(); i++) {
byte b = buffer.getByte(i);
System.out.println((char) b);
}
```
毛弃已读字节，discardReadBytes

可读字节时，读取所有字节
```text
ByteBuf buffer = ...;
while (buffer.isReadable()) {
System.out.println(buffer.readByte());
}

```
可写字节时写出int
```text
// Fills the writable bytes of a buffer with random integers.
ByteBuf buffer = ...;
while (buffer.writableBytes() >= 4) {
buffer.writeInt(random.nextInt());
}
```
索引管理
```text
markWriterIndex/markReaderIndex/resetWriterIndex/resetReaderIndex
readerIndex/writerIndex
clear//readerIndex = writerIndex = 0
```
查找,用ByteBufProcessor查找\r
```text
ByteBuf buffer = ...;
int index = buffer.forEachByte(ByteBufProcessor.FIND_CR);
```
衍生ByteBuf
```text

```

