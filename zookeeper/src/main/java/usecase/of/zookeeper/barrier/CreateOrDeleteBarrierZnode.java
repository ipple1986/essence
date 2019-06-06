package usecase.of.zookeeper.barrier;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;

public class CreateOrDeleteBarrierZnode extends  AbstractZooKeeperHelper{


    static {
        new CreateOrDeleteBarrierZnode();
    }
    private  static void createBarrierZnodeIfNeccessory()throws  Exception{

        if(zk.exists( barrierZnode,false)==null){
            System.out.println("--create barrier znode---");
            zk.create(barrierZnode,barrierZnode.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        close();
    }
    private static void deleteBarrierZnodeIfExists() throws Exception{
        if(zk.exists( barrierZnode,false)!=null){
            zk.delete(barrierZnode,-1);
        }
    }
    public static void main(String ... args) throws  Exception{

        //createBarrierZnodeIfNeccessory();
        deleteBarrierZnodeIfExists();
    }
}

