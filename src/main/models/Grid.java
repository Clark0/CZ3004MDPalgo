package models;

import utils.GridDescriptor;


public class Grid {
    private int rows;
    private int cols;
    private int[][] grid;

    public static final int ROWS = 20;
    public static final int COLS = 15;
    public static final int GRID_SIZE = ROWS * COLS;
    public static final int START_ROW = 1;
    public static final int START_COL = 1;

    private static final int EXPLORED = 1;
    private static final int OBSTACLE = 2;
    private static final int VIRTUAL_WALL = 3;

    public Grid() {
        this.rows = ROWS;
        this.cols = COLS;
        this.grid = new int[rows][cols];
    }

    public Grid(int rows, int cols, int[][] grid) {
        this.rows = rows;
        this.cols = cols;
        this.grid = grid;
    }

    public static Grid loadGridFromFile(String fileName) {
        int[][] grid = GridDescriptor.loadGrid(fileName);
        return new Grid(ROWS, COLS, grid);
    }

    public boolean isValid(int x, int y) {
        return x >= 0 && x < this.rows && y >= 0 && y < this.cols;
    }

    public boolean setObstacle(int x, int y) {
        if (isValid(x, y)) {
            this.grid[x][y] = OBSTACLE;
            return true;
        }
        return false;
    }

    public boolean isObstacle(int x, int y) {
        if (isValid(x, y)) {
            return this.grid[x][y] == OBSTACLE;
        }
        return false;
    }

    public boolean setExplored(int x, int y) {
        if (isValid(x, y)) {
            this.grid[x][y] = EXPLORED;
            return true;
        }
        return false;
    }

    public boolean isExplored(int x, int y) {
        if (isValid(x, y)) {
            return this.grid[x][y] == EXPLORED;
        }
        return false;
    }

    public boolean inStartZone(int x, int y) {
        return x >= 0 && x <= 2 && y >= 0 && y <= 2;
    }

    public boolean inGoalZone(int row, int col) {
        return (row <= ROWS + 1 && row >= ROWS - 1 && col <= COLS + 1 && col >= COLS - 1);
    }


}
