package algorithm;

import constants.Direction;
import constants.Movement;
import models.Grid;
import models.Robot;

public class Exploration {
    private Grid currentGrid;
    private Grid realGrid;
    private Robot robot;

    private void moveRobot(Movement movement) {
        robot.move(movement);
    }

    private boolean lookRight() {
        Direction right = robot.getDirection().turnRight();
        return false;
    }

    private boolean lookForward() {
        return false;
    }

    private boolean free(Direction direction) {
        return false;
    }

    public static void main(String[] agrs) {
        Grid realGrid = Grid.loadGridFromFile("sample");
        Grid currentGrid = new Grid();
        Robot robot = new Robot(Grid.START_ROW-1000, Grid.START_COL-1000, Direction.Sorth);
        printArena(realGrid, robot);
        robot.setRobotPosition(Grid.START_ROW, Grid.START_COL);
        printArena(realGrid, robot);
        robot.move(Movement.LEFT, 0);
        robot.sense(currentGrid, realGrid);
        printArena(currentGrid, robot);

    }

    public static void printArena(Grid grid, Robot robot) {
        for (int j = 0; j < Grid.COLS + 1; j++) {
            for (int i = 0; i < Grid.ROWS + 1; i++) {
                if (i == 0 && j == 0) {
                    print(" ");
                } else if (i == 0) {
                    print(String.valueOf(j-1));
                } else if (j == 0) {
                    print(String.valueOf(i-1));
                } else {
                    printCell(i-1, j-1, robot, grid);
                }
            }

            System.out.println();
        }

        System.out.println();
    }

    public static boolean isRobotArea(int x, int y, Robot robot) {
        int[] deltaX = new int[]{-1, 0, 1, -1, 0, 1, -1, 0, 1};
        int[] deltaY = new int[]{1, 1, 1, 0, 0, 0, -1, -1, -1};
        for (int i = 0; i < deltaX.length; i++) {
            int x_ = robot.getPosRow() + deltaX[i];
            int y_ = robot.getPosCol() + deltaY[i];
            if (x == x_ && y == y_)
                return true;
        }
        return false;
    }

    public static void printCell(int x, int y, Robot robot, Grid  grid) {
        if (isRobotArea(x, y, robot)) {
            print("O");
        } else if (grid.isUnknown(x, y)) {
            print(" ");
        } else if (grid.isExplored(x, y)) {
            print("*");
        } else if (grid.isObstacle(x, y)) {
            print("X");
        }
    }

    public static void print(String s) {
        if (s.length() > 1) {
            System.out.print(s + " ");
        } else {
            System.out.print(s + "  ");
        }
    }
}