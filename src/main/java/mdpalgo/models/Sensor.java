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

            int confidence = mapOBStoConfidence(i);
            if (realGrid.isObstacle(x, y)){
            	
            	if (id == "SF" || id == "SFL" || id == "SFR") {
            		if (i == 3) {
	            		if (currentGrid.getImageObstacle(position[0], position[1], ((direction.ordinal() + 2) % 4)) != 1) {
	            			currentGrid.setImageObstacle(position[0], position[1], ((direction.ordinal() + 2) % 4), 1);
	            			currentGrid.setImageObstacle(position2[0], position2[1], ((direction.ordinal() + 2) % 4), 1);
		            	}   
            		}
            		if (Simulator.obsSide) {
		            	if (currentGrid.getImageObstacle(position[0], position[1], ((direction.ordinal() + 2) % 4)) != 1) {
	            			currentGrid.setImageObstacle(position[0], position[1], ((direction.ordinal() + 2) % 4), 1);
	            			currentGrid.setImageObstacle(position2[0], position2[1], ((direction.ordinal() + 2) % 4), 1);
		            	}  
            		}
            	}
            	
            	if (id == "LL") {
	            	if (currentGrid.getImageObstacle(position[0], position[1], ((direction.ordinal() + 2) % 4)) != 1) {
            			Simulator.sensorLeft = i;
            		
	            		if (i == 1) {
	            			if (!Simulator.obsLeft) {
	        	            	if (currentGrid.getImageObstacle(position[0], position[1], ((direction.ordinal() + 2) % 4)) == 0) {
	            					Simulator.frontLeftPos[0] = direction.forward(x, y, -1)[0];
	            					Simulator.frontLeftPos[1] = direction.forward(x, y, -1)[1];
			            			Direction newDirection = direction.turnLeft();			            	        
			            			Simulator.backLeftPos[0] = newDirection.forward(Simulator.frontLeftPos[0], Simulator.frontLeftPos[1], 2)[0];
			            			Simulator.backLeftPos[1] = newDirection.forward(Simulator.frontLeftPos[0], Simulator.frontLeftPos[1], 2)[1];
			            			Simulator.obsLeft = true;
	        	            	}
		            		}
	            		}
	            		
	            		if (i == 2) {
	            			if (!Simulator.obsLeft) {
	        	            	if (currentGrid.getImageObstacle(position[0], position[1], ((direction.ordinal() + 2) % 4)) == 0) {
	            					Simulator.frontLeftPos[0] = direction.forward(x, y, -1)[0];
	            					Simulator.frontLeftPos[1] = direction.forward(x, y, -1)[1];
			            			Direction newDirection = direction.turnLeft();			            	        
			            			Simulator.backLeftPos[0] = newDirection.forward(Simulator.frontLeftPos[0], Simulator.frontLeftPos[1], 2)[0];
			            			Simulator.backLeftPos[1] = newDirection.forward(Simulator.frontLeftPos[0], Simulator.frontLeftPos[1], 2)[1];
			            			Simulator.obsLeft = true;
			            			Simulator.sensorLong = true;
	        	            	}
		            		}
	            		}
	            	}   		
            	}
            	
            	if (id == "SRF" || id == "SR") {
	            	if (currentGrid.getImageObstacle(position[0], position[1], ((direction.ordinal() + 2) % 4)) != 1) {
	            		Simulator.sensorRight = i;
	            	}
            	}

                currentGrid.updateCellConfidence(x, y, confidence);
                if (currentGrid.getCellConfidence(x, y) > 0 && !currentGrid.isVisited(x, y)) {
                    currentGrid.setObstacle(x, y);
                }
                break;
            } else {
                currentGrid.updateCellConfidence(x, y, -confidence);
                if (currentGrid.getCellConfidence(x, y) <= 0) {
                    currentGrid.setExplored(x, y);
                }
            }
        }

        return -1;
    }

    public int sense(int[] pos, Direction direction, Grid currentGrid, Grid realGrid) {
        return sense(pos[0], pos[1], direction, currentGrid, realGrid);
    }
    
    public void senseReal(int[] pos, Direction direction, Grid currentGrid, int sensorVal) {
        if (lowerRange > 1) {
            for (int i = 1; i < this.lowerRange; i++) {
                int[] front = direction.forward(pos[0], pos[1], i);
                if (!currentGrid.isValid(front[0], front[1]) || currentGrid.isObstacle(front[0], front[1])) {
                    return;





                }
            }
        }

        // Update map according to sensor's value.
    	for (int i = this.lowerRange; i <= this.upperRange; i++) {
            int[] position = direction.forward(pos[0], pos[1], i);
            int x = position[0];
            int y = position[1];
            int[] position2 = direction.forward(pos[0], pos[1], i-1);

            if (!currentGrid.isValid(x, y))
                return;

            int confidence = mapOBStoConfidence(i);
            if (sensorVal == i) {
                // obstacle position
            	
            	if (Simulator.testImage) {
	            	if (id == "SF" || id == "SFL" || id == "SFR") {
	            		if (currentGrid.countImage() != 5) {
			            	if (currentGrid.getImageObstacle(x, y, ((direction.ordinal() + 2) % 4)) == 0 || currentGrid.getImageObstacle(x, y, ((direction.ordinal() + 2) % 4)) == 2) {
			            		if (i == 3 || Simulator.obsSide) {
			            			takePhoto(currentGrid, x, y, ((direction.ordinal() + 2) % 4), sensorVal);
			            		}
			            	}       
	            		}
	            	}
	            	if (id == "LL" && currentGrid.countImage() != 5) {
		            	if (currentGrid.getImageObstacle(x, y, ((direction.ordinal() + 2) % 4)) == 0 || currentGrid.getImageObstacle(x, y, ((direction.ordinal() + 2) % 4)) == 2) {
		            		Simulator.sensorLeft = i;
		            		
		            		if (i == 1) {
		            			if (!Simulator.obsLeft) {
		        	            	if (currentGrid.getImageObstacle(x, y, ((direction.ordinal() + 2) % 4)) == 0) {
		            					Simulator.frontLeftPos[0] = direction.forward(x, y, -1)[0];
		            					Simulator.frontLeftPos[1] = direction.forward(x, y, -1)[1];
				            			Direction newDirection = direction.turnLeft();			            	        
				            			Simulator.backLeftPos[0] = newDirection.forward(Simulator.frontLeftPos[0], Simulator.frontLeftPos[1], 2)[0];
				            			Simulator.backLeftPos[1] = newDirection.forward(Simulator.frontLeftPos[0], Simulator.frontLeftPos[1], 2)[1];
				            			Simulator.obsLeft = true;
		        	            	}
			            		}
		            		}
		            		
		            		if (i == 2) {
		            			if (!Simulator.obsLeft) {
		        	            	if (currentGrid.getImageObstacle(x, y, ((direction.ordinal() + 2) % 4)) == 0) {
		            					Simulator.frontLeftPos[0] = direction.forward(x, y, -1)[0];
		            					Simulator.frontLeftPos[1] = direction.forward(x, y, -1)[1];
				            			Direction newDirection = direction.turnLeft();			            	        
				            			Simulator.backLeftPos[0] = newDirection.forward(Simulator.frontLeftPos[0], Simulator.frontLeftPos[1], 2)[0];
				            			Simulator.backLeftPos[1] = newDirection.forward(Simulator.frontLeftPos[0], Simulator.frontLeftPos[1], 2)[1];
				            			Simulator.obsLeft = true;
				            			Simulator.sensorLong = true;
		        	            	}
			            		}
		            		}
		            	}       		
	            	}
	            	if (id == "SR" || id == "SRF") {
	            		if (currentGrid.countImage() != 5) {
			            	if (currentGrid.getImageObstacle(x, y, ((direction.ordinal() + 2) % 4)) == 0 || currentGrid.getImageObstacle(x, y, ((direction.ordinal() + 2) % 4)) == 2) {
				            	Simulator.sensorRight = i;
			            	}       
	            		}
	            	}
                }
                
                currentGrid.updateCellConfidence(x, y, confidence);
                if (currentGrid.getCellConfidence(x, y) > 0 && !currentGrid.isVisited(x, y)) {
                    currentGrid.setObstacle(x, y);
                    if (Simulator.testImage && this.id.equals("SF")) {
                        takePhoto(x, y, sensorVal, direction);
                    }
                }
                break;
            } else {
                currentGrid.updateCellConfidence(x, y, -confidence);
                if (currentGrid.getCellConfidence(x, y) <= 0) {
                    currentGrid.setExplored(x, y);
                }
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
        	grid.setImageObstacle(x, y, z, 4);
			System.out.println(msgArr2[0] + " " + msgArr2[1] + " " + msgArr2[2]);
        } else {
        	grid.setImageCount();
			for (int oDir = 0; oDir < 4; oDir++) {
				grid.setImageObstacle(x, y, oDir, 1);
			}
			System.out.println(msgArr2[0] + " " + msgArr2[1] + " " + msgArr2[2]);
        }
    }

    private int mapOBStoConfidence(int obsValue) {
        if (id.equals("LL")) {
            if (obsValue == 1)
                return 4;
            else if (obsValue == 2){
                return 2;
            } else if (obsValue == 3) {
                return 2;
            } else if (obsValue == 4) {
                return 2;
            } else if (obsValue == 5) {
                return 1;
            }
        } else {
            if (obsValue == 1) {
                return 7;
            } else if (obsValue == 2) {
                return 5;
            } else if (obsValue == 3) {
                return 2;
            }
        }
        System.out.println("Invalid obsValue");
        return 0;
    }
}
