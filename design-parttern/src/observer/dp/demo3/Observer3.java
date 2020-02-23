package observer.dp.demo3;

public class Observer3 implements IObserver {
    public void update(int i,String subjectName) {
        System.out.println("Observer3 is notified to update to " + i + " in subject:" + subjectName);
    }
}
