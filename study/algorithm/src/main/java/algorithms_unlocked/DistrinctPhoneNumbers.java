package algorithms_unlocked;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class DistrinctPhoneNumbers {
   static Map<Integer,Map<Integer,Integer>> phoneMaps = new ConcurrentHashMap<>(4000);
    public static void main(String[] args) throws IOException {
        /*long freeMemory = Runtime.getRuntime().freeMemory();
        System.out.println(freeMemory);
        IntStream.range(10000,19999).forEach(e->{
            Map<Integer,Integer> map =   new ConcurrentHashMap<>(1000);
            phoneMaps.putIfAbsent(e,map);
            IntStream.range(0,999).forEach(ee->{
                IntStream.range(0,999).forEach(eee->map.putIfAbsent(ee,eee));
            });
        });
        System.out.println(phoneMaps.keySet().size()+"-- "+ phoneMaps.get(10000)+" --"+ (freeMemory-Runtime.getRuntime().freeMemory()));
        System.in.read();*/
        System.out.println(String.format("%04d",0));
        ExecutorService executor = Executors.newFixedThreadPool(10);
        executor.submit(()->{
            IntStream.range(10000,20000).forEach(f->{
                IntStream.range(0,1000).forEach(ff->{
                    IntStream.range(0,1000).forEach(fff->{
                        //new File("D:/s").mkdirs();
                        String path = String.format("%04d/%03d/%03d",f,ff,fff);
                        System.out.println(path);
                        new File("D:/s/".concat(path)).mkdirs();
                    });

                });
            });
        });
        executor.shutdown();
    }
}
