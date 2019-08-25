package mdpalgo.utils;

import mdpalgo.algorithm.FastestPath;
import mdpalgo.constants.Direction;
import mdpalgo.models.Grid;
import mdpalgo.models.Robot;

import java.util.List;

public class ArenaPrintUtil {
    public static void refreshArena(Grid grid, Robot robot) {
        System.out.print('\u000C');
        printArena(grid, robot);
    }

    public static void printArena(Grid grid) {
        printArena(grid, null);
    }

    public static void printArena(Grid grid, Robot robot) {
        for (int i = 0; i < Grid.ROWS + 1; i++) {
            for (int j = 0; j < Grid.COLS + 1; j++) {
                if (i == 0 && j == 0) {
                    print(" ");
                } else if (i == 0) {
                    print(String.valueOf(j-1));
                } else if (j == 0) {
                    print(String.valueOf(i-1));
                } else {
                    if (robot == null)
                        printCell(i-1, j-1, grid);
                    else
                        printCell(i-1, j-1, robot, grid);
                }
            }

            System.out.println();
        }

        System.out.println();
    }

    public static void printFastestPath(List<FastestPath.State> states, Grid currentGrid) {
        printFastestPath(states, currentGrid, null);
    }


    public static void printFastestPath(List<FastestPath.State> states, Grid grid, Robot robot) {
        for (int i = 0; i < Grid.ROWS + 1; i++) {
            for (int j = 0; j < Grid.COLS + 1; j++) {
                if (i == 0 && j == 0) {
                    print(" ");
                } else if (i == 0) {
                    print(String.valueOf(j - 1));
                } else if (j == 0) {
                    print(String.valueOf(i - 1));
                } else {
                    if (isRobotArea(i-1, j-1, robot)) {
                        printCell(i-1, j-1, robot, grid);
                    } else {
                        boolean printed = false;
                        for (FastestPath.State state : states) {
                            if (state.row == i - 1 && state.col == j - 1) {
                                printDirection(state.direction);
                                printed = true;
                            }
                        }
                        if (!printed) {
                            printCell(i - 1, j - 1, robot, grid);
                        }
                    }
                }
            }

            System.out.println();
        }

        System.out.println();
    }

    public static boolean isRobotArea(int x, int y, Robot robot) {
        if (robot == null)
            return false;
        for (int i = robot.getPosRow() - 1; i <= robot.getPosRow()+1; i++) {
            for (int j = robot.getPosCol() - 1; j <= robot.getPosCol()+1; j++) {
                if (x == i && y == j) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void printCell(int x, int y, Robot robot, Grid grid) {
        if (isRobotArea(x, y, robot)) {
            int[][] head = robot.getHead();
            if (x == head[1][0] && y == head[1][1]) {
                print("+");
            } else {
                print("O");
            }
        } else {
            printCell(x, y, grid);
        }
    }

    public static void printCell(int x, int y, Grid grid) {
        if (grid.isUnknown(x, y)) {
            print(" ");
        } else if (grid.isObstacle(x, y)) {
            print("X");
        } else if (grid.isExplored(x, y)) {
            print("*");
        }
    }

    public static void print(String s) {
        if (s.length() > 1) {
            System.out.print(s + " ");
        } else {
            System.out.print(s + "  ");
        }
    }

    public static void printDirection(Direction direction) {
        switch (direction) {
            case NORTH:
                print(">");
                break;
            case SOUTH:
                print("<");
                break;
            case EAST:
                print("V");
                break;
            case WEST:
                print("^");
                break;
        }
    }
}
