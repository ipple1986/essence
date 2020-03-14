package command;

public class CommandExample {
    // client
    public static void main(String[] args) {
        Invoker invoker = new Invoker();
        Reciver reciver = new Reciver();

        ICommand logCommand = new LogCommand(reciver);
        ICommand redoCommand = new RedoCommand(reciver);
        ICommand undoCommand = new UndoCommand(reciver);

        invoker.execCommand(logCommand);
        invoker.execCommand(redoCommand);
        invoker.execCommand(undoCommand);
    }
}
// Commands
interface  ICommand{
    void exec();
}
class LogCommand implements ICommand{
    Reciver reciver;
    public LogCommand(Reciver reciver){
        this.reciver = reciver;
    }
    @Override
    public void exec() {
            reciver.performLog();
    }
}
class UndoCommand implements ICommand{
    Reciver reciver;
    public UndoCommand(Reciver reciver){
        this.reciver = reciver;
    }
    @Override
    public void exec() {
        reciver.performUndo();
    }
}
class RedoCommand implements ICommand{
    Reciver reciver;
    public RedoCommand(Reciver reciver){
        this.reciver = reciver;
    }
    @Override
    public void exec() {
        reciver.performRedo();
    }
}
// reciver
class Reciver{
    public void performLog(){
        System.out.println("loging....");
    }
    public void performUndo(){
        System.out.println("undoing....");
    }
    public void performRedo(){
        System.out.println("redoing....");
    }
}
// invoker
class Invoker{
    public void execCommand(ICommand command){
        command.exec();
    }
}