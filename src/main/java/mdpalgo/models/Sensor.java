package mdpalgo.models;

import mdpalgo.constants.CommConstants;
import mdpalgo.constants.Direction;
import mdpalgo.simulator.Simulator;
import mdpalgo.utils.Connection;

public class Sensor {
    private final String id;
    private int lowerRange;
    private int upperRange;

    public Sensor(int lowerRange, int upperRange, String id) {
        this.lowerRange = lowerRange;
        this.upperRange = upperRange;
        this.id = id;
    }

    public int sense(int row, int col, Direction direction, Grid currentGrid, Grid realGrid) {
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
        if (lowerRange > 1) {
            for (int i = 1; i < this.lowerRange; i++) {
                int[] front = direction.forward(pos[0], pos[1], i);
                if (!currentGrid.isValid(front[0], front[1]) || currentGrid.isObstacle(front[0], front[1])) {
                    return;
                }
            }
        }

        // Update map according to sensor's value.
        for (int i = this.lowerRange; i <= this.upperRange; i++) {
            int[] position = direction.forward(pos[0], pos[1], i);
            int x = position[0];
            int y = position[1];

            if (!currentGrid.isValid(x, y))
                return;

            if (!currentGrid.isObstacle(x, y)) {
                currentGrid.setExplored(x, y);
            }

            if (sensorVal == i) {
                // obstacle position
            	if (Simulator.testImage && this.id.equals("SF")) {
            	    takePhoto(x, y, sensorVal, direction);
            	}

            	// set the cell to obstacle only when it was never occupied by the robot before.
                if (!currentGrid.isVisited(x, y)) {
                    currentGrid.setObstacle(x, y);
                }

                break;
            }

            // override with front sensors
            if (currentGrid.isObstacle(x, y)) {
                if ((id.equals("SFR") || id.equals("SF") || id.equals("SFL")
                        || id.equals("SRF") || id.equals("SR")) && i <= 2
                    || (id.equals("LL") && i <= 3)){
                    currentGrid.setExplored(x, y);
                } else {
                    break;
                }
            }
        }
    }
    
    private void takePhoto(int x, int y, int range, Direction direction) {
    	Connection connect = Connection.getConnection();
		connect.sendMessage(CommConstants.IMAGE, x + "," + y + "," + range + "," + direction.toString());

		// wait for image taken
		while (true) {
		    String msg = connect.receiveMessage();
		    String[] msgArr = msg.split(":");
		    if (msgArr[0].equals(CommConstants.IMAGE) && msgArr[1].equals("taken")) {
		        break;
            }
        }
    }
}
