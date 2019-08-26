package mdpalgo.algorithm;

import mdpalgo.constants.Movement;
import mdpalgo.models.Grid;
import mdpalgo.models.Robot;
import mdpalgo.simulator.Arena;

import static mdpalgo.utils.ArenaPrintUtil.refreshArena;

public class Exploration {
    private Grid currentGrid;
    private Grid realGrid;
    private Robot robot;
    private Movement preMovement;
    private int timeLimit;
    private Arena arena;

    public Exploration(Grid currentGrid, Grid realGrid, Robot robot, int timeLimit) {
        this.currentGrid = currentGrid;
        this.realGrid = realGrid;
        this.robot = robot;
        this.preMovement = null;
        this.timeLimit = timeLimit;
    }

    public Exploration(Arena arena, Grid realGrid, int timeLimit) {
        this.currentGrid = arena.getGrid();
        this.realGrid = realGrid;
        this.robot = arena.getRobot();
        this.preMovement = null;
        this.timeLimit = timeLimit;
        this.arena = arena;
    }

    public void explore() {
        long startTime = System.currentTimeMillis();
        long endTime = startTime + timeLimit * 1000;
        while (currentGrid.countExplored() < Grid.GRID_SIZE
                && System.currentTimeMillis() < endTime) {
            robot.sense(currentGrid, realGrid);
            refreshArena(currentGrid, robot);
            nextMove();
            System.out.println("Area explored" + currentGrid.countExplored());
        }
    }

    private void nextMove() {
        if (robot.isSafeMovement(Movement.RIGHT, currentGrid)) {
            robot.move(Movement.RIGHT);
            preMovement = Movement.RIGHT;
            if (robot.isSafeMovement(Movement.FORWARD, currentGrid)) {
                robot.move(Movement.FORWARD);
                preMovement = Movement.FORWARD;
            }
        } else if (robot.isSafeMovement(Movement.FORWARD, currentGrid)) {
            robot.move(Movement.FORWARD);
            preMovement = Movement.FORWARD;
        } else if (robot.isSafeMovement(Movement.LEFT, currentGrid)) {
            robot.move(Movement.LEFT);
            preMovement = Movement.LEFT;
            if (robot.isSafeMovement(Movement.FORWARD, currentGrid)) {
                robot.move(Movement.FORWARD);
                preMovement = Movement.FORWARD;
            }
        } else {
            robot.move(Movement.RIGHT, 0);
            robot.move(Movement.RIGHT, 0);
        }
        arena.repaint();
    }
}