package essentails.zookeeper.usecase.queue;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import essentails.zookeeper.usecase.AbstractZooKeeperHelper;
import essentails.zookeeper.usecase.ZKUtils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class DisttributedQueue extends AbstractZooKeeperHelper {

    private static String rootQueue = "/QUEUE";

    private static Object lockObject = new Object();
    private static void cleanAllItems(){
        try {
            List<String> children = zk.getChildren(rootQueue,false);
            for(String child:children){
                zk.delete(rootQueue.concat("/").concat(child),-1);
            }
            zk.delete(rootQueue,-1);
        } catch (KeeperException|InterruptedException e) {
            e.printStackTrace();
        }
    }
    private static void createItem(String data){

        try {

            if(zk.exists(rootQueue,false)==null){
                zk.create(rootQueue,"".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            zk.create(rootQueue.concat("/item-"),data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        } catch (KeeperException|InterruptedException e) {
            e.printStackTrace();
        }

    }
    private static class ConsumerRunnable implements  Runnable {

        private String runnableName;
        public  ConsumerRunnable(String runnableName){
            this.runnableName  = runnableName;
        }
        @Override
        public void run() {
            List<String> children = null;
            //get Children
            try {
                children = zk.getChildren(rootQueue,true);

            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //sortting
            children.sort((a,b)-> {return a.compareTo(b);});

            //iterator
            for(String child : children){
                String data = null;
                try {
                    //can read data
                    byte[] bytes = zk.getData(rootQueue.concat("/").concat(child),false,null);
                    if(bytes!=null){
                        data = new String(bytes);
                    }
                    //can delete node
                    zk.delete(rootQueue.concat("/").concat(child),-1);

                } catch (KeeperException|InterruptedException e) {
                    //e.printStackTrace();
                    continue;//if cann't read data or cann't delete node,it means other process has deal with this node ,reading next node.
                }
                //actual handle here
                System.out.println(this.runnableName + " " + child + " " + data);
                //break the for iterator
                break;
            }

        }
    };



    static  CopyOnWriteArrayList<String> list  = new CopyOnWriteArrayList<>();
    public static void main(String[] args) {
        cleanAllItems();
        for(int i=0;i<10;i++){
            createItem(String.valueOf(i));
        }



        ExecutorService executorService = Executors.newCachedThreadPool();
        for(int i=0;i<10;i++){
            executorService.execute(
               new ConsumerRunnable("ConsumerRunnable".concat(String.valueOf(i)))
            );
        }
        executorService.shutdown();


        ZKUtils.withoutExitJVM();
    }
}
/*

output:
ConsumerRunnable0 item-0000000000 0
ConsumerRunnable1 item-0000000001 1
ConsumerRunnable5 item-0000000002 2
ConsumerRunnable9 item-0000000003 3
ConsumerRunnable7 item-0000000004 4
ConsumerRunnable8 item-0000000005 5
ConsumerRunnable3 item-0000000006 6
ConsumerRunnable4 item-0000000007 7
ConsumerRunnable6 item-0000000008 8
ConsumerRunnable2 item-0000000009 9

 */
