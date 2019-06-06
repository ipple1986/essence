package usecase.of.zookeeper.doublebarrier;

import org.apache.zookeeper.*;
import usecase.of.zookeeper.AbstractZooKeeperHelper;

import java.io.IOException;
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
                System.out.println("clientId:"+clientId+" start the computation!! ");
                ClientTask.this.run();
                System.out.println("clientId:"+clientId+" finished the computation!! ");

                try {




                    Thread.sleep(2000);

                    //reset watcher for exit root /Barrier
                    zk.getChildren(rootBarrier,exitBarrierZnodeWatcher);
                    //delete ephem after finish computation
                    zk.delete(rootBarrier.concat("/").concat(clientId),-1);


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
                    if(childrens.size()<=1){
                        if(zk.exists(rootBarrier.concat("/Ready"),false)!=null){
                            zk.delete(rootBarrier.concat("/Ready"),-1);
                            System.out.println("clientid:"+clientId+" delete /Barrier/Ready");
                        }
                        if(zk.exists(rootBarrier,false)!=null){
                            zk.delete(rootBarrier,-1);
                            System.out.println("clientid:"+clientId+" delete /Barrier");
                        }
                    }else{//
                        createReadyZnodeWhenReachMaxSize();
                    }
                } catch (KeeperException|InterruptedException e) {
                    //e.printStackTrace();
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
            createReadyZnodeWhenReachMaxSize();

        } catch (KeeperException|InterruptedException e) {
            e.printStackTrace();
        }

    }
    private void createReadyZnodeWhenReachMaxSize() throws KeeperException, InterruptedException {
        List<String> children = zk.getChildren(rootBarrier,false);
        if(children.size()>=MAX_TASK_2_START_COMPUTATION){//at least MAX_TASK_2_START_COMPUTATION
            if(zk.exists(rootBarrier.concat("/Ready"),false)!=null){
                zk.delete(rootBarrier.concat("/Ready"),-1);
            }
            zk.create(rootBarrier.concat("/Ready"),"".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
        }
    }

    public void run(){
        //do something here
        System.out.println("clientId:"+this.clientId+" do something");
    }

    public static void  main(String ...args){
        //clean all
            try {
                zk = new ZooKeeper(conntectStr,2000,null);
                //clean all
                if(zk.exists(rootBarrier,false)!=null){
                    List<String> children = zk.getChildren(rootBarrier,false);
                    for(String child:children){
                        zk.delete(rootBarrier.concat("/").concat(child),-1);
                    }
                }
            } catch (KeeperException | IOException|InterruptedException e) {
                e.printStackTrace();
            }

    }
}
