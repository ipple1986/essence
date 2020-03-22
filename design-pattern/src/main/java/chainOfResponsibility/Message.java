package chainOfResponsibility;

public class Message {
    enum PORIORTY{ HIGH,LOW }
    private PORIORTY poriorty;
    private String msg;
    public Message(String msg ,PORIORTY poriorty){
        this.msg = msg;
        this.poriorty = poriorty;
    }

    public String getMsg() {
        return msg;
    }
}
