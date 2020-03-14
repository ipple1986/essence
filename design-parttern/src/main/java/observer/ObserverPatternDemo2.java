package observer;

import java.util.ArrayList;
import java.util.List;

public class ObserverPatternDemo2 {
    public static void main(String[] args) {
        Subject0 subject = new Subject0();
        IObeserver observer = new Observer0();
        IObeserver observer1 = new Observer1();
        System.out.println("Subject0 注册 Observer0 Observer1,同时被通知");
        subject.register(observer);
        subject.register(observer1);
        subject.setFlag(2);
        System.out.println("注销了Observer0,只有Observer1被通知");
        subject.unregister(observer);
        subject.setFlag(4);
        System.out.println("=============================================");
        System.out.println("** Subject1 注册 Observer0");
        Subject1 subject1 = new Subject1();
        subject1.register(observer);
        subject1.setFlag(6);
        System.out.println("** 未注销时，再次更新flag");
        //subject1.unregister(observer);
        subject1.setFlag(9);
    }
}

interface ISubject0{
    void register(IObeserver observer);
    void unregister(IObeserver observer);
    void notifyObservers(int i);
}

class Subject0 implements ISubject0{
    List<IObeserver> observerList = new ArrayList<>();
    private int flag ;
    public void setFlag(int flag){
        this.flag = flag;
        notifyObservers(flag);
    }
    @Override
    public void register(IObeserver observer) {
        observerList.add(observer);
    }

    @Override
    public void unregister(IObeserver observer) {
        observerList.remove(observer);
    }

    @Override
    public void notifyObservers(int i) {
        for(IObeserver observer:observerList){
            observer.update(this.getClass().getSimpleName(),i);
        }
    }
}
class Subject1 extends Subject0{}

interface  IObeserver{
    void update(String s,int i);
}
class Observer0 implements  IObeserver{
    public void update(String s,int i){
        System.out.println(String.format("Observer0: flag is changed in ISubject{%s} to {%d}",s,i));
    }
}
class Observer1 implements  IObeserver{
    public void update(String s,int i){
        System.out.println(String.format("Observer1: flag is changed in ISubject{%s} to {%d}",s,i));
    }
}