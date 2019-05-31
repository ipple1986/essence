package observer.dp.demo1;

public class ObserverPatternEx {
    public static void main(String ...args){

        System.out.println("***  Observer Patterm Demo1 ***");
        Subject subject = new Subject();
        Observer observer = new Observer();

        subject.register(observer);
        System.out.println("setting flag to 2");
        subject.setFlag(2);
        System.out.println();

        System.out.println("after unregister observer,setting flag to 3");
        subject.unregister(observer);
        subject.setFlag(3);
    }
}
