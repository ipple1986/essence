package chainOfResponsibility;

public class ChainOfResponsibilityExample {
    public static void main(String[] args) {
        IReciver faxReciver  = new FaxErrorHandlerReciver(null);
        IReciver emailReciver = new EmailErrorHandlerReciver(faxReciver);
        IssueRaiser issueRaiser =new IssueRaiser(emailReciver);

        issueRaiser.processMsg(new Message("Fax message11", Message.PORIORTY.HIGH));
        issueRaiser.processMsg(new Message("Email message11", Message.PORIORTY.LOW));
        issueRaiser.processMsg(new Message("Fax message22", Message.PORIORTY.HIGH));
        issueRaiser.processMsg(new Message("Email message22", Message.PORIORTY.LOW));
    }
}
