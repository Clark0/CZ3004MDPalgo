package mdpalgo.models;

import mdpalgo.constants.Direction;
import mdpalgo.utils.Connection;

public class Sensor {
    private int lowerRange;
    private int upperRange;
    private int iDirection;

    public Sensor(int lowerRange, int upperRange) {
        this.lowerRange = lowerRange;
        this.upperRange = upperRange;
    }

    public int sense(int row, int col, Direction direction, Grid currentGrid, Grid realGrid) {
        // TODO
        if (lowerRange > 1) {
            for (int i = 1; i < this.lowerRange; i++) {
                int[] pos = direction.forward(row, col, i);
                if (!realGrid.isValid(pos[0], pos[1]) || realGrid.isObstacle(pos[0], pos[1])) {
                    return i;
                }
            }
        }
        for (int i = lowerRange; i <= upperRange; i++) {
            int[] position = direction.forward(row, col, i);
            int x = position[0];
            int y = position[1];
            // out of range
            if (!realGrid.isValid(x, y)) {
                return i;
            }

            currentGrid.setExplored(x, y);
            if (realGrid.isObstacle(x, y)){
                currentGrid.setObstacle(x, y);
                return i;
            }
        }

        return -1;
    }

    public int sense(int[] pos, Direction direction, Grid currentGrid, Grid realGrid) {
        return sense(pos[0], pos[1], direction, currentGrid, realGrid);
    }
    
    public void senseReal(int[] pos, Direction direction, Grid currentGrid, int sensorVal, String sensorPos) {
    	
        for (int i = 1; i < this.lowerRange; i++) {

            if (!currentGrid.isValid(pos[0], pos[1])) return;
            if (currentGrid.isObstacle(pos[0], pos[1])) return;
        }

        // Update map according to sensor's value.
        for (int i = this.lowerRange; i <= this.upperRange; i++) {
            int[] position = direction.forward(pos[0], pos[1], i);
            int x = position[0];
            int y = position[1];
            
            if (!currentGrid.isValid(x, y)) continue;

            currentGrid.setExplored(x, y);

            if (sensorVal == i) {
            	
            	switch (direction) {
	                case NORTH:
	                	iDirection = 0;
	                case EAST:
	                	iDirection = 1;
	                case SOUTH:
	                	iDirection = 2;
	                case WEST:
	                	iDirection = 3;
	            }
            	
            	if (sensorPos == "SF") {
	            	if (currentGrid.getImageObstacle(x, y, iDirection) != 1 || currentGrid.getImageObstacle(x, y, iDirection) != 2)
	            		takePhoto(currentGrid, x, y, iDirection);
	            	
	            	if (currentGrid.getImageObstacle(x, y, iDirection) == 3 && sensorVal == 1)
	                	takePhoto(currentGrid, x, y, iDirection);            		
            	}
            	
                currentGrid.setObstacle(x, y);
                break;
            }
        }
    }
    
    public void takePhoto(Grid grid, int x, int y, int z) {
    	
    	Connection connect = Connection.getConnection();
		connect.sendMsg("image," + x + "," + y, Connection.IMAGE);

    	String msg = connect.recvMsg();
        String[] msgArr = msg.split(":");

        if (msgArr[0].equals(Connection.TNONE)) {
        	grid.setImageObstacle(x, y, z, 1);
        } 
        if (msgArr[0].equals(Connection.TFOUND)) {
        	grid.setImageObstacle(x, y, z, 2);
        }
        if (msgArr[0].equals(Connection.TNOT)) {
        	grid.setImageObstacle(x, y, z, 3);
        }
    }
}
