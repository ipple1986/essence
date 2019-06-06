package usecase.of.zookeeper.barrier;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import usecase.of.zookeeper.AbstractZooKeeperHelper;

public class DisttributedAddTask extends AbstractZooKeeperHelper implements Runnable, Watcher {

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
    public void run() {
        while(isBarrier){
            System.out.println("Add Operator is watting ");
        }
        System.out.println("add operator: 1+2="+(1+2));
        close();
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if(watchedEvent.getType()== Event.EventType.NodeDeleted){
            isBarrier = Boolean.FALSE;
        }
    }
}
