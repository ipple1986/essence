package loadbalance;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class NodeInvoker implements Invoker {
    protected  String invokerName;
    protected  boolean isUp;
    private String host;
    private int port;
    public NodeInvoker(String invokerName, boolean isUp,String host,int port){
        this.invokerName = invokerName;
        this.isUp = isUp;
        this.host = host;
        this.port = port;
    }
    @Override
    public boolean invoke() {
        if(isUp)System.out.println(String.format("%s-%b",invokerName,isUp));
        return isUp;
    }

    @Override
    public boolean isUp() {
        try {
            new Socket().connect(new InetSocketAddress(host,port));
            return  Boolean.TRUE;
        } catch (IOException e) {
            return Boolean.FALSE;
        }
    }

    @Override
    public String toString() {
        return super.toString().concat("-").concat(invokerName).concat("-").concat(host).concat(":").concat(port+"");
    }
}
