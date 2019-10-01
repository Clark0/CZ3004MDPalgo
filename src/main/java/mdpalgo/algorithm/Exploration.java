package mdpalgo.algorithm;

import mdpalgo.constants.Movement;
import mdpalgo.constants.RobotConstant;
import mdpalgo.models.Grid;
import mdpalgo.models.Robot;
import mdpalgo.simulator.Arena;
import mdpalgo.simulator.Simulator;
import mdpalgo.utils.SendUtil;


public class Exploration {
    private Grid currentGrid;
    private Grid realGrid;
    private Robot robot;
    private Movement preMovement;
    private int turningCount = 0;
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
        while (currentGrid.countExplored() * 1.0 / Grid.GRID_SIZE < coverage / 100.0
                && System.currentTimeMillis() + RobotConstant.ESTIMATED_RETURN_TIME < endTime) {
        	
            robot.sense(currentGrid, realGrid);
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

            System.out.println("Area explored : " + currentGrid.countExplored());
        }

        returnStart();
        realGrid = currentGrid;
    }

    public void returnStart() {
        FastestPath returnStartPath = new FastestPath(currentGrid, robot, Grid.START_ROW, Grid.START_COL);
        returnStartPath.runFastestPath(arena);
    }

    private void moveRobot(Movement movement) {
        robot.move(movement);
        if (Simulator.testRobot) {
            SendUtil.sendMoveRobotCommand(movement, 1);
        }
        if (Simulator.testAndroid) {
            SendUtil.sendRobotPos(robot);
        }

        preMovement = movement;
        if (arena != null)
            arena.repaint();
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
}