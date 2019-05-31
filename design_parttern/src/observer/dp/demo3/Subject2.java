package observer.dp.demo3;


import java.util.ArrayList;
import java.util.List;

public class Subject2 implements ISubject {
    List<IObserver> observerList = new ArrayList<>();
    private int myValue;
    @Override
    public void register(IObserver observer) {
        observerList.add(observer);
    }

    @Override
    public void unregister(IObserver observer) {
        observerList.remove(observer);
    }

    @Override
    public void modifiyObservers(int myValue) {
        for(IObserver observer : observerList){
            observer.update(myValue,this.getClass().getSimpleName());;
        }
    }

    public int getFlag() {
        return myValue;
    }

    public void setFlag(int _flag) {
        this.myValue = _flag;
        //if _flag changed,notify all observers
        modifiyObservers(myValue);
    }
}
