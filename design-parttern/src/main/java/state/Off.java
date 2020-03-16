package state;

public class Off implements RemoteControl {
    @Override
    public void pressSwitch(TV context) {
        System.out.println("I am off,will to be on..");
        context.setState(new On());

    }
}
