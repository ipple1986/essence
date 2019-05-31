package observer.dp.demo1;

public interface ISubject {

    void register(Observer observer);
    void unregister(Observer observer);
    void modifiyObservers();
}
