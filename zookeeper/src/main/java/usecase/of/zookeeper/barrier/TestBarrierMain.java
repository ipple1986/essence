package usecase.of.zookeeper.barrier;

import java.io.IOException;

public class TestBarrierMain {

    public static void main(String ... args)  {
        new DisttributedAddTask();
        new DisttributedDivideTask();

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

/*        new Thread(new DisttributedAddTaskRunnable()).start();
        new Thread(new DisttributedDivideTaskRunnable()).start();*/
    }
}
