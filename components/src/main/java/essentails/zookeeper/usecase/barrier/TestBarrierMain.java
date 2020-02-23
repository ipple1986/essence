package essentails.zookeeper.usecase.barrier;

        import essentails.zookeeper.usecase.ZKUtils;

public class TestBarrierMain {

    public static void main(String ... args)  {
        new DisttributedAddTask();
        new DisttributedDivideTask();

        ZKUtils.withoutExitJVM();

/*        new Thread(new DisttributedAddTaskRunnable()).start();
        new Thread(new DisttributedDivideTaskRunnable()).start();*/
    }
}
