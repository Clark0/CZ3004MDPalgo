package mdpalgo.models;

import mdpalgo.utils.GridDescriptor;


public class Grid {
    private int rows;
    private int cols;
    private int[][] grid;
    private boolean[][] visitedCells;
    private int[][] confidence;
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
        this.confidence = new int[rows][cols];
        this.exploredCount = 0;
    }

    public static Grid initCurrentGrid(Robot robot) {
        Grid grid = new Grid();
        grid.setExploredWithCenter(robot.getPosRow(), robot.getPosCol());
        grid.setExploredWithCenter(GOAL_ROW, GOAL_COL);
        grid.setExploredWithCenter(START_ROW, START_COL);

        grid.setVisited(robot);
        grid.setVisited(Grid.START_ROW, Grid.START_COL);
        grid.setVisited(Grid.GOAL_ROW, Grid.GOAL_COL);
        return grid;
    }

    public static Grid loadGridFromFile(String fileName) {
        Grid grid = GridDescriptor.loadGrid(fileName);
        return grid;
    }

    public boolean isValid(int x, int y) {
        return x >= 0 && x < ROWS && y >= 0 && y < COLS;
    }

    public boolean isValid(int[] pos) {
        return isValid(pos[0], pos[1]);
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
    }

    public boolean isWall(int row, int col) {
        return row == -1 || row == Grid.ROWS || col == -1 || col == Grid.COLS;
    }

    public boolean isWallOrObstacle(int row, int col) {
        return isWall(row, col)
                || (isValid(row, col) && isObstacle(row, col));
    }

    public boolean isWallOrObstacle(int[] pos) {
        return isWallOrObstacle(pos[0], pos[1]);
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

    public void setExploredWithCenter(int row, int col) {
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (isValid(i, j)) {
                    setExplored(i, j);
                }
            }
        }
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

    public boolean isUnknown(int[] pos) {
        return isUnknown(pos[0], pos[1]);
    }

    public boolean isValidAndUnknown(int[] pos) {
        return isValid(pos) && isUnknown(pos);
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

        setVisited(row, col);
    }

    public void setVisited(int row, int col) {
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                this.visitedCells[i][j] = true;
            }
        }
    }

    public boolean isVisited(int row, int col) {
        return this.visitedCells[row][col];
    }

    public int getCellConfidence(int row, int col) {
        return this.confidence[row][col];
    }

    public void setCellConfidence(int row, int col, int value) {
        this.confidence[row][col] = value;
    }

    public void updateCellConfidence(int row, int col, int update) {
        // this.confidence[row][col] = this.confidence[row][col] + update > 0 ? this.confidence[row][col] + update : 0;
        this.confidence[row][col] = this.confidence[row][col] + update;
    }
}
