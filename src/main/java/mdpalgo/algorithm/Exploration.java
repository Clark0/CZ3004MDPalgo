package mdpalgo.algorithm;

import mdpalgo.constants.Direction;
import mdpalgo.constants.Movement;
import mdpalgo.constants.RobotConstant;
import mdpalgo.models.Grid;
import mdpalgo.models.Robot;
import mdpalgo.simulator.Arena;
import mdpalgo.simulator.Simulator;
import mdpalgo.utils.Connection;
import mdpalgo.utils.SendUtil;

import java.util.List;


public class Exploration {
    private Grid currentGrid;
    private Grid realGrid;
    private Robot robot;
    private int turningCount = 0;
    private int calibrateCount = 0;
    private int timeLimit;
    private int coverage;
    private Arena arena;
    private boolean passedStartZone = false;
    private boolean recoverWallFollow = false;

    public Exploration(Grid currentGrid, Grid realGrid, Robot robot, int timeLimit, int coverage) {
        this.currentGrid = currentGrid;
        this.realGrid = realGrid;
        this.robot = robot;
        this.timeLimit = timeLimit;
        this.coverage = coverage;
    }

    public void explore(Arena arena) {
        long startTime = System.currentTimeMillis();
        long endTime = startTime + timeLimit;
        this.arena = arena;
        if (Simulator.testRobot) {
            SendUtil.sendSenseCommand();
        }

        robot.sense(currentGrid, realGrid);
        refreshArena();

        while (!passedStartZone && currentGrid.countExplored() * 1.0 / Grid.GRID_SIZE < coverage / 100.0
                && System.currentTimeMillis() + RobotConstant.ESTIMATED_RETURN_TIME < endTime) {


            if (this.recoverWallFollow) {
                recoverWallFollow();
            } else {
                nextMoveRightWall();
            }

            // Send map descriptor to Android
            if (Simulator.testAndroid) {
                SendUtil.sendGrid(currentGrid);
            }

            // Change to follow left wall
            if (!recoverWallFollow && robot.getPosRow() == Grid.START_ROW && robot.getPosCol() == Grid.START_COL) {
                passedStartZone = true;
            }

            // System.out.println("Area explored : " + currentGrid.countExplored());
        }

        if (passedStartZone) {
            while (currentGrid.countExplored() * 1.0 / Grid.GRID_SIZE < coverage / 100.0
                    && System.currentTimeMillis() + RobotConstant.ESTIMATED_RETURN_TIME < endTime) {
                FastestPath fastestPath = new FastestPath(currentGrid, robot);
                List<FastestPath.State> path = findNearestUnexplored(fastestPath);
                if (path != null) {
                    executePathSlow(path);
                } else {
                    break;
                }
            }
        }

        returnStartFast();

        // if FastestPath fails, follow right wall to start zone
        while (!currentGrid.inStartZone(robot.getPosRow(), robot.getPosCol())) {
            if (Simulator.testAndroid) {
                SendUtil.sendGrid(currentGrid);
            }

            nextMoveRightWall();
        }

        realGrid = currentGrid;
        System.out.println("Exploration Finished");
        calibrateAtStartZone();
    }

    public void returnStartSlow() {
        FastestPath returnStartPath = new FastestPath(currentGrid, robot, Grid.START_ROW, Grid.START_COL);
        List<FastestPath.State> path = returnStartPath.findFastestPath();
        if (path == null || path.isEmpty()) {
            System.out.println("Unable to find the way home");
            return;
        }
        executePathSlow(path);
    }

    public void returnStartFast() {
        FastestPath returnStartFast = new FastestPath(currentGrid, robot, Grid.START_ROW, Grid.START_COL);
        returnStartFast.runFastestPath(arena);
    }

    private void refreshArena() {
        if (this.arena != null)
            arena.repaint();
    }

    private void moveRobot(Movement movement) {
        moveRobot(movement, true);
    }

    private void moveRobot(Movement movement, boolean sense) {
        if (Simulator.testRobot) {
            doCalibrate();
            SendUtil.sendMoveRobotCommand(movement, 1);
        }

        robot.move(movement);
        currentGrid.setVisited(robot);
        if (sense) {
            robot.sense(currentGrid, realGrid);
        } else {
            if (Simulator.testRobot) {
                Connection.getConnection().receiveMessage();
            }
        }

        refreshArena();
        if (Simulator.testAndroid) {
            SendUtil.sendRobotPos(robot);
        }
    }

    private void nextMoveRightWall() {
        if (robot.isSafeMovement(Movement.RIGHT, currentGrid)) {
            moveRobot(Movement.RIGHT);
            if (robot.isSafeMovement(Movement.FORWARD, currentGrid)) {
                moveRobot(Movement.FORWARD);
                turningCount += 1;
                if (turningCount >= 6) {
                    recoverWallFollow = true;
                }
            }
        } else {
            if (robot.isSafeMovement(Movement.FORWARD, currentGrid)) {
                moveRobot(Movement.FORWARD);
            } else if (robot.isSafeMovement(Movement.LEFT, currentGrid)) {
                moveRobot(Movement.LEFT);
                if (robot.isSafeMovement(Movement.FORWARD, currentGrid)) {
                    moveRobot(Movement.FORWARD);
                }
            } else {
                moveRobot(Movement.RIGHT);
                moveRobot(Movement.RIGHT);
            }
            turningCount = 0;
        }
    }

    public void setCoverage(int coverage) {
        this.coverage = coverage;
    }

    public void recoverWallFollow() {
        if (robot.isSafeMovement(Movement.FORWARD, currentGrid)) {
            moveRobot(Movement.FORWARD);
        } else {
            moveRobot(Movement.LEFT);
            if (robot.isSafeMovement(Movement.FORWARD, currentGrid)) {
                moveRobot(Movement.FORWARD);
            }
            recoverWallFollow = false;
        }
    }

    /**
     * Check whether the robot is at the corner and
     * is able to do the corner calibration
     *
     * @param robot
     * @param currentGrid
     * @return
     */

    private boolean canCalibrateFront(Robot robot, Grid currentGrid) {
        int row = robot.getPosRow();
        int col = robot.getPosCol();

        Direction direction = robot.getDirection();
        // front side is wall or obs
        int[] pos = direction.getFrontRight(row, col);
        int[] frontRight = direction.forward(pos[0], pos[1]);

        pos = direction.getFrontLeft(row, col);
        int[] frontLeft = direction.forward(pos[0], pos[1]);

        return currentGrid.isWallOrObstable(frontRight[0], frontRight[1])
                && currentGrid.isWallOrObstable(frontLeft[0], frontLeft[1]);
    }

    private boolean canCalibrateRight(Robot robot, Grid currentGrid) {
        int row = robot.getPosRow();
        int col = robot.getPosCol();

        Direction direction = robot.getDirection();
        // right side is wall or obs
        int[] pos = direction.getFrontRight(row, col);
        int[] rightFront = direction.turnRight().forward(pos[0], pos[1]);

        pos = direction.getBackRight(row, col);
        int[] right = direction.turnRight().forward(pos[0], pos[1]);

        return currentGrid.isWallOrObstable(rightFront[0], rightFront[1])
                && currentGrid.isWallOrObstable(right[0], right[1]);
    }

    private boolean canCalibrateFrontRight(Robot robot, Grid currentGrid) {
        return canCalibrateRight(robot, currentGrid)
                && canCalibrateFront(robot, currentGrid);
    }

    private void doCalibrate() {
        if (canCalibrateFrontRight(robot, currentGrid) && robot.isSafeMovement(Movement.LEFT, currentGrid)) {
            this.calibrateCount = 0;
            SendUtil.sendCalibrateFrontRight();
        } else if (canCalibrateFront(robot, currentGrid)) {
            this.calibrateCount = 0;
            SendUtil.sendCalibrateFront();
        } else {
            calibrateCount++;
            if (calibrateCount > 3 && canCalibrateRight(robot, currentGrid)) {
                SendUtil.sendCalibrateRight();
                calibrateCount = 0;
            }
        }
    }

    private void calibrateAtStartZone() {
        if (robot.getPosRow() != Grid.START_ROW || robot.getPosCol() != Grid.START_COL) {
            return;
        }

        Direction target = Direction.SOUTH;
        Movement movement = Direction.getMovementByDirections(robot.getDirection(), target);
        // rotate the robot to south
        if (movement != Movement.FORWARD) {
            moveRobot(movement, false);
        }
        if (Simulator.testRobot) {
            SendUtil.sendCalibrateFrontRight();
        }
        this.calibrateCount = 0;
        moveRobot(Movement.LEFT, false);
    }

    private List<FastestPath.State> findNearestUnexplored(FastestPath fastestPath) {
        GoalState goalState = state -> {
            Direction direction = state.direction;
            // goal state is a free place
            for (int i = state.row - 1; i <= state.row + 1; i++) {
                for (int j = state.col - 1; j <= state.col + 1; j++) {
                    if (!currentGrid.isValid(i, j)
                            || !currentGrid.isExplored(i, j)
                            || currentGrid.isObstacle(i, j)) {
                        return false;
                    }
                }
            }

            int[] head = direction.forward(state.row, state.col);
            if (currentGrid.isValidAndUnknown(direction.getFrontRight(head[0], head[1]))
                    || currentGrid.isValidAndUnknown(direction.forward(head[0], head[1]))
                    || currentGrid.isValidAndUnknown(direction.getFrontLeft(head[0], head[1]))) {

                return true;
            }

            int[] headRight = direction.getRight(state.row, state.col);
            if (currentGrid.isValidAndUnknown(direction.turnRight().getFrontRight(headRight[0], headRight[1]))
                    || currentGrid.isValidAndUnknown(direction.turnRight().getFrontLeft(headRight[0], headRight[1]))) {
                return true;
            }

            int[] headLeft = direction.getLeft(state.row, state.col);
            if (currentGrid.isValidAndUnknown(direction.turnLeft().getFrontRight(headLeft[0], headLeft[1]))) {
                return true;
            }

            return false;
        };

        return fastestPath.findFastestPath(goalState);
    }

    private void executePathSlow(List<FastestPath.State> path) {
        arena.setPath(path);
        refreshArena();
        for (FastestPath.State state : path) {
            Direction target = state.direction;
            Movement movement = Direction.getMovementByDirections(robot.getDirection(), target);
            moveRobot(movement);

            if (movement != Movement.FORWARD) {
                moveRobot(Movement.FORWARD);
            }
        }
    }
}