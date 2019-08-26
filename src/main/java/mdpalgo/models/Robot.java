package mdpalgo.models;

import mdpalgo.constants.Direction;
import mdpalgo.constants.Movement;
import mdpalgo.constants.RobotConstant;
import mdpalgo.utils.Connection;
import mdpalgo.utils.GridDescriptor;


public class Robot {
    private int posRow;
    private int posCol;
    private Direction direction;
    private int speed;
    private Sensor[] sensorsFront;
    private Sensor sensorFrontLeft;
    private Sensor sensorFrontRight;
    private Sensor sensorRight;

    private static final int SENSOR_SHORT_RANGE_L = 1;
    private static final int SENSOR_SHORT_RANGE_H = 2;
    private static final int SENSOR_LONG_RANGE_L = 3;
    private static final int SENSOR_LONG_RANGE_H = 4;

    public Robot(int row, int col, Direction direction) {
        this.posRow = row;
        this.posCol = col;
        this.direction = direction;
        this.speed = RobotConstant.SPEED;

        sensorsFront = new Sensor[3];
        sensorFrontLeft = new Sensor(SENSOR_SHORT_RANGE_L, SENSOR_SHORT_RANGE_H);
        sensorFrontRight = new Sensor(SENSOR_SHORT_RANGE_L, SENSOR_SHORT_RANGE_H);
        sensorRight = new Sensor(SENSOR_SHORT_RANGE_L, SENSOR_SHORT_RANGE_H);
        for (int i = 0; i < 3; i++) {
            sensorsFront[i] = new Sensor(SENSOR_SHORT_RANGE_L, SENSOR_SHORT_RANGE_H);
        }

    }

    public void move(Movement movement, int step) {
        if (step <= 0) {
            move(movement);
            return;
        }

        try {
            Thread.sleep(speed);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Move the robot 'step' steps in any case
        this.direction = this.direction.rotate(movement);
        int[] newPosition = this.direction.forward(posRow, posCol, step);
        this.posRow = newPosition[0];
        this.posCol = newPosition[1];
    }

    public void move(Movement movement) {
        try {
            Thread.sleep(speed);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.direction = this.direction.rotate(movement);
        if (movement == Movement.FORWARD) {
            // Only the Forward command will move the robot
            int[] newPosition = this.direction.forward(posRow, posCol);
            this.posRow = newPosition[0];
            this.posCol = newPosition[1];
        }
    }

    public void sense(Grid currentGrid, Grid realGrid) {
        sensorFrontRight.sense(direction.getFrontRight(posRow, posCol), direction.turnRight(), currentGrid, realGrid);
        sensorFrontLeft.sense(direction.getFrontLeft(posRow, posCol), direction.turnLeft(), currentGrid, realGrid);
        sensorRight.sense(direction.getRight(posRow, posCol), direction.turnRight(), currentGrid, realGrid);
        int[][] head = getHead();
        for (int i = 0; i < 3; i++) {
            sensorsFront[i].sense(head[i], direction, currentGrid, realGrid);
        }

        Connection connect = Connection.getConnection();
        String serializedMap = GridDescriptor.serializeGrid(currentGrid);
        connect.sendMsg(serializedMap, Connection.MAP);

    }

    public boolean isSafeMovement(Movement movement, Grid grid) {
        Direction newDirection = this.direction.rotate(movement);
        int[] newPos = newDirection.forward(this.posRow, this.posCol);
        int[] newPosRight = newDirection.getFrontRight(this.posRow, this.posCol);
        int[] newPosLeft = newDirection.getFrontLeft(this.posRow, this.posCol);
        return !grid.isVirtualWall(newPos[0], newPos[1])
                && grid.isExplored(newPos[0], newPos[1])
                && !grid.isObstacle(newPos[0], newPos[1])
                && grid.isExplored(newPosRight[0], newPosRight[1])
                && !grid.isObstacle(newPosRight[0], newPosRight[1])
                && grid.isExplored(newPosLeft[0], newPosLeft[1])
                && !grid.isObstacle(newPosLeft[0], newPosLeft[1]);

    }

    public int[] getRight() {
        return direction.getRight(posRow, posCol);
    }

    public int[] getLeft() {
        return direction.getLeft(posRow, posCol);
    }

    public int[][] getHead() {
        int[] centerHead = direction.forward(posRow, posCol);
        int[] rightHead = direction.getFrontRight(posRow, posCol);
        int[] leftHead = direction.getFrontLeft(posRow, posCol);
        return new int[][]{leftHead, centerHead, rightHead};
    }

    public Direction getDirection() {
        return direction;
    }

    public int getPosRow() {
        return posRow;
    }

    public int getPosCol() {
        return posCol;
    }

    public void setRobotPosition(int x, int y) {
        this.posRow = x;
        this.posCol = y;
    }

    public void setDirection(Direction d) {
        this.direction = d;
    }

    public void setSpeed(int speed) {this.speed = speed;}

    public int getSpeed() {
        return speed;
    }
}
