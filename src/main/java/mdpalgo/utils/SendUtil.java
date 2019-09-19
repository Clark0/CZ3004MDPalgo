package mdpalgo.utils;

import mdpalgo.constants.CommConstants;
import mdpalgo.models.Grid;
import mdpalgo.models.Robot;

public class SendUtil {
    public static Connection connect;
    public static void sendGrid(Grid grid) {
        connect = Connection.getConnection();
        String serializedMap = GridDescriptor.serializeGrid(grid);
        connect.sendMessage(serializedMap, CommConstants.MAP);
    }

    public static void sendRobotPos(Robot robot) {
        connect = Connection.getConnection();
        connect.sendMessage(robot.getPosRow() + "," + robot.getPosCol() + "," + robot.getDirection().toString(), CommConstants.BOT_POS);
    }
}
