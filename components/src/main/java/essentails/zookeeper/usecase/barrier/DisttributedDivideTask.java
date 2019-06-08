package essentails.zookeeper.usecase.barrier;

import essentails.zookeeper.usecase.AbstractZooKeeperHelper;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;


public class DisttributedDivideTask  extends AbstractZooKeeperHelper implements Watcher {

    static String barrierZnode = "/zk_barrier";
    
    public DisttributedDivideTask(){
        super();
        try {
            zk.exists(barrierZnode,this);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void process(WatchedEvent watchedEvent) {
        if(watchedEvent.getType()== Event.EventType.NodeDeleted){
            System.out.println("divide operator: 10/2="+(10/2));
            close();
        }
    }
}