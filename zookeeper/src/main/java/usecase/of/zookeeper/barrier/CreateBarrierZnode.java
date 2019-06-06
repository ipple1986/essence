package usecase.of.zookeeper.barrier;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

public class CreateBarrierZnode extends  AbstractZooKeeperHelper{


    private  static void createBarrierZnodeIfNeccessory()throws  Exception{

        if(zk.exists( barrierZnode,false)==null){
            zk.create(barrierZnode,barrierZnode.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        }
        close();
    }
    public static void main(String ... args) throws  Exception{
        createBarrierZnodeIfNeccessory();

    }
}

