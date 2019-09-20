package mdpalgo.utils;

import mdpalgo.constants.CommConstants;
import mdpalgo.constants.Movement;
import mdpalgo.models.Grid;
import mdpalgo.models.Robot;

public class SendUtil {
    public static Connection connect;
    public static void sendGrid(Grid grid) {
        connect = Connection.getConnection();
        String serializedMap = GridDescriptor.serializeGrid(grid);
        connect.sendMessage(CommConstants.MAP, serializedMap);
    }

    public static void sendRobotPos(Robot robot) {
        connect = Connection.getConnection();
        connect.sendMessage(CommConstants.BOT_POS, robot.getPosRow() + "," + robot.getPosCol() + "," + robot.getDirection().toString());
    }

    public static void sendMoveRobotCommand(Movement movement, int step) {
        connect = Connection.getConnection();
        connect.sendMessage(CommConstants.MOVE, Movement.print(movement) + "," + step);
    }
}
