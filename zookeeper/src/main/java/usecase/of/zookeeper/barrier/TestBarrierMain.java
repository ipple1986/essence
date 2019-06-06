package usecase.of.zookeeper.barrier;

public class TestBarrierMain {

    public static void main(String ... args){
        new Thread(new DisttributedAddTask()).start();
        new Thread(new DisttributedDivideTask()).start();
    }
}
