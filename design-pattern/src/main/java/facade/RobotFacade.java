package facade;

public class RobotFacade {
    private RobotBody robotBody;
    private RobotMetal robotMetal;
    private RobotColor robotColor;
    public RobotFacade(){
        robotBody = new RobotBody();
        robotColor = new RobotColor();
        robotMetal = new RobotMetal();
    }
    public void createRobot(String color,String metal){
        System.out.println("====Start Create Robot=======");
        robotColor.setColor(color);
        robotMetal.setMetal(metal);
        robotBody.createRobotBody();
        System.out.println("====End Create Robot=======");
    }
}
