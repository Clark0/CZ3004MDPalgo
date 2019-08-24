package mdpalgo.models;

import mdpalgo.constants.Direction;
import mdpalgo.constants.Movement;


public class Robot {
    private int posRow;
    private int posCol;
    private Direction direction;
    private Sensor[] sensorsFront;
    private Sensor sensorLeft;
    private Sensor sensorRight;

    private static final int SENSOR_SHORT_RANGE_L = 1;
    private static final int SENSOR_SHORT_RANGE_H = 2;
    private static final int SENSOR_LONG_RANGE_L = 3;
    private static final int SENSOR_LONG_RANGE_H = 4;

    public Robot(int row, int col, Direction direction) {
        this.posRow = row;
        this.posCol = col;
        this.direction = direction;

        sensorsFront = new Sensor[3];
        sensorLeft = new Sensor(SENSOR_SHORT_RANGE_L, SENSOR_SHORT_RANGE_H);
        sensorRight = new Sensor(SENSOR_SHORT_RANGE_L, SENSOR_SHORT_RANGE_H);
        for (int i = 0; i < 3; i++) {
            sensorsFront[i] = new Sensor(SENSOR_SHORT_RANGE_L, SENSOR_SHORT_RANGE_H);
        }
    }

    public void move(Movement movement, int steps) {
        this.direction = this.direction.rotate(movement);
        if (movement == Movement.FORWARD) {
            int[] newPosition = this.direction.forward(posRow, posCol, steps);
            this.posRow = newPosition[0];
            this.posCol = newPosition[1];
        }
    }

    public void move(Movement movement) {
        this.move(movement, 1);
    }

    public int[] sense(Grid currentGrid, Grid realGrid) {
        int[] result = new int[6];
        sensorRight.sense(direction.getFrontRight(posRow, posCol), direction.turnRight(), currentGrid, realGrid);
        sensorLeft.sense(direction.getFrontLeft(posRow, posCol), direction.turnLeft(), currentGrid, realGrid);
        int[][] head = getHead();
        for (int i = 0; i < 3; i++) {
            sensorsFront[i].sense(head[i], direction, currentGrid, realGrid);
        }

        return result;
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
}
