package facade;

public class RobotFacadeExample {
    public static void main(String[] args) {
        RobotFacade robotFacade1 = new RobotFacade();
        robotFacade1.createRobot("GREEN","IRON");
        robotFacade1.createRobot("RED","STEEL");
    }
}
