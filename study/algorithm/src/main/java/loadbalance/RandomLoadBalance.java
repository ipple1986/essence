package loadbalance;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RandomLoadBalance implements LoadBalance{
    //private int currentSelect = 0;
    private  int ALL_SELECTED ;
    private static Random random = new Random();

    public void setInvokers(List<Invoker> invokers) {
        this.invokers = invokers;
        ALL_SELECTED = (1<<invokers.size())-1;
    }
    private List<Invoker> invokers;
    public RandomLoadBalance(){ }
    public RandomLoadBalance(List<Invoker> invokers){
        this();
        this.invokers = invokers;
        ALL_SELECTED = (1<<invokers.size())-1;
    }
    ReentrantLock reentrantLock = new ReentrantLock();
    @Override
    public  Invoker select() {
        if(this.invokers==null||this.invokers.size()==0)return null;
        int selectedmark = 0;
        while(selectedmark!=ALL_SELECTED){
            int currentSelect = random.nextInt(invokers.size());
            if((selectedmark & (1<<currentSelect))==0){
                selectedmark|=(1<<currentSelect);
                if(invokers.get(currentSelect).isUp()){
                    return  invokers.get(currentSelect);
                }
            }
        }
        return null;
    }
}
