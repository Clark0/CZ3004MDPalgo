package mdpalgo.algorithm;

import mdpalgo.constants.Direction;
import mdpalgo.constants.Movement;
import mdpalgo.models.Grid;
import mdpalgo.models.Robot;
import mdpalgo.simulator.Arena;
import mdpalgo.simulator.Simulator;
import mdpalgo.utils.Connection;
import mdpalgo.utils.SendUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
    public GoalState goalState;

    private static final int MOVE_COST = 10;
    private static final int TURN_COST = 15;
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

        goalState = state -> state.row == this.goalRow && state.col == this.goalCol;
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

    private void moveRobot(Movement m, int step) {
        this.robot.move(m, step);
        if (Simulator.testAndroid) {
            SendUtil.sendRobotPos(robot);
        }
        if (Simulator.testRobot) {
            SendUtil.sendMoveRobotCommand(m, step);
            Connection.getConnection().receiveMessage();
            if (robot.canCalibrateFront(currentGrid)) {
                SendUtil.sendCalibrateFront();
            } else if (robot.canCalibrateRight(currentGrid)) {
                SendUtil.sendCalibrateRight();
            }
        }

    }

    private boolean reachable(int row, int col) {
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (!currentGrid.isValid(i, j)
                        || currentGrid.isObstacle(i, j)
                        || !currentGrid.isExplored(i ,j)) {
                    return false;
                }
            }
        }

        return true;
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

    public List<State> findFastestPath() {
        return findFastestPath(this.goalState);
    }

    public List<State> findFastestPath(GoalState goalState) {
        while (!pq.isEmpty()) {
            State currentState = pq.poll();
            if (goalState.isGoalState(currentState)) {
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
        List<State> path = this.findFastestPath(this.goalState);
        if (path == null) {
            System.out.println("Unable to find the fastest path");
        }
        executePath(path);
    }

    public void executePath(List<State> path) {
        if (this.arena != null) {
            arena.setPath(path);
        }
        refreshArena();
        int forwardCount = 0;
        for (Iterator<State> iter = path.iterator(); iter.hasNext();) {
            State state = iter.next();
            Direction direction = state.direction;
            // Arduino can only take one digit
            Movement movement = Direction.getMovementByDirections(robot.getDirection(), direction);
            if (movement == Movement.FORWARD) {
                forwardCount += 1;
                if (forwardCount < 15 && iter.hasNext()) {
                    continue;
                }
            }

            moveRobot(Movement.FORWARD, forwardCount);
            forwardCount = 0;
            refreshArena();

            if (movement != Movement.FORWARD) {
                // Make a turn
                moveRobot(movement, 0);
                refreshArena();

                forwardCount = 1;
                if (!iter.hasNext()) {
                    moveRobot(Movement.FORWARD, 1);
                    refreshArena();
                }
            }
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

    public void setArena(Arena arena) {
        this.arena = arena;
    }
}
