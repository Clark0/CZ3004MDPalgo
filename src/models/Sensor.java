package models;

import constants.Direction;

public class Sensor {
    private int lowerRange;
    private int upperRange;

    public Sensor(int lowerRange, int upperRange) {
        this.lowerRange = lowerRange;
        this.upperRange = upperRange;
    }

    public int sense(int row, int cal, Direction direction, Grid currentGrid, Grid realGrid) {
        for (int i = lowerRange; i <= upperRange; i++) {
            int[] position = direction.forward(row, cal, i);
            int x = position[0];
            int y = position[1];
            // out of range
            if (!realGrid.isValid(x, y))
                return i;

            /*currentGrid.setExplored(x, y);
            if (realGrid.isObstacle(x, y)){
                return i;
            }*/
        }

        return -1;
    }

    public int sense(int[] pos, Direction direction, Grid currentGrid, Grid realGrid) {
        return sense(pos[0], pos[1], direction, currentGrid, realGrid);
    }
}
