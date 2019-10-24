package mdpalgo.models;

import mdpalgo.constants.CommConstants;
import mdpalgo.constants.Direction;
import mdpalgo.constants.Movement;
import mdpalgo.constants.RobotConstant;
import mdpalgo.simulator.Simulator;
import mdpalgo.utils.Connection;

/**
 *          ^   ^   ^
 *         SR  SR  SR
 *   < LR [X] [X] [X] SR >
 *        [X] [X] [X]
 *        [X] [X] [X] SR >
 *
 * SR = Short Range Sensor, LR = Long Range Sensor
 *
 */

public class Robot {
    private int posRow;
    private int posCol;
    private Direction direction;
    private int speed;
    private Sensor sFrontRight;
    private Sensor sFrontLeft;
    private Sensor sFront;
    private Sensor lLeft;
    private Sensor sRightFront;
    private Sensor sRight;

    private static final int SENSOR_SHORT_RANGE_L = 1;
    private static final int SENSOR_SHORT_RANGE_H = 3;
    private static final int SENSOR_LONG_RANGE_L = 1;
    private static final int SENSOR_LONG_RANGE_H = 5;

    public Robot(int row, int col, Direction direction) {
        this.posRow = row;
        this.posCol = col;
        this.direction = direction;
        this.speed = RobotConstant.SPEED;

        sFrontRight = new Sensor(SENSOR_SHORT_RANGE_L, SENSOR_SHORT_RANGE_H, "SFR");
        sFrontLeft = new Sensor(SENSOR_SHORT_RANGE_L, SENSOR_SHORT_RANGE_H, "SFL");
        sFront = new Sensor(SENSOR_SHORT_RANGE_L, SENSOR_SHORT_RANGE_H, "SF");
        lLeft = new Sensor(SENSOR_LONG_RANGE_L, SENSOR_LONG_RANGE_H, "LL");
        sRightFront = new Sensor(SENSOR_SHORT_RANGE_L, SENSOR_SHORT_RANGE_H, "SRF");
        sRight = new Sensor(SENSOR_SHORT_RANGE_L, SENSOR_SHORT_RANGE_H, "SR");
    }

    public void move(Movement movement, int step) {
        if (step < 0) {
            System.out.println("Given a step less than 0");
            // move(movement);
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
        if (!Simulator.testRobot) {
            try {
                Thread.sleep(speed);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
    	if(!Simulator.testRobot) {
            lLeft.sense(direction.getFrontLeft(posRow, posCol), direction.turnLeft(), currentGrid, realGrid);
            sFrontLeft.sense(direction.getFrontLeft(posRow, posCol), direction, currentGrid, realGrid);
            sFront.sense(direction.forward(posRow, posCol), direction, currentGrid, realGrid);
            sFrontRight.sense(direction.getFrontRight(posRow, posCol), direction, currentGrid, realGrid);
            sRightFront.sense(direction.getFrontRight(posRow, posCol), direction.turnRight(), currentGrid, realGrid);
            sRight.sense(direction.getBackRight(posRow, posCol), direction.turnRight(), currentGrid, realGrid);

		} else {
    		int[] result = new int[6];

    		Connection connect = Connection.getConnection();

    		String msg = connect.receiveMessage();
            if (!msg.contains(CommConstants.OBS)) return;

            String[] msgArr = msg.split(":");
            String[] msgArr2 = msgArr[1].split("\\|");

            if (msgArr[0].equals(CommConstants.OBS)) {
			    result[0] = Integer.parseInt(msgArr2[0]);
			    result[1] = Integer.parseInt(msgArr2[1]);
			    result[2] = Integer.parseInt(msgArr2[2]);
			    result[3] = Integer.parseInt(msgArr2[3]);
			    result[4] = Integer.parseInt(msgArr2[4]);
			    result[5] = Integer.parseInt(msgArr2[5]);

                lLeft.senseReal(direction.getFrontLeft(posRow, posCol), direction.turnLeft(), currentGrid, result[0]);
                sFrontLeft.senseReal(direction.getFrontLeft(posRow, posCol), direction, currentGrid, result[1]);
                sFront.senseReal(direction.forward(posRow, posCol), direction, currentGrid, result[2]);
                sFrontRight.senseReal(direction.getFrontRight(posRow, posCol), direction, currentGrid, result[3]);
                sRightFront.senseReal(direction.getFrontRight(posRow, posCol), direction.turnRight(), currentGrid, result[4]);
                sRight.senseReal(direction.getBackRight(posRow, posCol), direction.turnRight(), currentGrid, result[5]);
            }
        }
    }

    public boolean isSafeMovement(Movement movement, Grid grid) {
        Direction newDirection = this.direction.rotate(movement);
        int[] pos = newDirection.forward(this.posRow, this.posCol);
        int[] newPos = newDirection.forward(pos[0], pos[1]);
        int[] newPosRight = newDirection.getFrontRight(pos[0], pos[1]);
        int[] newPosLeft = newDirection.getFrontLeft(pos[0], pos[1]);
        return 	grid.isValid(newPos[0], newPos[1])
                && grid.isValid(newPosRight[0], newPosRight[1])
                && grid.isValid(newPosLeft[0], newPosLeft[1])
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


    /**
     * Check whether the robot is at the corner and
     * is able to do the corner calibration
     *
     * @param currentGrid
     * @param currentGrid
     * @return
     */

    public boolean canCalibrateFront(Grid currentGrid) {
        int row = getPosRow();
        int col = getPosCol();

        Direction direction = getDirection();
        // front side is wall or obs
        int[] pos = direction.getFrontRight(row, col);
        int[] frontRight = direction.forward(pos[0], pos[1]);

        pos = direction.getFrontLeft(row, col);
        int[] frontLeft = direction.forward(pos[0], pos[1]);

        return currentGrid.isWallOrObstable(frontRight[0], frontRight[1])
                && currentGrid.isWallOrObstable(frontLeft[0], frontLeft[1]);
    }

    public boolean canCalibrateRight(Grid currentGrid) {
        int row = getPosRow();
        int col = getPosCol();

        Direction direction = getDirection();
        // right side is wall or obs
        int[] pos = direction.getFrontRight(row, col);
        int[] rightFront = direction.turnRight().forward(pos[0], pos[1]);

        pos = direction.getBackRight(row, col);
        int[] right = direction.turnRight().forward(pos[0], pos[1]);

        return currentGrid.isWallOrObstable(rightFront[0], rightFront[1])
                && currentGrid.isWallOrObstable(right[0], right[1]);
    }
    
    public boolean canCalibrateLeft(Grid currentGrid) {
        int row = getPosRow();
        int col = getPosCol();

        Direction direction = getDirection();
        // right side is wall or obs
        int[] pos = direction.getFrontLeft(row, col);
        int[] right = direction.turnLeft().forward(pos[0], pos[1]);

        pos = direction.backward(row, col,2);
        if (currentGrid.isValid(pos)) {
	        int[] posBack = direction.getFrontLeft(pos[0], pos[1]);
	        int[] left = direction.turnLeft().forward(posBack[0], posBack[1]);
	
	        return currentGrid.isWallOrObstable(right[0], right[1])
	                && currentGrid.isWallOrObstable(left[0], left[1]);
        }
        else {
        	return false;
        }
    }

    public boolean canCalibrateFrontRight(Grid currentGrid) {
        return canCalibrateRight(currentGrid)
                && canCalibrateFront(currentGrid);
    }
}