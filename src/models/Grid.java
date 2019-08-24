package models;

public class Grid {
    private int rows;
    private int cols;
    private boolean isExplored;
    private boolean isObstacle;
    private boolean isVirtualWall;

    public static final int ROWS = 20;
    public static final int COLS = 15;
    public static final int GRID_SIZE = 300;
    public static final int START_ROW = 1;
    public static final int START_COL = 1;
    public static final int GOAL_ROW = 18;
    public static final int GOAL_COL = 13;

    //private static final int EXPLORED = 1;
    //private static final int OBSTACLE = 2;
    //private static final int VIRTUAL_WALL = 3;

    public Grid() {
        this.rows = ROWS;
        this.cols = COLS;
    }

    public Grid(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
    }

    public boolean isValid(int x, int y) {
        return x >= 0 && x < this.rows && y >= 0 && y < this.cols;
    }

    public void setObstacle(boolean obs) {
        this.isObstacle = obs;
    }

    public boolean isObstacle() {
        return this.isObstacle;
    }

    public void setExplored(boolean exp) {
        this.isExplored = exp;
    }

    public boolean isExplored() {
        return this.isExplored;
    }
    
    public void setVirtualWall(boolean wall) {
        if(wall) {
        	this.isVirtualWall = wall;
        }
        else {
        	if(rows != 0 && rows != ROWS - 1 && cols != 0 && cols != COLS - 1) {
            	this.isVirtualWall = !wall;
        	}
        }
    }

    public boolean isVirtualWall() {
        return this.isVirtualWall;
    }

    


}
