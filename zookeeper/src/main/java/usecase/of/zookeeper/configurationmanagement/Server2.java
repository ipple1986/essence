package usecase.of.zookeeper.configurationmanagement;

import org.apache.zookeeper.*;
import usecase.of.zookeeper.AbstractZooKeeperHelper;

import java.io.IOException;

public class Server2 extends AbstractZooKeeperHelper {


    private String redisHost = "";
    private String redisHostZnode = "/redisHost";
    public Server2(){
        try {
            if(zk.exists(redisHostZnode,false)==null){
                zk.create(redisHostZnode,"127.0.0.1:6379".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            redisHost = new String(zk.getData(redisHostZnode,myWatcher,null));
            System.out.println("Server2 get redisHost:"+redisHost);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private Watcher myWatcher = new Watcher() {
        @Override
        public void process(WatchedEvent watchedEvent) {
            if(watchedEvent.getType() == Event.EventType.NodeDataChanged){
                try {
                    redisHost = new String(zk.getData(redisHostZnode,myWatcher,null));
                    System.out.println("Server2 recevice event:[resitHost="+redisHost+"]");
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

}
