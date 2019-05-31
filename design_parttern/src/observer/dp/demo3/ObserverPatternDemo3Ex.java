package observer.dp.demo3;

public class ObserverPatternDemo3Ex {
    public static void main(String ...args){

        System.out.println("***  Observer Patterm Demo3 ***");
        Subject1 subject1 = new Subject1();
        Subject2 subject2 = new Subject2();

        IObserver observer1 = new Observer1();
        IObserver observer2 = new Observer2();
        IObserver observer3 = new Observer3();

        subject1.register(observer1);
        subject1.register(observer2);
        subject1.register(observer3);

        subject2.register(observer1);
        subject2.register(observer2);
        subject2.register(observer3);

        System.out.println("In subject1 ,setting myValue to 2");
        subject1.setFlag(2);
        System.out.println();


        subject1.unregister(observer1);
        System.out.println("In subject1,after unregister observer1 ,setting myValue to 2");
        subject1.setFlag(3);
        System.out.println();

        System.out.println("In subject2,setting myValue to 4");
        subject2.setFlag(4);
        System.out.println();

        subject2.unregister(observer1);
        subject2.unregister(observer2);
        subject2.unregister(observer3);
        System.out.println("In subject2,after unregister all observers,setting myValue to 5");
        subject2.setFlag(5);
        System.out.println();
    }
}
