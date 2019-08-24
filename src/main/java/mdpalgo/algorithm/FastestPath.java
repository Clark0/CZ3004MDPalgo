package mdpalgo.algorithm;

import mdpalgo.constants.Direction;
import mdpalgo.models.Grid;
import mdpalgo.models.Robot;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;

import static mdpalgo.utils.ArenaPrintUtil.*;


public class FastestPath {
    public class State implements Comparable<State> {
        State parent = null;
        int row;
        int col;
        double heuristic;
        double cost;
        Direction direction;

        State(int row, int col, Direction direction) {
            this.row = row;
            this.col = col;
            this.cost = 0;
            this.heuristic = calculateHeuristic(row, col);
            this.direction = direction;
        }

        State(int row, int col, State parent) {
            this.row = row;
            this.col = col;
            this.heuristic = calculateHeuristic(row, col);
            this.direction = Direction.getDirectionByDelta(row - parent.row, col - parent.col);
            this.cost = calculateCost(this.direction, parent);
            this.parent = parent;
        }

        @Override
        public int compareTo(State o) {
            return Double.compare(this.cost + this.heuristic, o.cost + this.heuristic);
        }
    }

    private Robot robot;
    private Grid currentGrid;
    private Grid realGrid;
    private final int goalRow;
    private final int goalCol;
    private boolean[][] visited;
    PriorityQueue<State> pq;

    private static final int MOVE_COST = 10;
    private static final int TURN_COST = 10;
    private static final int[] neighbourX = new int[]{1, 0, -1, 0};
    private static final int[] neighbourY = new int[]{0, 1, 0, -1};

    public FastestPath(Grid currentGrid, Grid realGrid, Robot robot, int goalRow, int goalCol) {
        this.goalRow = goalRow;
        this.goalCol = goalCol;
        this.currentGrid = currentGrid;
        this.realGrid = realGrid;
        this.robot = robot;
        visited = new boolean[Grid.ROWS][Grid.COLS];
        pq = new PriorityQueue<>();

        State initState = new State(robot.getPosRow(), robot.getPosCol(), robot.getDirection());
        setVisited(robot.getPosRow(), robot.getPosCol());
        pq.offer(initState);
    }

    public FastestPath(Grid currentGrid, Grid realGrid, Robot robot) {
        this(currentGrid, realGrid, robot, Grid.GOAL_ROW, Grid.GOAL_COL);
    }

    private double calculateHeuristic(int x, int y) {
        double moveCost = (Math.abs(this.goalRow - x) + Math.abs(this.goalCol - y)) * MOVE_COST;
        double turnCost = x == this.goalRow || y == this.goalCol ?  0 : TURN_COST;
        return moveCost + turnCost;
    }

    private double calculateCost(Direction relativeDirection, State state) {
        int directionDiff = Direction.getDirectionDiff(relativeDirection, state.direction);
        return state.cost + directionDiff * TURN_COST + MOVE_COST;
    }

    private boolean isGoalState(State state) {
        return state.row == this.goalRow && state.col == this.goalCol;
    }

    private boolean reachable(int x, int y) {
        return currentGrid.isValid(x, y)
                && currentGrid.isExplored(x, y)
                && !currentGrid.isObstacle(x, y)
                && !currentGrid.isVirtualWall(x, y);
    }

    private boolean isVisited(int x, int y) {
        return visited[x][y];
    }

    private void setVisited(int x, int y) {
        visited[x][y] = true;
    }

    private List<State> getNeighbours(State state) {
        List<State> neighbours = new ArrayList<>();
        for (int i = 0; i < neighbourX.length; i++) {
            int x = state.row + neighbourX[i];
            int y = state.col + neighbourY[i];
            if (reachable(x, y)) {
                neighbours.add(new State(x, y, state));
            }
        }
        return neighbours;
    }

    public State findFastestPath() {
        while (!pq.isEmpty()) {
            State currentState = pq.poll();
            if (isGoalState(currentState)) {
                printFastestPath(constructPath(currentState));
                return currentState;
            }

            List<State> neighbours = getNeighbours(currentState);
            for (State neighbour : neighbours) {
                if (isVisited(neighbour.row, neighbour.col))
                    continue;
                setVisited(neighbour.row, neighbour.col);
                pq.offer(neighbour);
            }
        }

        return null;
    }

    public List<State> constructPath(State state) {
        Stack<State> stateStack = new Stack<>();
        while (state != null) {
            stateStack.push(state);
            state = state.parent;
        }
        return stateStack;
    }

    public void printFastestPath(List<State> states) {
        for (int i = 0; i < Grid.ROWS + 1; i++) {
            for (int j = 0; j < Grid.COLS + 1; j++) {
                if (i == 0 && j == 0) {
                    print(" ");
                } else if (i == 0) {
                    print(String.valueOf(j - 1));
                } else if (j == 0) {
                    print(String.valueOf(i - 1));
                } else {
                    boolean printed = false;
                    for (State state : states) {
                        if (state.row == i-1 && state.col == j-1) {
                            printDirection(state.direction);
                            printed = true;
                        }
                    }
                    if (!printed)
                        printCell(i-1, j-1, this.currentGrid);
                }
            }

            System.out.println();
        }

        System.out.println();
    }

    public static void main(String[] args) {
        Grid realGrid = Grid.loadGridFromFile("map1");
        Robot robot = new Robot(Grid.START_ROW, Grid.START_COL, Direction.NORTH);
        FastestPath fastestPath = new FastestPath(realGrid, realGrid, robot);
        State state = fastestPath.findFastestPath();
        System.out.println(state.row + " " + state.col);
    }
}
