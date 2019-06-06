package usecase.of.zookeeper;

import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public abstract class AbstractZooKeeperHelper {
    protected Boolean isBarrier = true;

    static String conntectStr = "localhost:2181";
    static protected  ZooKeeper zk;
    public AbstractZooKeeperHelper(){
        try {
            zk = new ZooKeeper(conntectStr,2000,null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    protected  static synchronized void close(){
        try {
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
