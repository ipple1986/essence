package usecase.of.zookeeper.doublebarrier;

import org.apache.zookeeper.*;
import usecase.of.zookeeper.AbstractZooKeeperHelper;

import java.util.List;

public class ClientTask extends AbstractZooKeeperHelper {
    public final static Integer MAX_TASK_2_START_COMPUTATION = 5;
    public final static String rootBarrier = "/Barrier";
    private String clientId;
    private Watcher startComputationWatcher = new Watcher() {
        @Override
        public void process(WatchedEvent watchedEvent) {
            if(watchedEvent.getType() == Event.EventType.NodeCreated){
                //start to computation
                ClientTask.this.run();
                try {
                    //delete ephem after finish computation
                    zk.delete(rootBarrier.concat("/").concat(clientId),-1);
                    //
                    zk.getChildren(rootBarrier,exitBarrierZnodeWatcher);
                } catch (InterruptedException|KeeperException e) {
                    e.printStackTrace();
                }

            }else{
                //if not exist /Barrier/Ready  ,reset watcher
                try {
                    zk.exists(rootBarrier.concat("/").concat("Ready"),startComputationWatcher);
                } catch (KeeperException|InterruptedException  e) {
                    e.printStackTrace();
                }
            }

        }
    };
    private Watcher exitBarrierZnodeWatcher = new Watcher() {
        @Override
        public void process(WatchedEvent watchedEvent) {
            if(watchedEvent.getType() == Event.EventType.NodeChildrenChanged){
                try {
                    List<String> childrens =   zk.getChildren(rootBarrier,exitBarrierZnodeWatcher);
                    //if only Ready znode under Barrier,delete both /Barrier and /Barrier/Ready
                    if(childrens.size()==1){
                        zk.delete(rootBarrier.concat("/Ready"),-1);
                        zk.delete(rootBarrier,-1);
                    }
                } catch (KeeperException|InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    };
    public  ClientTask(String clientId){
        super();
        try {
            this.clientId = clientId;

            //create root znode /Barrier if not exists
            if(zk.exists(rootBarrier,false)==null){
                zk.create(rootBarrier,"".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            //judge is to start computation,/Barrier/Ready is exists
            zk.exists(rootBarrier.concat("/").concat("Ready"),startComputationWatcher);

            //create epheml znode under /Barrier
            zk.create(rootBarrier.concat("/").concat(clientId),clientId.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

            //judge is the time to set /Barrier/Ready
            List<String> children = zk.getChildren(rootBarrier,false);
            if(children.size()==MAX_TASK_2_START_COMPUTATION){
               zk.create(rootBarrier.concat("/").concat("Ready"),"".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL,CreateMode.PERSISTENT);
            }

        } catch (KeeperException|InterruptedException e) {
            e.printStackTrace();
        }

    }
    public void run(){
        //do something here
        System.out.println(this.clientId+" do something");
    }

}
