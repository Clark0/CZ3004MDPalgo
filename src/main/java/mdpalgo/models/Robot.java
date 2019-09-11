package mdpalgo.models;

import mdpalgo.constants.Direction;
import mdpalgo.constants.Movement;
import mdpalgo.constants.RobotConstant;
import mdpalgo.utils.Connection;
import mdpalgo.utils.GridDescriptor;

/**
 * Represents the robot moving in the arena.
 *
 * The robot is represented by a 3 x 3 cell space as below:
 *
 *          ^   ^   ^
 *         SR  LR  SR      
 *   < SR [X] [X] [X]
 *   < LR [X] [X] [X] SR >
 *        [X] [X] [X]
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
    private Sensor lFront;
    private Sensor sLeft;
    private Sensor lLeft;
    private Sensor sRight;

    private static final int SENSOR_SHORT_RANGE_L = 1;
    private static final int SENSOR_SHORT_RANGE_H = 3;
    private static final int SENSOR_LONG_RANGE_L = 3;
    private static final int SENSOR_LONG_RANGE_H = 5;

    public Robot(int row, int col, Direction direction) {
        this.posRow = row;
        this.posCol = col;
        this.direction = direction;
        this.speed = RobotConstant.SPEED;

        sFrontRight = new Sensor(SENSOR_SHORT_RANGE_L, SENSOR_SHORT_RANGE_H);
        sFrontLeft = new Sensor(SENSOR_SHORT_RANGE_L, SENSOR_SHORT_RANGE_H);
        lFront = new Sensor(SENSOR_SHORT_RANGE_L, SENSOR_LONG_RANGE_H);
        sLeft = new Sensor(SENSOR_SHORT_RANGE_L, SENSOR_SHORT_RANGE_H);
        lLeft = new Sensor(SENSOR_LONG_RANGE_L, SENSOR_LONG_RANGE_H);
        sRight = new Sensor(SENSOR_SHORT_RANGE_L, SENSOR_SHORT_RANGE_H);

    }

    public void move(Movement movement, int step) {
    	
    	Connection connect = Connection.getConnection();
        connect.sendMsg(Movement.print(movement) + "," + step , Connection.INSTR);
        
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

        Connection connect = Connection.getConnection();

        if (movement == Movement.FORWARD) {
        	connect.sendMsg(Movement.print(movement) + ",1", Connection.INSTR);
        } else {
        	connect.sendMsg(Movement.print(movement) + ",0", Connection.INSTR);
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
    	/*Connection connect = Connection.getConnection();
    	String msg = connect.recvMsg();
        String[] msgArr = msg.split(";");
        
    	if(!msgArr[0].equals(Connection.SDATA)) {*/
    		
    		sFrontRight.sense(direction.getFrontRight(posRow, posCol), direction, currentGrid, realGrid);
    		sFrontLeft.sense(direction.getFrontLeft(posRow, posCol), direction, currentGrid, realGrid);
    		lFront.sense(direction.forward(posRow, posCol), direction, currentGrid, realGrid);
    		sLeft.sense(direction.getFrontLeft(posRow, posCol), direction.turnLeft(), currentGrid, realGrid);
    		lLeft.sense(direction.getLeft(posRow, posCol), direction.turnLeft(), currentGrid, realGrid);
    		sRight.sense(direction.getFrontRight(posRow, posCol), direction.turnRight(), currentGrid, realGrid);    
	/*	}
    	else {
    		int[] result = new int[6];
    		
            result[0] = Integer.parseInt(msgArr[1].split("_")[1]);
            result[1] = Integer.parseInt(msgArr[2].split("_")[1]);
            result[2] = Integer.parseInt(msgArr[3].split("_")[1]);
            result[3] = Integer.parseInt(msgArr[4].split("_")[1]);
            result[4] = Integer.parseInt(msgArr[5].split("_")[1]);
            result[5] = Integer.parseInt(msgArr[6].split("_")[1]);
            
            sLeft.senseReal(direction.getFrontLeft(posRow, posCol), direction.turnLeft(), currentGrid, result[0]);
    		lLeft.senseReal(direction.getLeft(posRow, posCol), direction.turnLeft(), currentGrid, result[1]);
    		sFrontLeft.senseReal(direction.forward(posRow, posCol), direction, currentGrid, result[2]);
    		lFront.senseReal(direction.forward(posRow, posCol), direction, currentGrid, result[3]);
    		sFrontRight.senseReal(direction.forward(posRow, posCol), direction, currentGrid, result[4]);
    		sRight.senseReal(direction.getFrontRight(posRow, posCol), direction.turnRight(), currentGrid, result[5]);    	
    	}*/
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

}