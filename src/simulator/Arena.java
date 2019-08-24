package simulator;

import constants.Direction;
import constants.GraphicsConstants;
import models.Grid;
import models.Robot;

import javax.swing.*;
import java.awt.*;

public class Arena extends JPanel {
	
    private Grid[][] grid;
    private Robot robot;

    public Arena(Robot robot) {
    	
        this.robot = robot;
        grid = new Grid[Grid.ROWS][Grid.COLS];
        
        for(int i = 0; i < Grid.ROWS; i++) {
        	for(int j = 0; j < Grid.COLS; j++) {

                grid[i][j] = new Grid(i, j);
        		
        	}
        }
    }
    
    public boolean startZone(int row, int col) {
    	
        return row >= 0 && row <= 2 && col >= 0 && col <= 2;
        
    }

    public boolean goalZone(int row, int col) {
    	
        return (row <= Grid.GOAL_ROW + 1 && row >= Grid.GOAL_ROW - 1 && col <= Grid.GOAL_COL + 1 && col >= Grid.GOAL_COL - 1);
        
    }
    
    @Override
    public void paintComponent(Graphics g) {
    	
        DisplayGrid[][] mapGrid = new DisplayGrid[Grid.ROWS][Grid.COLS];
        
        for (int i = 0; i < Grid.ROWS; i++) {
            for (int j = 0; j < Grid.COLS; j++) {
            	
            	mapGrid[i][j] = new DisplayGrid(j * GraphicsConstants.CELL_SIZE, i * GraphicsConstants.CELL_SIZE, GraphicsConstants.CELL_SIZE);
                
            }
        }

        for (int row = 0; row < Grid.ROWS; row++) {
            for (int col = 0; col < Grid.COLS; col++) {
            	
                Color gridColor;

                if (startZone(row, col))
                	
                	gridColor = GraphicsConstants.START;
                
                else if (goalZone(row, col))
                
                	gridColor = GraphicsConstants.GOAL;
                
                else {
                    
                	if (!grid[row][col].isExplored())
                    
                		gridColor = GraphicsConstants.UNEXPLORED;
                    
                	else if (grid[row][col].isObstacle())
                        
                		gridColor = GraphicsConstants.OBSTACLE;
                    
                	else
                    
                		gridColor = GraphicsConstants.NO_OBSTACLE;
                
                }

                g.setColor(gridColor);
                g.fillRect(mapGrid[row][col].gridX + GraphicsConstants.MAP_X_OFFSET, mapGrid[row][col].gridY, mapGrid[row][col].gridSize, mapGrid[row][col].gridSize);

            }
        }

        // Paint the robot on-screen.
        g.setColor(GraphicsConstants.ROBOT);
        int r = robot.getPosRow();
        int c = robot.getPosCol();
        g.fillOval((c - 1) * GraphicsConstants.CELL_SIZE + GraphicsConstants.ROBOT_X_OFFSET + GraphicsConstants.MAP_X_OFFSET, GraphicsConstants.MAP_HEIGHT - (r * GraphicsConstants.CELL_SIZE + GraphicsConstants.ROBOT_Y_OFFSET), GraphicsConstants.ROBOT_WIDTH, GraphicsConstants.ROBOT_HEIGHT);

        // Paint the robot's direction indicator on-screen.
        g.setColor(GraphicsConstants.ROBOT_DIRECTION);
        Direction d = robot.getDirection();
        
        switch (d) {
            case North:
                g.fillOval(c * GraphicsConstants.CELL_SIZE + 10 + GraphicsConstants.MAP_X_OFFSET, GraphicsConstants.MAP_HEIGHT - r * GraphicsConstants.CELL_SIZE - 15, GraphicsConstants.ROBOT_DIR_W, GraphicsConstants.ROBOT_DIR_H);
                break;
            case East:
                g.fillOval(c * GraphicsConstants.CELL_SIZE + 35 + GraphicsConstants.MAP_X_OFFSET, GraphicsConstants.MAP_HEIGHT - r * GraphicsConstants.CELL_SIZE + 10, GraphicsConstants.ROBOT_DIR_W, GraphicsConstants.ROBOT_DIR_H);
                break;
            case Sorth:
                g.fillOval(c * GraphicsConstants.CELL_SIZE + 10 + GraphicsConstants.MAP_X_OFFSET, GraphicsConstants.MAP_HEIGHT - r * GraphicsConstants.CELL_SIZE + 35, GraphicsConstants.ROBOT_DIR_W, GraphicsConstants.ROBOT_DIR_H);
                break;
            case West:
                g.fillOval(c * GraphicsConstants.CELL_SIZE - 15 + GraphicsConstants.MAP_X_OFFSET, GraphicsConstants.MAP_HEIGHT - r * GraphicsConstants.CELL_SIZE + 10, GraphicsConstants.ROBOT_DIR_W, GraphicsConstants.ROBOT_DIR_H);
                break;
        }
    }

    private class DisplayGrid {
    	
        public final int gridX;
        public final int gridY;
        public final int gridSize;

        public DisplayGrid(int xCoor, int yCoor, int gridSize) {
        	
            this.gridX = xCoor + GraphicsConstants.CELL_LINE_SPACE;
            this.gridY = GraphicsConstants.MAP_HEIGHT - (yCoor - GraphicsConstants.CELL_LINE_SPACE);
            this.gridSize = gridSize - (GraphicsConstants.CELL_LINE_SPACE * 2);
            
        }
    }
    
    public void obstacleDetected(int row, int col, boolean obstacle) {
    	
        if (obstacle && (startZone(row, col) || goalZone(row, col)))
            return;

        grid[row][col].setObstacle(obstacle);
        
    }
    
    public void showEntireMapExplored() {
    	
        for(int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
            	
                grid[row][col].setExplored(true);
            
            }
        }
    }
}
