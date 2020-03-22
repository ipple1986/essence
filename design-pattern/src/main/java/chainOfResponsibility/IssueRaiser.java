package chainOfResponsibility;

public class IssueRaiser implements IReciver{
    private IReciver firstReciver;
    public IssueRaiser(IReciver firstReciver){
        this.firstReciver = firstReciver;
    }

    @Override
    public void processMsg(Message message) {
        if(firstReciver!=null){
            firstReciver.processMsg(message);
        }
    }
}
