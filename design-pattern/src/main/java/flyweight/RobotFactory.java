package flyweight;

import java.util.HashMap;
import java.util.Map;

public class RobotFactory {
    private Map<String,IRobot> maps = new HashMap<>();
    public int totalRobotsCreated(){
        return maps.size();
    }
    public IRobot getRobotFromFactory(String type){
        IRobot iRobot = null;
        if(maps.containsKey(type)){
            iRobot =  maps.get(type);
        }
        switch (type){
            case "small":
                iRobot =   new SmallRobot();
                maps.put("small",iRobot);
                break;
            case "large":
                iRobot =   new LargeRobot();
                maps.put("large",iRobot);
                break;
            default: new Exception("not support robot type");
        }
        return iRobot;
    }
}
