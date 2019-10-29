package mdpalgo.utils;

import mdpalgo.constants.CommConstants;
import mdpalgo.constants.Movement;
import mdpalgo.models.Grid;
import mdpalgo.models.Robot;
import mdpalgo.constants.Direction;

import static mdpalgo.constants.CommConstants.CALIBRATE_DONE;
import static mdpalgo.constants.CommConstants.IMAGE_DONE;


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
        if (step > 15) {
            System.out.println("Arduino does not accept steps larger than 15");
            return;
        }

        String encodedStep = step < 10 ? String.valueOf(step) : String.valueOf((char)('A' + step - 10));
        connect = Connection.getConnection();
        connect.sendMessage(CommConstants.MOVE, Movement.print(movement) + "," + encodedStep);
    }

    public static void sendCalibrateRight() {
        System.out.println("Calibrate robot, Right");
        connect = Connection.getConnection();
        connect.sendMessage(CommConstants.MOVE, CommConstants.CALIBRATE_RIGHT);
        // waiting for robot calibration finish
        while(!connect.receiveMessage().equals(CALIBRATE_DONE));
    }

    public static void sendCalibrateFrontRight() {
        System.out.println("Calibrate robot, Corner");
        connect = Connection.getConnection();
        connect.sendMessage(CommConstants.MOVE, CommConstants.CALIBRATE_FRONT_RIGHT);
        // waiting for robot calibration finish
        while(!connect.receiveMessage().equals(CALIBRATE_DONE));
    }

    public static void sendCalibrateFront() {
        System.out.println("Calibrate robot, Front");
        connect = Connection.getConnection();
        connect.sendMessage(CommConstants.MOVE, CommConstants.CALIBRATE_FRONT);
        // waiting for robot calibration finish
        while (!connect.receiveMessage().equals(CALIBRATE_DONE));
    }

    public static void sendCalibrateStepRight() {
        System.out.println("Calibrate robot, Step Right");
        connect = Connection.getConnection();
        connect.sendMessage(CommConstants.MOVE, CommConstants.CALIBRATE_STEP_RIGHT);
        // waiting for robot calibration finish
        while (!connect.receiveMessage().equals(CALIBRATE_DONE));
    }

    public static void sendCalibrateStepLeft() {
        System.out.println("Calibrate robot, Step Left");
        connect = Connection.getConnection();
        connect.sendMessage(CommConstants.MOVE, CommConstants.CALIBRATE_STEP_LEFT);
        // waiting for robot calibration finish
        while (!connect.receiveMessage().equals(CALIBRATE_DONE));
    }

    public static void sendSenseCommand() {
        connect = Connection.getConnection();
        connect.sendMessage(CommConstants.MOVE, CommConstants.SENSE);
    }

    public static void sendTakeImage(int x, int y, Direction direction) {
        connect = Connection.getConnection();
        connect.sendMessage(CommConstants.IMAGE, x + "," + y + "," + direction);
        while (!connect.receiveMessage().equals(IMAGE_DONE));
    }
}
