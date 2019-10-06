package mdpalgo.models;

import mdpalgo.utils.GridDescriptor;


public class Grid {
    private int rows;
    private int cols;
    private int[][] grid;
    private boolean[][] virtualWall;
    private boolean[][] visitedCells;
    private int exploredCount;

    public static final int ROWS = 20;
    public static final int COLS = 15;
    public static final int GRID_SIZE = ROWS * COLS;
    public static final int START_ROW = 1;
    public static final int START_COL = 1;

    public static final int GOAL_ROW = ROWS - 2;
    public static final int GOAL_COL = COLS - 2;

    private static final int UNKNOWN = 0;
    private static final int EXPLORED = 1;
    private static final int OBSTACLE = 2;

    public Grid() {
        this.rows = ROWS;
        this.cols = COLS;
        this.grid = new int[rows][cols];
        this.visitedCells = new boolean[rows][cols];
        this.exploredCount = 0;
        initVirtualWall();
    }

    public static Grid initCurrentGrid(Robot robot) {
        Grid grid = new Grid();
        for (int row = robot.getPosRow() - 1; row <= robot.getPosRow() + 1; row++) {
            for (int col = robot.getPosCol() - 1; col <= robot.getPosCol() + 1; col++) {
                grid.setExplored(row, col);
            }
        }
        return grid;
    }

    public static Grid loadGridFromFile(String fileName) {
        Grid grid = GridDescriptor.loadGrid(fileName);
        return grid;
    }

    public void initVirtualWall() {
        this.virtualWall = new boolean[ROWS][COLS];
        for (int i = 0; i < ROWS; i++) {
            this.setVirtualWall(i, 0);
            this.setVirtualWall(i, COLS - 1);
        }

        for (int j = 0; j < COLS; j++) {
            this.setVirtualWall(0, j);
            this.setVirtualWall(ROWS - 1, j);
        }
    }

    public boolean isValid(int x, int y) {
        return x >= 0 && x < ROWS && y >= 0 && y < COLS;
    }

    public boolean inStartZone(int x, int y) {
        return x >= START_ROW - 1 && x <= START_ROW + 1 && y >= START_ROW - 1 && y <= START_COL + 1;
    }

    public boolean inGoalZone(int row, int col) {
        return (row <= GOAL_ROW + 1 && row >= GOAL_ROW - 1 && col <= GOAL_COL + 1 && col >= GOAL_COL - 1);
    }

    public void setObstacle(int x, int y) {
        if (!isExplored(x, y)) {
            this.exploredCount++;
        }
        this.grid[x][y] = OBSTACLE;
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (isValid(i, j)) {
                    this.setVirtualWall(i, j);
                }
            }
        }
    }

    public boolean isWall(int row, int col) {
        return row == -1 || row == Grid.ROWS || col == -1 || col == Grid.COLS;
    }

    public boolean isWallOrObstable(int row, int col) {
        return isWall(row, col)
                || (isValid(row, col) && isObstacle(row, col));
    }

    public boolean isObstacle(int x, int y) {
        return this.grid[x][y] == OBSTACLE;
    }

    public void setExplored(int x, int y) {
        if (!isExplored(x, y)) {
            this.exploredCount++;
        }
        this.grid[x][y] = EXPLORED;
    }

    public void markExplored(int x, int y, boolean explored) {
        if (explored) {
            this.setExplored(x, y);
        } else {
          if (isExplored(x, y)) {
              this.exploredCount--;
          }
          this.grid[x][y] = UNKNOWN;
        }
    }

    public void markObstacle(int x, int y, boolean isObstacle) {
        if (isObstacle)
            setObstacle(x, y);
    }

    public double getCoverage() {
        return (double) this.countExplored() / Grid.GRID_SIZE;
    }

    public boolean isExplored(int x, int y) {
        return !isUnknown(x, y);
    }

    public int getCell(int i, int j) {
        return grid[i][j];
    }

    public void setCell(int i, int j, int value) {
        grid[i][j] = value;
    }

    public boolean isUnknown(int i, int j) {
        return grid[i][j] == UNKNOWN;
    }

    public boolean isVirtualWall(int i, int j) {
        return this.virtualWall[i][j];
    }

    public void setVirtualWall(int i, int j) {
        this.virtualWall[i][j] = true;
    }

    public int countExplored() {
        return this.exploredCount;
    }

    /**
     * mark all robot occupied cells as visited
     * to prevent phantom block updating
     * @param robot
     */
    public void setVisited(Robot robot) {
        int row = robot.getPosRow();
        int col = robot.getPosCol();

        for (int i = row - 1; row < row + 1; row++) {
            for (int j = col - 1; j < col + 1; col++) {
                this.visitedCells[i][j] = true;
            }
        }
    }

    public boolean isVisited(int row, int col) {
        return this.visitedCells[row][col];
    }
}
