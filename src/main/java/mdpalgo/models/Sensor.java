package mdpalgo.models;

import mdpalgo.constants.CommConstants;
import mdpalgo.constants.Direction;
import mdpalgo.utils.Connection;

public class Sensor {
    private final String id;
    private int lowerRange;
    private int upperRange;
    private int iDirection;
    private int iTaken = 0;

    public Sensor(int lowerRange, int upperRange, String id) {
        this.lowerRange = lowerRange;
        this.upperRange = upperRange;
        this.id = id;
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
    
    public void senseReal(int[] pos, Direction direction, Grid currentGrid, int sensorVal) {
        // Update map according to sensor's value.
        for (int i = this.lowerRange; i <= this.upperRange; i++) {
            int[] position = direction.forward(pos[0], pos[1], i);
            int x = position[0];
            int y = position[1];

            if (!currentGrid.isValid(x, y)) return;
            currentGrid.setExplored(x, y);

            if (sensorVal == i) {
                // obstacle position

            	switch (direction) {
	                case NORTH:
	                	iDirection = 0;
	                	break;
	                case EAST:
	                	iDirection = 1;
	                	break;
	                case SOUTH:
	                	iDirection = 2;
	                	break;
	                case WEST:
	                	iDirection = 3;
	                	break;
	            }
            	
            	if (this.id.equals("SF") && iTaken != 5) {
	            	if (currentGrid.getImageObstacle(x, y, iDirection) != 0 || currentGrid.getImageObstacle(x, y, iDirection) != 1) {
	            		if (sensorVal != 1) {
	            			takePhoto(currentGrid, x, y, iDirection, sensorVal);
	            		}
	            	}       		
            	}
            	
                currentGrid.setObstacle(x, y);
                break;
            }
        }
    }
    
    public void takePhoto(Grid grid, int x, int y, int z, int range) {
    	
    	Connection connect = Connection.getConnection();
		connect.sendMessage(CommConstants.IMAGE, "img:" + x + "," + y + "," + range);

		System.out.println("img:" + x + "," + y + "," + range);
		
    	String msg = connect.receiveMessage();
        String[] msgArr = msg.split(":");
        String[] msgArr2 = msgArr[1].split(",");

        if (msgArr[0].equals(-1)) {
        	grid.setImageObstacle(Integer.parseInt(msgArr2[1]), Integer.parseInt(msgArr2[2]), z, 0);
			System.out.println(msgArr2[0] + " " + msgArr2[1] + " " + msgArr2[2]);
        } else {
        	iTaken++;
			for (int oDir = 0; oDir < 4; oDir++) {
				grid.setImageObstacle(Integer.parseInt(msgArr2[1]), Integer.parseInt(msgArr2[2]), oDir, 1);
			}
        }
    }
}
