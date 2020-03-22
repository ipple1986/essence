package mediator;

public class Friend1 implements Person {
    private String name;
    private Mediator mediator;
    public Friend1(String name,Mediator mediator){
        this.name = name;
        this.mediator = mediator;
    }
    @Override
    public void notify(String msg) {
        System.out.println("Friend 1 has been notified");
    }

    @Override
    public void send(String msg) {
        mediator.send(this,msg);
    }
}
