package memento;

public class Originator {
    private String state;
    public void setState(String state){
        this.state = state;
        System.out.println("State at present: "+ state);
    }
    public Memento memento(){
        return  new Memento(state);
    }
    public void revert(Memento memento){
        System.out.println("Revert State .....");
        state = memento.getState();
        System.out.println("State at present: "+ state);
    }
}
