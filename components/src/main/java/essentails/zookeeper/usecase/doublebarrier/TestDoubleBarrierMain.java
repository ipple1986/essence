package essentails.zookeeper.usecase.doublebarrier;

import essentails.zookeeper.usecase.ZKUtils;

public class TestDoubleBarrierMain {
    public static void main(String ...args){

        //create first 4 task
        for(int i = 0;i<4;i++){
            new ClientTask(""+i);
        }
        //sleep 5 seconds
        ZKUtils.threadSleep(5000);

        //start another 6 task
        new ClientTask("4");
        for(int i = 5;i<11;i++){
            new ClientTask(""+i);
        }

        ZKUtils.withoutExitJVM();
    }
}
