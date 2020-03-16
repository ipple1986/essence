package observer;

import java.util.ArrayList;
import java.util.List;
// 发布与订阅 模型
// 监听对象们 与 目标对象
public class ObserverPatternDemo1 {
    public static void main(String[] args) {
        Subject subject = new Subject();
        Observer observer = new Observer();

        subject.register(observer);
        subject.setFlag(2);

        subject.unregister(observer);
        subject.setFlag(4);
    }
}
interface ISubject{
    void register(Observer observer);
    void unregister(Observer observer);
    void notifyObservers();
}
class Subject implements ISubject{
    List<Observer> observerList = new ArrayList<Observer>();
    private int flag ;
    public void setFlag(int flag){
        this.flag = flag;
        notifyObservers();
    }
    @Override
    public void register(Observer observer) {
        observerList.add(observer);
    }

    @Override
    public void unregister(Observer observer) {
        observerList.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for(Observer observer:observerList){
            observer.update();
        }
    }
}
class Observer{
    public void update(){
        System.out.println("Observer: flag is changed in ISubject");
    }
}
