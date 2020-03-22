package mediator;

public class ConcreteMediator implements Mediator {
    private Friend1 friend1;
    private Friend2 friend2;
    private Boss boss;
    @Override
    public void send(Person person, String msg) {
        if(person==friend1){
            friend2.notify(msg);
            boss.notify(msg);
        }else if(person==friend2){
            friend1.notify(msg);
            boss.notify(msg);
        }else{
            friend1.notify(msg);
            friend2.notify(msg);
        }

    }

    public void setBoss(Boss boss) {
        this.boss = boss;
    }

    public void setFriend1(Friend1 friend1) {
        this.friend1 = friend1;
    }

    public void setFriend2(Friend2 friend2) {
        this.friend2 = friend2;
    }
}
