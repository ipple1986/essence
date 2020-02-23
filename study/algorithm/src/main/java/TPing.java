import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class TPing {
    public static void main(String[] args) {
        int trheads = Runtime.getRuntime().availableProcessors();

        Executor executor = Executors.newFixedThreadPool(trheads);
        class MyCallable implements Callable {
            private String name;
            private int start;
            private int end;
            public MyCallable(int start,int end){
                this.name = "MyCallable{".concat(""+start).concat("-").concat(""+end).concat("}");
                this.start = start;
                this.end = end;
            }
            @Override
            public Object call() throws Exception {
                Set<Integer> ports = new HashSet<>();
                for(int port = start; port < end; port++){
                    try{
                        new Socket("124.117.251.206",port);
                        System.out.println(this.name + "  available port: " + port);
                        ports.add(port);
                    }catch (IOException e){
                    }finally {
                        //System.out.println(this.name+" "+ port);
                    }
                }
                System.out.println(this.name + ports);
                return ports;
            }
        }
        int runables = (65535%trheads == 0) ? (65535/trheads): (65535/trheads +1);
        List<Future<Set<Integer>>> futures = new ArrayList<>();
        for(int port = 0; port < 35536; port+=runables){
                int end = ((port + runables)>65535)?65535:(port + runables);
            Future<Set<Integer>> future = ((ExecutorService) executor).submit(new MyCallable(port,end));
            futures.add(future);
        }
        Set<Integer> ports = new CopyOnWriteArraySet<>();
        futures.forEach(f->{
            try {
                Set<Integer> set = f.get();
                ports.addAll(set);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        System.out.println(ports);
        ((ExecutorService) executor).shutdown();
    }
}
