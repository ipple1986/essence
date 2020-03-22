package mediator;

public class Boss implements Person {
    private String name;
    private Mediator mediator;
    public Boss(String name,Mediator mediator){
        this.name = name;
        this.mediator = mediator;
    }
    @Override
    public void notify(String msg) {
        System.out.println("Boss has been notified");
    }

    @Override
    public void send(String msg) {
        mediator.send(this,msg);
    }
}
