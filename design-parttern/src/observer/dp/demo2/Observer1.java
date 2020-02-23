package observer.dp.demo2;

public class Observer1 implements IObserver{
    public void update(int i){
        System.out.println("Observer1 is notified to update to " + i);
    }
}
