package observer.dp.demo2;

public class Observer2 implements IObserver{
    public void update(int i){
        System.out.println("Observer2 is notified to update to " + i);
    }
}
