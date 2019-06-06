package usecase.of.zookeeper.barrier;

        import usecase.of.zookeeper.ZKUtils;

        import java.io.IOException;

public class TestBarrierMain {

    public static void main(String ... args)  {
        new DisttributedAddTask();
        new DisttributedDivideTask();

        ZKUtils.withoutExitJVM();

/*        new Thread(new DisttributedAddTaskRunnable()).start();
        new Thread(new DisttributedDivideTaskRunnable()).start();*/
    }
}
