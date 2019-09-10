package mdpalgo.utils;

import mdpalgo.models.Grid;
import mdpalgo.models.Robot;

public class SendUtil {
    public static Connection connect;
    
    public static void sendGrid(Grid grid) {
        connect = Connection.getConnection();
        String serializedMap = GridDescriptor.serializeGrid(grid);
        connect.sendMsg(serializedMap, Connection.MAP);
    }

    public static void sendRobotPos(Robot robot) {
        connect = Connection.getConnection();
        connect.sendMsg(robot.getPosRow() + "," + robot.getPosCol() + "," + robot.getDirection().ordinal(), Connection.BOT_POS);
    }
}
