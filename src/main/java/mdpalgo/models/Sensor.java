package mdpalgo.models;

import mdpalgo.constants.CommConstants;
import mdpalgo.constants.Direction;
import mdpalgo.constants.Movement;
import mdpalgo.simulator.Simulator;
import mdpalgo.utils.Connection;
import mdpalgo.utils.SendUtil;

public class Sensor {
    private final String id;
    private int lowerRange;
    private int upperRange;
    private int iDirection;

    public Sensor(int lowerRange, int upperRange, String id) {
        this.lowerRange = lowerRange;
        this.upperRange = upperRange;
        this.id = id;
    }

    public int sense(int row, int col, Direction direction, Grid currentGrid, Grid realGrid) {
        // TODO
        if (lowerRange > 1) {
            for (int i = 1; i < this.lowerRange; i++) {
                int[] pos = direction.forward(row, col, i);
                if (!realGrid.isValid(pos[0], pos[1]) || realGrid.isObstacle(pos[0], pos[1])) {
                    return i;
                }
            }
        }
        for (int i = lowerRange; i <= upperRange; i++) {
            int[] position = direction.forward(row, col, i);
            int x = position[0];
            int y = position[1];
            int[] position2 = direction.forward(row, col, i-1);
            // out of range
            if (!realGrid.isValid(x, y)) {
                return i;
            }

            currentGrid.setExplored(x, y);
            if (realGrid.isObstacle(x, y)){
            	
            	
            	if (id == "SF" || id == "SFL" || id == "SFR") {
            		if (i == 3) {
	            		if (currentGrid.getImageObstacle(position2[0], position2[1], ((direction.ordinal() + 2) % 4)) != 1) {
	            			currentGrid.setImageObstacle(position[0], position[1], ((direction.ordinal() + 2) % 4), 1);
	            			currentGrid.setImageObstacle(position2[0], position2[1], ((direction.ordinal() + 2) % 4), 1);
		            	}   
            		}
            		if (Simulator.obsSide) {
		            	if (currentGrid.getImageObstacle(position2[0], position2[1], ((direction.ordinal() + 2) % 4)) != 1) {
	            			currentGrid.setImageObstacle(position[0], position[1], ((direction.ordinal() + 2) % 4), 1);
	            			currentGrid.setImageObstacle(position2[0], position2[1], ((direction.ordinal() + 2) % 4), 1);
		            	}  
            		}
            	}
            	
            	if (id == "LL") {
	            	if (currentGrid.getImageObstacle(position2[0], position2[1], ((direction.ordinal() + 2) % 4)) == 0) {
            			Simulator.sensorLeft = i;
            		
	            		if (i == 1) {
	            			if (!Simulator.obsLeft) {
	        	            	if (currentGrid.getImageObstacle(position2[0], position2[1], ((direction.ordinal() + 2) % 4)) == 0) {
		            				System.out.println("LL LEFT STICK "+position2[0]+","+ position2[1]+","+((direction.ordinal() + 2) % 4));
	            					System.out.println("LL " +((direction.ordinal() + 2) % 4));
	            					Simulator.frontLeftPos[0] = direction.forward(x, y, -1)[0];
	            					Simulator.frontLeftPos[1] = direction.forward(x, y, -1)[1];
			            			Direction newDirection = direction.turnLeft();			            	        
			            			Simulator.backLeftPos[0] = newDirection.forward(Simulator.frontLeftPos[0], Simulator.frontLeftPos[1], 2)[0];
			            			Simulator.backLeftPos[1] = newDirection.forward(Simulator.frontLeftPos[0], Simulator.frontLeftPos[1], 2)[1];   	
			            			Simulator.obsLeft = true;
	        	            	}
		            		}
	            			
	            		}
	            	}   		
            	}
            	
            	if (id == "SRC" || id == "SRL") {
	            	if (currentGrid.getImageObstacle(position2[0], position2[1], ((direction.ordinal() + 2) % 4)) == 0) {
	            		Simulator.sensorRight = i;
	            	}
            	}
            	
                currentGrid.setObstacle(x, y);
                return i;
            }
        }

        return -1;
    }

    public int sense(int[] pos, Direction direction, Grid currentGrid, Grid realGrid) {
        return sense(pos[0], pos[1], direction, currentGrid, realGrid);
    }
    
    public void senseReal(int[] pos, Direction direction, Grid currentGrid, int sensorVal) {
        // Update map according to sensor's value.
    	for (int i = this.lowerRange; i <= this.upperRange; i++) {
            int[] position = direction.forward(pos[0], pos[1], i);
            int x = position[0];
            int y = position[1];

            if (!currentGrid.isValid(x, y)) return;
            currentGrid.setExplored(x, y);

            if (sensorVal == i) {
                // obstacle position

            	switch (direction) {
	                case NORTH:
	                	iDirection = 0;
	                	break;
	                case EAST:
	                	iDirection = 1;
	                	break;
	                case SOUTH:
	                	iDirection = 2;
	                	break;
	                case WEST:
	                	iDirection = 3;
	                	break;
	            }
            	
            	if (id == "SF" && currentGrid.countImage() != 5) {
	            	if (currentGrid.getImageObstacle(x, y, iDirection) != 0 || currentGrid.getImageObstacle(x, y, iDirection) != 1) {
	            		if (sensorVal != 1 && sensorVal != 5) {
	            			takePhoto(currentGrid, x, y, iDirection, sensorVal);
	            		}
	            	}       		
            	}
            	if (id == "LL" && currentGrid.countImage() != 5) {
	            	if (currentGrid.getImageObstacle(x, y, iDirection) != 0 || currentGrid.getImageObstacle(x, y, iDirection) != 1) {
	            		if (sensorVal != 1 && sensorVal != 5) {
	                        SendUtil.sendMoveRobotCommand(Movement.LEFT, 0);
	            			takePhoto(currentGrid, x, y, iDirection, sensorVal);
	                        SendUtil.sendMoveRobotCommand(Movement.RIGHT, 0);
	            		}
	            	}       		
            	}
            	if (id == "SR" && currentGrid.countImage() != 5) {
	            	if (currentGrid.getImageObstacle(x, y, iDirection) != 0 || currentGrid.getImageObstacle(x, y, iDirection) != 1) {
	            		if (sensorVal != 1 && sensorVal != 5) {
	                        SendUtil.sendMoveRobotCommand(Movement.RIGHT, 0);
	            			takePhoto(currentGrid, x, y, iDirection, sensorVal);
	                        SendUtil.sendMoveRobotCommand(Movement.LEFT, 0);
	            		}
	            	}       		
            	}
                currentGrid.setObstacle(x, y);
                break;
            }
        }
    }
    	
    public void takePhoto(Grid grid, int x, int y, int z, int range) {  	
    	Connection connect = Connection.getConnection();
		connect.sendMessage(CommConstants.IMAGE, "img:" + x + "," + y + "," + range);

		System.out.println("img:" + x + "," + y + "," + range);
		
    	String msg = connect.receiveMessage();
        String[] msgArr = msg.split(":");
        String[] msgArr2 = msgArr[1].split(",");

        if (msgArr2[0].equals(-1)) {
        	grid.setImageObstacle(Integer.parseInt(msgArr2[1]), Integer.parseInt(msgArr2[2]), z, 0);
			System.out.println(msgArr2[0] + " " + msgArr2[1] + " " + msgArr2[2]);
        } else {
        	grid.setImageCount();
			for (int oDir = 0; oDir < 4; oDir++) {
				grid.setImageObstacle(Integer.parseInt(msgArr2[1]), Integer.parseInt(msgArr2[2]), oDir, 1);
			}
        }
    }
}
