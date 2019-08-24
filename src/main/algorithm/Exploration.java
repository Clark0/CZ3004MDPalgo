package algorithm;

import constants.Direction;
import constants.Movement;
import models.Grid;
import models.Robot;

public class Exploration {
    private Grid currentGrid;
    private Grid realGrid;
    private Robot robot;

    private void moveRobot(Movement movement) {
        robot.move(movement);
    }

    private boolean lookRight() {
        Direction right = robot.getDirection().turnRight();
        return false;
    }

    private boolean lookForward() {
        return false;
    }

    private boolean free(Direction direction) {
        return false;
    }
}
