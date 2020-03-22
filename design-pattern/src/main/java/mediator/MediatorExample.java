package mediator;

public class MediatorExample {
    public static void main(String[] args) {
        Mediator mediator = new ConcreteMediator();

        Boss boss = new Boss("BossName",mediator);
        Friend1 friend1 = new Friend1("friend1",mediator);
        Friend2 friend2 = new Friend2("friend2",mediator);

        ((ConcreteMediator) mediator).setBoss(boss);
        ((ConcreteMediator) mediator).setFriend1(friend1);
        ((ConcreteMediator) mediator).setFriend2(friend2);

        System.out.println("=================");
        friend1.send("friend1 says ");
        System.out.println("=================");
        friend2.send("friend2 talks");
        System.out.println("=================");
        boss.send("boss laughing");
    }
}
