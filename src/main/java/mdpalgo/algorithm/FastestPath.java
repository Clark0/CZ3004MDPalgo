package mdpalgo.algorithm;

import mdpalgo.constants.Direction;
import mdpalgo.constants.Movement;
import mdpalgo.models.Grid;
import mdpalgo.models.Robot;
import mdpalgo.simulator.Arena;
import mdpalgo.simulator.Simulator;
import mdpalgo.utils.SendUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;


public class FastestPath {
    public class State implements Comparable<State> {
        public int row;
        public int col;
        public Direction direction;
        double heuristic;
        double cost;
        State parent = null;


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

    private Arena arena;
    private Robot robot;
    private Grid currentGrid;
    private final int goalRow;
    private final int goalCol;
    private boolean[][] visited;
    PriorityQueue<State> pq;

    private static final int MOVE_COST = 10;
    private static final int TURN_COST = 10;
    private static final int[] neighbourX = new int[]{1, 0, -1, 0};
    private static final int[] neighbourY = new int[]{0, 1, 0, -1};

    public FastestPath(Grid currentGrid, Robot robot, int goalRow, int goalCol) {
        this.goalRow = goalRow;
        this.goalCol = goalCol;
        this.currentGrid = currentGrid;
        this.robot = robot;
        visited = new boolean[Grid.ROWS][Grid.COLS];
        pq = new PriorityQueue<>();

        State initState = new State(robot.getPosRow(), robot.getPosCol(), robot.getDirection());
        setVisited(robot.getPosRow(), robot.getPosCol());
        pq.offer(initState);
    }

    public FastestPath(Grid currentGrid, Robot robot) {
        this(currentGrid, robot, Grid.GOAL_ROW, Grid.GOAL_COL);
    }

    private double calculateHeuristic(int x, int y) {
        double moveCost = (Math.abs(this.goalRow - x) + Math.abs(this.goalCol - y)) * MOVE_COST;
        double turnCost = x == this.goalRow || y == this.goalCol ?  0 : TURN_COST;
        return moveCost + turnCost;
    }

    private double calculateCost(Direction relativeDirection, State state) {
        int directionDiff = Math.abs(Direction.getDirectionDiff(relativeDirection, state.direction));
        return state.cost + directionDiff * TURN_COST + MOVE_COST;
    }

    private void moveRobot(Movement m) {
        this.robot.move(m, 1);
        if (Simulator.testAndroid) {
            SendUtil.sendRobotPos(robot);
        }
        if (Simulator.testRobot) {
            SendUtil.sendMoveRobotCommand(m, 1);
        }
    }

    private boolean isGoalState(State state) {
        return state.row == this.goalRow && state.col == this.goalCol;
    }

    private boolean reachable(int x, int y, Direction direction) {
        int[] right = direction.getRight(x, y);
        int[] left = direction.getLeft(x, y);
        return currentGrid.isValid(x, y)
                && currentGrid.isExplored(x, y)
                && !currentGrid.isObstacle(x, y)
                && !currentGrid.isVirtualWall(x, y)
                && currentGrid.isExplored(right[0], right[1])
                && !currentGrid.isObstacle(right[0], right[1])
                && currentGrid.isExplored(left[0], left[1])
                && !currentGrid.isObstacle(left[0], left[1]);

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
            Direction direction = Direction.getDirectionByDelta(neighbourX[i], neighbourY[i]);
            if (reachable(x, y, direction)) {
                neighbours.add(new State(x, y, state));
            }
        }
        return neighbours;
    }

    public List<State> findFastestPath() {
        while (!pq.isEmpty()) {
            State currentState = pq.poll();
            if (isGoalState(currentState)) {
                return constructPath(currentState);
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

    public void runFastestPath(Arena arena) {
        this.arena = arena;
        List<State> path = this.findFastestPath();
        if (path == null) {
            System.out.println("Unable to find the fastest path");
        }
        executePath(path);
    }

    public void executePath(List<State> path) {
        // FastestPath(path, currentGrid, robot);
        if (this.arena != null) {
            arena.setPath(path);
        }
        refreshArena();
        for (State state : path) {
            Direction direction = state.direction;
            Movement movement = Direction.getMovementByDirections(direction, robot.getDirection());
            // Move robot 1 step
            moveRobot(movement);
            // printFastestPath(path, currentGrid, robot);
            refreshArena();
        }
    }

    private List<State> constructPath(State state) {
        List<State> states = new LinkedList<>();
        while (state != null) {
            states.add(state);
            state = state.parent;
        }
        Collections.reverse(states);
        // remove the initial state
        states.remove(0);
        return states;
    }

    private void refreshArena() {
        if (this.arena != null)
            arena.repaint();
    }

    public static void main(String[] args) {
        Grid currentGrid = Grid.loadGridFromFile("map1");
        Robot robot = new Robot(Grid.START_ROW, Grid.START_COL, Direction.NORTH);
        FastestPath fastestPath = new FastestPath(currentGrid, robot, Grid.GOAL_ROW, Grid.GOAL_COL);
        fastestPath.runFastestPath(null);
    }
}
