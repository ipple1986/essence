package loadbalance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RobbinLoadBalance implements LoadBalance{
    private AtomicInteger count = new AtomicInteger(-1);
    private  int ALL_SELECTED ;
    public void setInvokers(List<Invoker> invokers) {
        this.invokers = invokers;
        ALL_SELECTED = (1<<invokers.size())-1;
    }
    private List<Invoker> invokers;
    public RobbinLoadBalance(){}
    public RobbinLoadBalance(List<Invoker> invokers){
        this.invokers = invokers;
        ALL_SELECTED = (1<<invokers.size())-1;
    }
    @Override
    public Invoker select() {
        if(this.invokers==null||this.invokers.size()==0)return null;
        int selectedmark = 0;
        while (selectedmark!=ALL_SELECTED){
            int currentSelect = count.incrementAndGet()%invokers.size();
            if((selectedmark & (1<<currentSelect))==0){
                selectedmark|=(1<<currentSelect);
                if(invokers.get(currentSelect).isUp()){
                    return invokers.get(currentSelect);
                }
            }
        }
        return null;
    }
}
