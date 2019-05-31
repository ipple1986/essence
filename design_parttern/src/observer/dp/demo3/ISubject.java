package observer.dp.demo3;

public interface ISubject {

    void register(IObserver observer);
    void unregister(IObserver observer);
    void modifiyObservers(int i);
}
