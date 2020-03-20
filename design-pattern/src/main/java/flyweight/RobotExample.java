package flyweight;

public class RobotExample {
    public static void main(String[] args) {
        RobotFactory robotFactory = new RobotFactory();
        IRobot iRobot = robotFactory.getRobotFromFactory("small");
        iRobot.print();

        for(int i = 0;i<2;i++){
            iRobot = robotFactory.getRobotFromFactory("small");
            iRobot.print();
        }
        System.out.println("the number of  created robots : "+ robotFactory.totalRobotsCreated());

        for(int i=0;i<5;i++){
            iRobot = robotFactory.getRobotFromFactory("large");
            iRobot.print();
        }
        System.out.println("the number of  created robots : "+ robotFactory.totalRobotsCreated());
    }
}
