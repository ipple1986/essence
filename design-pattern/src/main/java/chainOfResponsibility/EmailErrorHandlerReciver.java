package chainOfResponsibility;

public class EmailErrorHandlerReciver implements IReciver {
    private IReciver next;
    public EmailErrorHandlerReciver(IReciver next){
        this.next = next;
    }
    @Override
    public void processMsg(Message message) {
        if(message.getMsg().contains("Email")){
            System.out.println(" Email::"+ message.getMsg());
        }else if(next!=null){
            next.processMsg(message);
        }
    }
}
