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
    private int circleLoop = 0;

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
    
    public void exploreImage(Arena arena) {
        long startTime = System.currentTimeMillis();
        long endTime = startTime + timeLimit;
        this.arena = arena;
        while (System.currentTimeMillis() < endTime) {

            robot.sense(currentGrid, realGrid);
            if (Simulator.sensorLong) {
            	moveRobot(Movement.LEFT);
            	robot.photoRight(currentGrid, realGrid);
            	Simulator.sensorLong = false;
            	moveRobot(Movement.RIGHT);
            }

	    	if (robot.getFrontRight()[0] == Simulator.backLeftPos[0] && robot.getFrontRight()[1] == Simulator.backLeftPos[1] && circleLoop < 3) {
	    		Simulator.obsLeft = false;
        		circleLoop = 0;
            	moveRobot(Movement.BACKWARD);
            	Simulator.frontLeftPos[0] = 0;
            	Simulator.frontLeftPos[1] = 0;
            	Simulator.backLeftPos[0] = 0;
            	Simulator.backLeftPos[1] = 0;
	    	}
            
            if (Simulator.obsLeft) {
            	nextMoveLeft();
	            leftRightImage();
	        	if (robot.getFrontLeft()[0] == Simulator.frontLeftPos[0] && robot.getFrontLeft()[1] == Simulator.frontLeftPos[1]) {
                	Simulator.obsLeft = false;
            		circleLoop = 0;
                	Simulator.frontLeftPos[0] = 0;
                	Simulator.frontLeftPos[1] = 0;
                	Simulator.backLeftPos[0] = 0;
                	Simulator.backLeftPos[1] = 0;
	        	}	
            }
            else {
            	leftRightImage();
	            nextMove();
            }
        	System.out.println("Area explored : " + currentGrid.countExplored());
        }
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
            	robot.sense(currentGrid, realGrid);
                moveRobot(Movement.FORWARD);
                turningCount += 1;
                if (turningCount >= 4) {
                    recoverWallFollow = true;
                }
            }
        } else {
            if (robot.isSafeMovement(Movement.FORWARD, currentGrid)) {
            	robot.sense(currentGrid, realGrid);
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
    
    private void nextMoveLeft() {
    	if (robot.isStickLeft(Movement.LEFT, currentGrid)) {
        	if (robot.isSafeMovement(Movement.LEFT, currentGrid)) {
	            moveRobot(Movement.LEFT);
	        	if (robot.isSafeMovement(Movement.FORWARD, currentGrid)) {
	            	robot.sense(currentGrid, realGrid);
	                moveRobot(Movement.FORWARD);
	            	circleLoop++;
	            }
	        } else if (robot.isSafeMovement(Movement.FORWARD, currentGrid)) {
	        	moveRobot(Movement.FORWARD);
	        } else if (robot.isSafeMovement(Movement.RIGHT, currentGrid)) {
	        	if (robot.isExplored(Movement.FORWARD, currentGrid)) {
		            moveRobot(Movement.RIGHT);
		        	if (robot.isSafeMovement(Movement.FORWARD, currentGrid)) {
		            	robot.sense(currentGrid, realGrid);
		                moveRobot(Movement.FORWARD);
		            	circleLoop--;
		            }
	        	}
	        	else {
		            moveRobot(Movement.BACKWARD);
		    		Simulator.obsLeft = false;
	        	}
	        } else {
	            moveRobot(Movement.BACKWARD);
	            Simulator.obsLeft = false;
	        }
	    } else if (robot.isStickLeft(Movement.FORWARD, currentGrid)) {
	        if (robot.isSafeMovement(Movement.RIGHT, currentGrid)) {
	            moveRobot(Movement.RIGHT);
	            if (robot.isSafeMovement(Movement.FORWARD, currentGrid)) {
	            	robot.sense(currentGrid, realGrid);
	                moveRobot(Movement.FORWARD);
	            }
	        } else if (robot.isSafeMovement(Movement.FORWARD, currentGrid)) {
	            moveRobot(Movement.FORWARD);
            	robot.sense(currentGrid, realGrid);
	        } else if (robot.isSafeMovement(Movement.LEFT, currentGrid)) {
	            moveRobot(Movement.LEFT);
	            if (robot.isSafeMovement(Movement.FORWARD, currentGrid)) {
	            	robot.sense(currentGrid, realGrid);
	                moveRobot(Movement.FORWARD);
	            }
	        } else {
	            moveRobot(Movement.BACKWARD);
	            Simulator.obsLeft = false;
	        }
	    } else {
	        moveRobot(Movement.LEFT);
	        if (robot.isSafeMovement(Movement.FORWARD, currentGrid)) {
	            moveRobot(Movement.FORWARD);
	        }
	    }
    }
    
    public void leftRightImage() {
    	if (Simulator.sensorLeft) {
        	moveRobot(Movement.LEFT);
        	robot.photoRight(currentGrid, realGrid);
        	Simulator.sensorLeft = false;
        	moveRobot(Movement.RIGHT);
        }
        if (Simulator.sensorRight) {
    		moveRobot(Movement.RIGHT);
        	robot.photoRight(currentGrid, realGrid);
        	Simulator.sensorRight = false;
        	moveRobot(Movement.LEFT);
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