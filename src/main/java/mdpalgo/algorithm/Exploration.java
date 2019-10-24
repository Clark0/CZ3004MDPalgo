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
    private int circleLoop = 0;

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
        	
            if (Simulator.sensorRight) {
            	robot.imgReg(Movement.RIGHT, currentGrid);
        		if (robot.imagePossible(Movement.LEFT, currentGrid, 1)) {
        			moveRobot(Movement.RIGHT);
        			moveRobot(Movement.RIGHT);
                	Simulator.obsSide = true;
	                robot.sense(currentGrid, realGrid);
	                Simulator.sensorRight = false;
                	Simulator.obsSide = false;
	            	moveRobot(Movement.LEFT);
	            	moveRobot(Movement.LEFT);
    			}
                Simulator.sensorRight = false;
            }

            if (Simulator.sensorFront) {
            	robot.imgReg(Movement.FORWARD, currentGrid);
        		if (robot.imagePossible(Movement.BACKWARD, currentGrid, 1)) {
        			moveRobot(Movement.RIGHT);
                	Simulator.obsSide = true;
	                robot.sense(currentGrid, realGrid);
	                Simulator.sensorFront = false;
                	Simulator.obsSide = false;
	            	moveRobot(Movement.LEFT);
    			}
                Simulator.sensorFront = false;
            }


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
    
    public void exploreImage(Arena arena) {
        long startTime = System.currentTimeMillis();
        long endTime = startTime + timeLimit;
        this.arena = arena;
        while (System.currentTimeMillis() < endTime) {

            robot.sense(currentGrid, realGrid);
            refreshArena();
            if (Simulator.sensorFront) {
            	robot.imgReg(Movement.FORWARD, currentGrid);
        		if (robot.imagePossible(Movement.BACKWARD, currentGrid, 1)) {
        			moveRobot(Movement.RIGHT);
                	Simulator.obsSide = true;
	                robot.sense(currentGrid, realGrid);
	                Simulator.sensorFront = false;
                	Simulator.obsSide = false;
	            	moveRobot(Movement.LEFT);
	            	//Need cali?
    			}
            }
            if (Simulator.sensorRight) {
            	robot.imgReg(Movement.RIGHT, currentGrid);
        		if (robot.imagePossible(Movement.LEFT, currentGrid, 1)) {
        			moveRobot(Movement.RIGHT);
        			moveRobot(Movement.RIGHT);
                	Simulator.obsSide = true;
	                robot.sense(currentGrid, realGrid);
	                Simulator.sensorFront = false;
                	Simulator.obsSide = false;
	            	moveRobot(Movement.LEFT);
	            	moveRobot(Movement.LEFT);
	            	//Need cali?
    			}
            }
            /*System.out.println("robot "+robot.getPosRow() + "," + robot.getPosCol());
            System.out.println("robot "+robot.getFrontRight()[0] + "," + robot.getFrontRight()[1]);
            System.out.println("simi "+Simulator.backLeftPos[0] + "," + Simulator.backLeftPos[1]);
            System.out.println("circle "+circleLoop);
            if (robot.getFrontRight()[0] == Simulator.backLeftPos[0] && robot.getFrontRight()[1] == Simulator.backLeftPos[1] && circleLoop < 3) {
	    		Simulator.obsLeft = false;
        		circleLoop = 0;
            	moveRobot(Movement.BACKWARD);
            	Simulator.frontLeftPos[0] = 0;
            	Simulator.frontLeftPos[1] = 0;
            	Simulator.backLeftPos[0] = 0;
            	Simulator.backLeftPos[1] = 0;
            	if (Simulator.sensorLong) {
                    moveRobot(Movement.RIGHT);
                    moveRobot(Movement.FORWARD);
                    moveRobot(Movement.LEFT);
        			Simulator.sensorLong = false;
            	}
	    	}
            
            if (Simulator.obsLeft) {
            	leftRightImage();
            	nextMoveLeft();
	        	if (circleLoop == 4 && robot.getFrontLeft()[0] == Simulator.frontLeftPos[0] && robot.getFrontLeft()[1] == Simulator.frontLeftPos[1]) {
	        		Simulator.obsLeft = false;
            		circleLoop = 0;
                	Simulator.frontLeftPos[0] = 0;
                	Simulator.frontLeftPos[1] = 0;
                	Simulator.backLeftPos[0] = 0;
                	Simulator.backLeftPos[1] = 0;
                	if (Simulator.sensorLong) {
                        moveRobot(Movement.RIGHT);
                        moveRobot(Movement.FORWARD);
                        moveRobot(Movement.LEFT);
            			Simulator.sensorLong = false;
                	}
	        	}	
            }
            else {
            	leftRightImage();
            	nextMoveImage();
            }*/
        }
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
            	robot.sense(currentGrid, realGrid);
                moveRobot(Movement.FORWARD);
                turningCount += 1;
                if (turningCount >= 10) {
                    recoverWallFollow = true;
                    Direction targetDirection = currentGrid.countExplored() < Grid.GRID_SIZE / 2 ? Direction.NORTH : Direction.SOUTH;
                    Movement movement = Direction.getMovementByDirections(robot.getDirection(), targetDirection);
                    if (movement != Movement.FORWARD) {
                        moveRobot(movement);
                    }
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
                moveRobot(Movement.RIGHT);
                moveRobot(Movement.RIGHT);
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
	        	if (robot.checkImageObs(Movement.LEFT, currentGrid)) {
	        		if (robot.imagePossible(Movement.RIGHT, currentGrid, 1)) {
	        			moveRobot(Movement.RIGHT);
	    				moveRobot(Movement.FORWARD);
	                	moveRobot(Movement.BACKWARD);
	                	Simulator.obsSide = true;
		                robot.sense(currentGrid, realGrid);
	                	Simulator.obsSide = false;
		            	moveRobot(Movement.FORWARD);
		            	moveRobot(Movement.RIGHT);
	    			}
	        	}
	        	moveRobot(Movement.FORWARD);
	        } else if (robot.isSafeMovement(Movement.RIGHT, currentGrid)) {
	        	if (robot.isExplored(Movement.FORWARD, currentGrid)) {
		        	if (robot.checkImageObs(Movement.LEFT, currentGrid)) {
		        		if (robot.imagePossible(Movement.RIGHT, currentGrid, 1)) {
		        			moveRobot(Movement.RIGHT);
		    				moveRobot(Movement.FORWARD);
		                	moveRobot(Movement.BACKWARD);
		                	Simulator.obsSide = true;
			                robot.sense(currentGrid, realGrid);
		                	Simulator.obsSide = false;
			            	moveRobot(Movement.FORWARD);
			            	moveRobot(Movement.RIGHT);
		    			}
		        	}
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
	    } else {
	        moveRobot(Movement.LEFT);
	        if (robot.isSafeMovement(Movement.FORWARD, currentGrid)) {
	            moveRobot(Movement.FORWARD);
		        moveRobot(Movement.RIGHT);
            	robot.sense(currentGrid, realGrid);
	        }
	    }
    }
    
    private void nextMoveImage() {
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

	        	if (robot.isExplored(Movement.FORWARD, currentGrid)) {
		        	if (robot.checkImageObs(Movement.RIGHT, currentGrid)) {
		        		if (robot.imagePossible(Movement.LEFT, currentGrid, 1)) {
		        			moveRobot(Movement.LEFT);
		    				moveRobot(Movement.FORWARD);
		                	moveRobot(Movement.BACKWARD);
		                	Simulator.obsSide = true;
			                robot.sense(currentGrid, realGrid);
		                	Simulator.obsSide = false;
			            	moveRobot(Movement.FORWARD);
			            	moveRobot(Movement.LEFT);
		    			}
		        	}
	        	}
                moveRobot(Movement.FORWARD);
            } else if (robot.isSafeMovement(Movement.LEFT, currentGrid)) {
	        	if (robot.isExplored(Movement.FORWARD, currentGrid)) {
		        	if (robot.checkImageObs(Movement.RIGHT, currentGrid)) {
		        		if (robot.imagePossible(Movement.LEFT, currentGrid, 1)) {
		        			moveRobot(Movement.LEFT);
		    				moveRobot(Movement.FORWARD);
		                	moveRobot(Movement.BACKWARD);
		                	Simulator.obsSide = true;
			                robot.sense(currentGrid, realGrid);
		                	Simulator.obsSide = false;
			            	moveRobot(Movement.FORWARD);
			            	moveRobot(Movement.LEFT);
		    			}
		        	}
	        	}
                moveRobot(Movement.LEFT);
                if (robot.isSafeMovement(Movement.FORWARD, currentGrid)) {

                    robot.sense(currentGrid, realGrid);
                    moveRobot(Movement.FORWARD);
                }
            } else {
                moveRobot(Movement.BACKWARD);
            }
            turningCount = 0;
        }
    }
    /*
    public void leftRightImage() {
    	if (Simulator.sensorLeft != 0) { 
    		if (robot.isSideObstacle(Movement.RIGHT, currentGrid)) {
	    		if (Simulator.sensorLeft == 1) {
	    			moveRobot(Movement.RIGHT);
	        		robot.sense(currentGrid, realGrid);
	        		
	    			if (robot.imagePossible(Movement.FORWARD, currentGrid, Simulator.sensorLeft)) {
	    				moveRobot(Movement.FORWARD);
	                	moveRobot(Movement.BACKWARD);
	                	Simulator.obsSide = true;
		                robot.sense(currentGrid, realGrid);
	                	Simulator.obsSide = false;
		            	moveRobot(Movement.FORWARD);
		            	moveRobot(Movement.RIGHT);
		            	Simulator.sensorLeft = 0;
	    			}
	    			else {
	    				moveRobot(Movement.LEFT);
		            	Simulator.sensorLeft = 1;
	    				robot.noImage(Movement.LEFT, currentGrid, Simulator.sensorLeft);
		            	Simulator.sensorLeft = 0;
	    			}
	    		}
	    		if (Simulator.sensorLeft == 2) {
	    			moveRobot(Movement.RIGHT);
	        		robot.sense(currentGrid, realGrid);
	        		
	    			if (robot.imagePossible(Movement.FORWARD, currentGrid, Simulator.sensorLeft)) {
	                	moveRobot(Movement.BACKWARD);
	                	Simulator.obsSide = true;
		                robot.sense(currentGrid, realGrid);
	                	Simulator.obsSide = false;
		            	moveRobot(Movement.RIGHT);
		            	Simulator.sensorLeft = 0;
	    			}
	    			else {
	    				moveRobot(Movement.LEFT);
		            	Simulator.sensorLeft = 2;
	    				robot.noImage(Movement.LEFT, currentGrid, Simulator.sensorLeft);
		            	Simulator.sensorLeft = 0;
	    			}
	    		}
	    		if (Simulator.sensorLeft == 3) {
	            	moveRobot(Movement.LEFT);
	            	Simulator.obsSide = true;
	                robot.sense(currentGrid, realGrid);
	            	Simulator.obsSide = false;
	            	moveRobot(Movement.RIGHT);
	            	Simulator.sensorLeft = 0;
	    		}
    		} 
    		else {
				robot.noImage(Movement.LEFT, currentGrid, 0);
            	Simulator.sensorLeft = 0;
    		}
        }
        if (Simulator.sensorRight != 0) {
    		if (robot.isSideObstacle(Movement.LEFT, currentGrid)) {
	        	if (Simulator.sensorRight == 1) {
	    			moveRobot(Movement.LEFT);
	        		robot.sense(currentGrid, realGrid);
	        		
	    			if (robot.imagePossible(Movement.FORWARD, currentGrid, Simulator.sensorRight)) {
	    				moveRobot(Movement.FORWARD);
	                	moveRobot(Movement.BACKWARD);
	                	Simulator.obsSide = true;
		                robot.sense(currentGrid, realGrid);
	                	Simulator.obsSide = false;
		            	moveRobot(Movement.FORWARD);
		            	moveRobot(Movement.LEFT);
		            	Simulator.sensorRight = 0;
	    			}
	    			else {
	    				moveRobot(Movement.RIGHT);
		            	Simulator.sensorRight = 1;
	    				robot.noImage(Movement.RIGHT, currentGrid, Simulator.sensorRight);
		            	Simulator.sensorRight = 0;
	    			}
	    		}
	        	if (Simulator.sensorRight == 2) {
	    			moveRobot(Movement.LEFT);
	        		robot.sense(currentGrid, realGrid);
	        		
	    			if (robot.imagePossible(Movement.FORWARD, currentGrid, Simulator.sensorRight)) {
	    				moveRobot(Movement.BACKWARD);
	                	Simulator.obsSide = true;
		                robot.sense(currentGrid, realGrid);
	                	Simulator.obsSide = false;
		            	moveRobot(Movement.LEFT);
		            	Simulator.sensorRight = 0;
	    			}
	    			else {
	    				moveRobot(Movement.RIGHT);
		            	Simulator.sensorRight = 2;
	    				robot.noImage(Movement.RIGHT, currentGrid, Simulator.sensorRight);
		            	Simulator.sensorRight = 0;
	    			}
	    		}
	        	if (Simulator.sensorRight == 3) {
	            	moveRobot(Movement.LEFT);
	            	Simulator.obsSide = true;
	                robot.sense(currentGrid, realGrid);
	            	Simulator.obsSide = false;
	            	moveRobot(Movement.RIGHT);
	            	Simulator.sensorRight = 0;
	    		} 
    		}
    		else {
				robot.noImage(Movement.RIGHT, currentGrid, 0);
            	Simulator.sensorRight = 0;
    		}
        }
    }
*/
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


    private void doCalibrate() {
        if (robot.canCalibrateFrontRight(currentGrid) && robot.isSafeMovement(Movement.LEFT, currentGrid)) {
            this.calibrateCount = 0;
            SendUtil.sendCalibrateFrontRight();
        } else if (robot.canCalibrateFront(currentGrid)) {
            this.calibrateCount = 0;
            SendUtil.sendCalibrateFront();
        } else {
            calibrateCount++;
            if (calibrateCount >= 3 && robot.canCalibrateRight(currentGrid)) {
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
        if (movement != Movement.FORWARD && Simulator.testRobot) {
            moveRobot(movement, false);
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

            // Check front sensors
            int[] head = direction.forward(state.row, state.col);
            if (currentGrid.isValidAndUnknown(direction.getFrontRight(head[0], head[1]))
                    || currentGrid.isValidAndUnknown(direction.forward(head[0], head[1]))
                    || currentGrid.isValidAndUnknown(direction.getFrontLeft(head[0], head[1]))) {

                return true;
            }

            // Check right sensors
            int[] headRight = direction.getRight(state.row, state.col);
            if (currentGrid.isValidAndUnknown(direction.turnRight().getFrontRight(headRight[0], headRight[1]))
                    || currentGrid.isValidAndUnknown(direction.turnRight().getFrontLeft(headRight[0], headRight[1]))) {
                return true;
            }

            // Check left sensor
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