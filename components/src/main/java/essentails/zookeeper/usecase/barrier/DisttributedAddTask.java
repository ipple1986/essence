package essentails.zookeeper.usecase.barrier;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import essentails.zookeeper.usecase.AbstractZooKeeperHelper;

public class DisttributedAddTask extends AbstractZooKeeperHelper implements Watcher {

    static String barrierZnode = "/zk_barrier";
    public DisttributedAddTask(){
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

            System.out.println("add operator: 1+2="+(1+2));
            close();
        }
    }
}
