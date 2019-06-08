package essentails.zookeeper.usecase;

import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public abstract class AbstractZooKeeperHelper {
    protected Boolean isBarrier = true;

    protected static String conntectStr = "localhost:2181";
    static protected  ZooKeeper zk;
    static{
        try {
            zk = new ZooKeeper(conntectStr,2000,null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public AbstractZooKeeperHelper(){}
    protected  static synchronized void close(){
        try {
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
