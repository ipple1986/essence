package memento;

public class MementoExample {
    public static void main(String[] args) {
        Originator originator = new Originator();
        originator.setState("first state");

        CareTaker careTaker = new CareTaker();
        careTaker.setMemento(originator.memento());

        originator.setState("second state.");

        originator.revert(careTaker.getMemento());
    }
}
