package observer.dp.demo1;

import java.util.ArrayList;
import java.util.List;

public class Subject implements ISubject{
    List<Observer> observerList = new ArrayList<>();
    private int _flag;
    @Override
    public void register(Observer observer) {
        observerList.add(observer);
    }

    @Override
    public void unregister(Observer observer) {
        observerList.remove(observer);
    }

    @Override
    public void modifiyObservers() {
        for(Observer observer : observerList){
            observer.update();;
        }
    }

    public int getFlag() {
        return _flag;
    }

    public void setFlag(int _flag) {
        this._flag = _flag;
        //if _flag changed,notify all observers
        modifiyObservers();
    }
}
