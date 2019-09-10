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
    private Sensor sensorLeft;

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
        sensorLeft = new Sensor(SENSOR_LONG_RANGE_L, SENSOR_LONG_RANGE_H);
        for (int i = 0; i < 3; i++) {
            sensorsFront[i] = new Sensor(SENSOR_SHORT_RANGE_L, SENSOR_SHORT_RANGE_H);
        }

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
        connect.sendMsg(Movement.print(movement) + ",1", Connection.INSTR);
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
	        sensorFrontRight.sense(direction.getFrontRight(posRow, posCol), direction.turnRight(), currentGrid, realGrid);
	        sensorFrontLeft.sense(direction.getFrontLeft(posRow, posCol), direction.turnLeft(), currentGrid, realGrid);
	        sensorLeft.sense(direction.getLeft(posRow, posCol), direction.turnLeft(), currentGrid, realGrid);
	        int[][] head = getHead();
	        for (int i = 0; i < 3; i++) {
	            sensorsFront[i].sense(head[i], direction, currentGrid, realGrid);
	        }	
    	/*}
    	else {
    		int[] result = new int[6];
    		
            result[0] = Integer.parseInt(msgArr[1].split("_")[1]);
            result[1] = Integer.parseInt(msgArr[2].split("_")[1]);
            result[2] = Integer.parseInt(msgArr[3].split("_")[1]);
            result[3] = Integer.parseInt(msgArr[4].split("_")[1]);
            result[4] = Integer.parseInt(msgArr[5].split("_")[1]);
            result[5] = Integer.parseInt(msgArr[6].split("_")[1]);
            
            sensorFrontRight.senseReal(direction.getFrontRight(posRow, posCol), direction.turnRight(), currentGrid, result[0]);
	        sensorFrontLeft.senseReal(direction.getFrontLeft(posRow, posCol), direction.turnLeft(), currentGrid, result[1]);
	        sensorLeft.senseReal(direction.getLeft(posRow, posCol), direction.turnLeft(), currentGrid, result[2]);
	        int[][] head = getHead();
	        for (int i = 0; i < 3; i++) {
	            sensorsFront[i].senseReal(head[i], direction, currentGrid, result[i+3]);
	        }	
    	}*/
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
    
    public void getEntireRobot(Grid grid, int x, int y) {
    	switch (this.direction) {
        case NORTH:
        	processRobotPos(grid, x, y, 1, 0);
            break;
        case EAST:
        	processRobotPos(grid, x, y, 0, 1);
            break;
        case SOUTH:
        	processRobotPos(grid, x, y, -1, 0);
            break;
        case WEST:
        	processRobotPos(grid, x, y, 0, -1);
            break;
    	}
    }
    
    private void processRobotPos(Grid grid, int x, int y, int row, int col) {
    	System.out.println("abs");
    	for (int i = x-1; i <= x+1; i++) {
            for (int j = y-1; j <= y+1; j++) {
            	
            	grid.setCell(i, j, 4);
            	
            }
    	}

    	System.out.println(grid.getCell(0, 0));
    	grid.setCell(x+row, y+col, 3);
    }
}
