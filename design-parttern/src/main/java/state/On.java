package state;

public class On implements RemoteControl {
    @Override
    public void pressSwitch(TV context) {
        System.out.println("I am on,will to be off");
        context.setState(new Off());
    }
}
