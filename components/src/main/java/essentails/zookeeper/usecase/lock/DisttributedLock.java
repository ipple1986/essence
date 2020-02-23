package essentails.zookeeper.usecase.lock;

import essentails.zookeeper.usecase.AbstractZooKeeperHelper;
import org.apache.zookeeper.*;

import java.util.List;

public class DisttributedLock extends AbstractZooKeeperHelper {

    private static String cpuLockZnode = "/CPU-LOCK";


    private static class AcquireCpuResouceRunnable implements Runnable{

        private String name;
        private String lockId ;
        public  AcquireCpuResouceRunnable(String name){
            this.name = name;
        }

        private boolean acquireLock(){

            try {
                if(lockId==null){
                    lockId = zk.create(cpuLockZnode.concat("/"),"".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
                }

                List<String> children = zk.getChildren(cpuLockZnode,false);
                //System.out.println(children);
                children.sort((a,b)-> a.compareTo(b));

                if(lockId.endsWith(children.get(0))){
                    //acquire the lock
                    //System.out.println("Acquire " + lockId +" "+ children.get(0));
                    synchronized (this){
                        notifyAll();
                    }
                    System.out.println("Acquire " + lockId );
                    return Boolean.TRUE;
                    //release the lock by delete node
                }else{ //find the next lower than self to set watch
                    children = zk.getChildren(cpuLockZnode,false);
                    children.sort((a,b)-> a.compareTo(b));
                    String before = children.get(0);

                    for(String child:children){
                        if(!lockId.endsWith(child))before = child;
                        if(lockId.endsWith(child))break;
                    }

                    zk.exists(cpuLockZnode.concat("/").concat(before), new Watcher() {
                        @Override
                        public void process(WatchedEvent watchedEvent) {
                            //System.out.println("----");
                            if(watchedEvent.getType() ==  Event.EventType.NodeDeleted){
                                acquireLock();
                            }
                        }
                    });
                    synchronized (this){
                        wait(2000);
                    }
                    return Boolean.TRUE;

                }

            } catch (KeeperException|InterruptedException e) {
                e.printStackTrace();
                return Boolean.FALSE;
            }
        }
        private void releaseLock(){

            try {
                zk.delete(lockId,-1);
                System.out.println("releaseLock "+lockId);
            } catch (InterruptedException|KeeperException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void run() {
            try {
                if(zk.exists(cpuLockZnode,false)==null){
                    zk.create(cpuLockZnode,"CPURESOUCE".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                }
            } catch (KeeperException|InterruptedException e) {
               // e.printStackTrace();
            }


                if(acquireLock()){
                    System.out.println(this.name + " doSomethings...here");
                    releaseLock();

                }

        }
    }

    public static void main(String[] args) {
        try {
            zk.delete(cpuLockZnode,-1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }

        for(int i=0;i<10;i++){
            new Thread(new AcquireCpuResouceRunnable("AcquireCpuResouceRunnable".concat(String.valueOf(i)))).start();
        }
       // ZKUtils.withoutExitJVM();
    }
}
