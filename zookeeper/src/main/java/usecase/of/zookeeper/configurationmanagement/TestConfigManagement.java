package usecase.of.zookeeper.configurationmanagement;

import java.io.IOException;

public class TestConfigManagement {

    public static void main(String ... args){
        new Server1();
        new Server2();
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
/*

operate
set /redisHost 123

output
Server1 get redisHost:127.0.0.1:6379
Server2 get redisHost:127.0.0.1:6379
Server1 recevice event:[resitHost=123]
Server2 recevice event:[resitHost=123]
 */