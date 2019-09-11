package mdpalgo.models;

import mdpalgo.constants.Direction;

public class Sensor {
    private int lowerRange;
    private int upperRange;

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
    
    public void senseReal(int[] pos, Direction direction, Grid currentGrid, int sensorVal) {
    	if (sensorVal == 0) return;  // return value for LR sensor if obstacle before lowerRange

        // If above fails, check if starting point is valid for sensors with lowerRange > 1.
        for (int i = 1; i < this.lowerRange; i++) {

            if (!currentGrid.isValid(pos[0], pos[1])) return;
            if (currentGrid.isObstacle(pos[0], pos[1])) return;
        }

        // Update map according to sensor's value.
        for (int i = this.lowerRange; i <= this.upperRange; i++) {
            if (!currentGrid.isValid(pos[0], pos[1])) continue;

            currentGrid.setExplored(pos[0], pos[1]);

            if (sensorVal == i) {
                currentGrid.setObstacle(pos[0], pos[1]);
                break;
            }

            /* Override previous obstacle value if front sensors detect no obstacle.
            if (currentGrid.isObstacle(pos[0], pos[1])) {
                if (id.equals("SRFL") || id.equals("SRFC") || id.equals("SRFR")) {
                	currentGrid.setObstacle(pos[0], pos[1]);
                } else {
                    break;
                }
            }*/
        }
    }
}
