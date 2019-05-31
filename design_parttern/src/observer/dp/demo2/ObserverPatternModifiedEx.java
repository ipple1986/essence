package observer.dp.demo2;

public class ObserverPatternModifiedEx {
    public static void main(String ...args){

        System.out.println("***  Observer Patterm Demo2 ***");
        Subject subject = new Subject();
        IObserver observer1 = new Observer1();
        IObserver observer2 = new Observer2();

        subject.register(observer1);
        subject.register(observer2);
        System.out.println("setting myValue to 2");
        subject.setFlag(2);
        System.out.println();

        subject.unregister(observer1);
        System.out.println("After unregister observer1,setting flag to 3");
        subject.setFlag(3);
        System.out.println();

        subject.unregister(observer2);
        System.out.println("After unregister observer2,setting flag to 4");
        subject.setFlag(4);
    }
}
