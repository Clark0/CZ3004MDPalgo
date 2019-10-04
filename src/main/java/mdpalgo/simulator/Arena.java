package mdpalgo.simulator;

import mdpalgo.algorithm.FastestPath;
import mdpalgo.constants.Direction;
import mdpalgo.constants.GraphicsConstants;
import mdpalgo.models.Grid;
import mdpalgo.models.Robot;

import javax.swing.*;
import java.awt.*;

import static mdpalgo.models.Grid.ROWS;
import static mdpalgo.models.Grid.COLS;


public class Arena extends JPanel {
    private Grid grid;
    private Robot robot;
    private java.util.List<FastestPath.State> path;

    public Arena(Grid grid, Robot robot) {
        this.grid = grid;
        this.robot = robot;
        path = null;
    }

    public void update(Grid grid, Robot robot) {
        this.grid = grid;
        this.robot = robot;
        path = null;
    }

    private boolean onPath(int row, int col) {
        if (this.path == null)
            return false;
        for (FastestPath.State state : this.path) {
            if (state.row == row && state.col == col) {
                return true;
            }
        }
        return false;
    }

    public void setPath(java.util.List<FastestPath.State> path) {
        this.path = path;
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

                if (onPath(mapRow, mapCol))
                    cellColor = GraphicsConstants.C_PATH;
                else if (grid.inStartZone(mapRow, mapCol))
                    cellColor = GraphicsConstants.C_START;
                else if (!grid.isExplored(mapRow, mapCol))
                    cellColor = GraphicsConstants.C_UNEXPLORED;
                else if (grid.isObstacle(mapRow, mapCol))
                    cellColor = GraphicsConstants.C_OBSTACLE;
//              else if (grid.isVirtualWall(mapRow, mapCol))
//                  cellColor = GraphicsConstants.C_VIRTUAL_WALL;
                else if (grid.inGoalZone(mapRow, mapCol))
                    cellColor = GraphicsConstants.C_GOAL;
                else
                    cellColor = GraphicsConstants.C_FREE;

                g.setColor(cellColor);
                g.fillRect(_mapCells[mapRow][mapCol].cellX + GraphicsConstants.MAP_X_OFFSET, _mapCells[mapRow][mapCol].cellY, _mapCells[mapRow][mapCol].cellSize, _mapCells[mapRow][mapCol].cellSize);
                if (mapRow == 0) {
                    g.setColor(Color.black);
                    g.drawString(String.valueOf(mapCol), _mapCells[mapRow][mapCol].cellX + GraphicsConstants.MAP_X_OFFSET + 10, _mapCells[mapRow][mapCol].cellY + 40);
                }
                if (mapCol == 0) {
                    g.setColor(Color.black);
                    g.drawString(String.valueOf(mapRow), _mapCells[mapRow][mapCol].cellX + GraphicsConstants.MAP_X_OFFSET - 20, _mapCells[mapRow][mapCol].cellY + GraphicsConstants.CELL_SIZE - 10);
                }
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
            case NORTH:
                g.fillOval(c * GraphicsConstants.CELL_SIZE + 10 + GraphicsConstants.MAP_X_OFFSET, GraphicsConstants.MAP_H - r * GraphicsConstants.CELL_SIZE - 15, GraphicsConstants.ROBOT_DIR_W, GraphicsConstants.ROBOT_DIR_H);
                break;
            case EAST:
                g.fillOval(c * GraphicsConstants.CELL_SIZE + 35 + GraphicsConstants.MAP_X_OFFSET, GraphicsConstants.MAP_H - r * GraphicsConstants.CELL_SIZE + 10, GraphicsConstants.ROBOT_DIR_W, GraphicsConstants.ROBOT_DIR_H);
                break;
            case SOUTH:
                g.fillOval(c * GraphicsConstants.CELL_SIZE + 10 + GraphicsConstants.MAP_X_OFFSET, GraphicsConstants.MAP_H - r * GraphicsConstants.CELL_SIZE + 35, GraphicsConstants.ROBOT_DIR_W, GraphicsConstants.ROBOT_DIR_H);
                break;
            case WEST:
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

    public Grid getGrid() {
        return grid;
    }

    public Robot getRobot() {
        return robot;
    }
}
