package simulator;

import constants.Direction;
import constants.GraphicsConstants;
import models.Grid;
import models.Robot;

import javax.swing.*;
import java.awt.*;

import static models.Grid.ROWS;
import static models.Grid.COLS;


public class Arena extends JPanel {
    private Grid grid;
    private Robot robot;

    public Arena(Grid grid, Robot robot) {
        this.grid = grid;
        this.robot = robot;
    }

    @Override
    public void paintComponent(Graphics g) {
        _DisplayCell[][] _mapCells = new _DisplayCell[ROWS][COLS];
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                _mapCells[i][j] = new _DisplayCell(j * GraphicsConstants.CELL_SIZE, i * GraphicsConstants.CELL_SIZE, GraphicsConstants.CELL_SIZE);
            }
        }

        for (int mapRow = 0; mapRow < ROWS; mapRow++) {
            for (int mapCol = 0; mapCol < COLS; mapCol++) {
                Color cellColor;

                if (grid.inStartZone(mapRow, mapCol))
                    cellColor = GraphicsConstants.C_START;
                else if (grid.inGoalZone(mapRow, mapCol))
                    cellColor = GraphicsConstants.C_GOAL;
                else {
                    if (!grid.isExplored(mapRow, mapCol))
                        cellColor = GraphicsConstants.C_UNEXPLORED;
                    else if (grid.isObstacle(mapRow, mapCol))
                        cellColor = GraphicsConstants.C_OBSTACLE;
                    else
                        cellColor = GraphicsConstants.C_FREE;
                }

                g.setColor(cellColor);
                g.fillRect(_mapCells[mapRow][mapCol].cellX + GraphicsConstants.MAP_X_OFFSET, _mapCells[mapRow][mapCol].cellY, _mapCells[mapRow][mapCol].cellSize, _mapCells[mapRow][mapCol].cellSize);

            }
        }

        // Paint the robot on-screen.
        g.setColor(GraphicsConstants.C_ROBOT);
        int r = robot.getPosRow();
        int c = robot.getPosCol();
        g.fillOval((c - 1) * GraphicsConstants.CELL_SIZE + GraphicsConstants.ROBOT_X_OFFSET + GraphicsConstants.MAP_X_OFFSET, GraphicsConstants.MAP_H - (r * GraphicsConstants.CELL_SIZE + GraphicsConstants.ROBOT_Y_OFFSET), GraphicsConstants.ROBOT_W, GraphicsConstants.ROBOT_H);

        // Paint the robot's direction indicator on-screen.
        g.setColor(GraphicsConstants.C_ROBOT_DIR);
        Direction d = robot.getDirection();
        switch (d) {
            case North:
                g.fillOval(c * GraphicsConstants.CELL_SIZE + 10 + GraphicsConstants.MAP_X_OFFSET, GraphicsConstants.MAP_H - r * GraphicsConstants.CELL_SIZE - 15, GraphicsConstants.ROBOT_DIR_W, GraphicsConstants.ROBOT_DIR_H);
                break;
            case East:
                g.fillOval(c * GraphicsConstants.CELL_SIZE + 35 + GraphicsConstants.MAP_X_OFFSET, GraphicsConstants.MAP_H - r * GraphicsConstants.CELL_SIZE + 10, GraphicsConstants.ROBOT_DIR_W, GraphicsConstants.ROBOT_DIR_H);
                break;
            case Sorth:
                g.fillOval(c * GraphicsConstants.CELL_SIZE + 10 + GraphicsConstants.MAP_X_OFFSET, GraphicsConstants.MAP_H - r * GraphicsConstants.CELL_SIZE + 35, GraphicsConstants.ROBOT_DIR_W, GraphicsConstants.ROBOT_DIR_H);
                break;
            case West:
                g.fillOval(c * GraphicsConstants.CELL_SIZE - 15 + GraphicsConstants.MAP_X_OFFSET, GraphicsConstants.MAP_H - r * GraphicsConstants.CELL_SIZE + 10, GraphicsConstants.ROBOT_DIR_W, GraphicsConstants.ROBOT_DIR_H);
                break;
        }
    }

    private class _DisplayCell {
        public final int cellX;
        public final int cellY;
        public final int cellSize;

        public _DisplayCell(int borderX, int borderY, int borderSize) {
            this.cellX = borderX + GraphicsConstants.CELL_LINE_WEIGHT;
            this.cellY = GraphicsConstants.MAP_H - (borderY - GraphicsConstants.CELL_LINE_WEIGHT);
            this.cellSize = borderSize - (GraphicsConstants.CELL_LINE_WEIGHT * 2);
        }
    }
}
