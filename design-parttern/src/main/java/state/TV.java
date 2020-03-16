package state;

public class TV {
    private RemoteControl state;

    public TV(RemoteControl state){
        this.state = state;
    }
    public void setState(RemoteControl state) {
        this.state = state;
    }
    public  void pressBusston(){
        state.pressSwitch(this);
    }
}
