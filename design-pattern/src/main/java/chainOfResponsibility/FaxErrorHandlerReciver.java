package chainOfResponsibility;

public class FaxErrorHandlerReciver implements IReciver {
    private IReciver next;
    public FaxErrorHandlerReciver(IReciver next){
        this.next = next;
    }
    @Override
    public void processMsg(Message message) {
        if(message.getMsg().contains("Fax")){
            System.out.println(" Fax::"+ message.getMsg());
        }else if(next!=null){
            next.processMsg(message);
        }
    }
}
