package usecase.of.zookeeper.doublebarrier;

import java.io.IOException;

public class TestDoubleBarrierMain {
    public static void main(String ...args){
        for(int i = 0;i<4;i++){
            new ClientTask(""+i);
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        new ClientTask("4");

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
