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
    private Movement preMovement;
    private int turningCount = 0;
    private int calibrateCount = 0;
    private int timeLimit;
    private int coverage;
    private Arena arena;
    private boolean newStrategy = false;
    private boolean recoverWallFollow = false;

    public Exploration(Grid currentGrid, Grid realGrid, Robot robot, int timeLimit, int coverage) {
        this.currentGrid = currentGrid;
        this.realGrid = realGrid;
        this.robot = robot;
        this.preMovement = null;
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

        while (currentGrid.countExplored() * 1.0 / Grid.GRID_SIZE < coverage / 100.0
                && System.currentTimeMillis() + RobotConstant.ESTIMATED_RETURN_TIME < endTime) {

            if (Simulator.testAndroid) {
                SendUtil.sendGrid(currentGrid);
            }

            if (newStrategy) {
                nextMoveNew();
            } else if (recoverWallFollow){
                recoverWallFollow();
            } else {
            	nextMove();
            }
            
            if (!recoverWallFollow && currentGrid.inStartZone(robot.getPosRow(), robot.getPosCol())
                    && currentGrid.countExplored() != Grid.GRID_SIZE
            		&& currentGrid.countExplored() > (Grid.GRID_SIZE / 2)) {
                moveRobot(Movement.BACKWARD);
                newStrategy = !newStrategy;
        	}

            // System.out.println("Area explored : " + currentGrid.countExplored());
        }

        returnStart();
        realGrid = currentGrid;
    }

    public void returnStart() {
        FastestPath returnStartPath = new FastestPath(currentGrid, robot, Grid.START_ROW, Grid.START_COL);
        List<FastestPath.State> path = returnStartPath.findFastestPath();
        if (path == null || path.isEmpty()) {
            System.out.println("Unable to find the way home");
            return;
        }
        arena.setPath(path);
        refreshArena();
        for (FastestPath.State state : path) {
            Direction target = state.direction;
            Movement movement = Direction.getMovementByDirections(robot.getDirection(), target);
            moveRobot(movement);
            if (Simulator.testRobot) {
                // remove unwanted obs data
                Connection connection = Connection.getConnection();
                connection.receiveMessage();
            }

            if (movement != Movement.FORWARD) {
                moveRobot(Movement.FORWARD);
                // remove unwanted obs data
                if (Simulator.testRobot) {
                    Connection connection = Connection.getConnection();
                    connection.receiveMessage();
                }
            }
        }

        // Calibrate at the start zone
        if (robot.getPosRow() == Grid.START_ROW && robot.getPosCol() == Grid.START_COL) {
            Direction target = Direction.SOUTH;
            Movement movement = Direction.getMovementByDirections(robot.getDirection(), target);
            // rotate the robot to south
            if (movement != Movement.FORWARD) {
                moveRobot(movement);
            }
            if (Simulator.testRobot) {
                SendUtil.sendCalibrateFrontRight();
            }
            this.calibrateCount = 0;
            moveRobot(Movement.LEFT);
        }
    }

    private void refreshArena() {
        if (this.arena != null)
            arena.repaint();
    }

    private void moveRobot(Movement movement) {
        //if (Simulator.testRobot) {
            // check whether the robot can calibrate
            if (this.canCalibrateFrontRight(robot, currentGrid)) {
                this.calibrateCount = 0;
                System.out.println("Calibrate corner, robot: " + robot.getPosRow() + " " + robot.getPosCol());
                SendUtil.sendCalibrateFrontRight();
            } else {
                calibrateCount++;
                if (calibrateCount > 3) {
                    SendUtil.sendCalibrateRight();
                    calibrateCount = 0;
                }
            }

            SendUtil.sendMoveRobotCommand(movement, 1);
        //}

        robot.move(movement);
        robot.sense(currentGrid, realGrid);
        refreshArena();
        if (movement == Movement.BACKWARD) {
            SendUtil.sendCalibrateRight();
        }

        preMovement = movement;

        if (Simulator.testAndroid) {
            SendUtil.sendRobotPos(robot);
        }
    }

    private void nextMove() {
        if (robot.isSafeMovement(Movement.RIGHT, currentGrid)) {
            moveRobot(Movement.RIGHT);
            if (robot.isSafeMovement(Movement.FORWARD, currentGrid)) {
                moveRobot(Movement.FORWARD);
                turningCount += 1;
                if (turningCount >= 4) {
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
                moveRobot(Movement.BACKWARD);
            }
            turningCount = 0;
        }
    }

    private void nextMoveNew() {
    	if (robot.isSafeMovement(Movement.LEFT, currentGrid)) {
            moveRobot(Movement.LEFT);
            if (robot.isSafeMovement(Movement.FORWARD, currentGrid)) {
                moveRobot(Movement.FORWARD);
            }
        } else if (robot.isSafeMovement(Movement.FORWARD, currentGrid)) {
            moveRobot(Movement.FORWARD);
        } else if (robot.isSafeMovement(Movement.RIGHT , currentGrid)) {
    	    moveRobot(Movement.RIGHT);
    	    if (robot.isSafeMovement(Movement.FORWARD, currentGrid)) {
    	        moveRobot(Movement.FORWARD);
            }
    	} else {
    	    moveRobot(Movement.BACKWARD);
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

    public boolean canCalibrateFrontRight(Robot robot, Grid currentGrid) {
        int row = robot.getPosRow();
        int col = robot.getPosCol();

        Direction direction = robot.getDirection();
        // right side is wall or obs
        int[] pos = direction.getFrontRight(row, col);
        int[] rightFront = direction.turnRight().forward(pos[0], pos[1]);

        pos = direction.getRight(row, col);
        int[] right = direction.turnRight().forward(pos[0], pos[1]);

        // front side is wall or obs
        pos = direction.getFrontRight(row, col);
        int[] frontRight = direction.forward(pos[0], pos[1]);

        pos = direction.getFrontLeft(row, col);
        int[] frontLeft = direction.forward(pos[0], pos[1]);

        return currentGrid.isWallOrObstable(rightFront[0], rightFront[1])
                && currentGrid.isWallOrObstable(right[0], right[1])
                && currentGrid.isWallOrObstable(frontRight[0], frontRight[1])
                && currentGrid.isWallOrObstable(frontLeft[0], frontLeft[1]);
    }
}