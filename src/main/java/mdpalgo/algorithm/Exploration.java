package mdpalgo.algorithm;

import mdpalgo.constants.Movement;
import mdpalgo.models.Grid;
import mdpalgo.models.Robot;
import mdpalgo.simulator.Arena;
import mdpalgo.utils.SendUtil;


public class Exploration {
    private Grid currentGrid;
    private Grid realGrid;
    private Robot robot;
    private Movement preMovement;
    private int timeLimit;
    private int coverage;
    private Arena arena;

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
        while (currentGrid.countExplore() * 1.0 / Grid.GRID_SIZE < coverage / 100.0
                && System.currentTimeMillis() < endTime) {
            robot.sense(currentGrid, realGrid);
            SendUtil.sendGrid(currentGrid);
            // refreshArena(currentGrid, robot);
            nextMove();
            System.out.println("Area explored" + currentGrid.countExplore());
        }
        returnStart();
    }

    public void returnStart() {
        FastestPath returnStartPath = new FastestPath(currentGrid, robot, Grid.START_ROW, Grid.START_COL);
        returnStartPath.runFastestPath(arena);
    }

    private void moveRobot(Movement movement) {
        robot.move(movement);
        preMovement = movement;
        if (arena != null)
            arena.repaint();
        SendUtil.sendRobotPos(robot);
    }

    private void nextMove() {
        System.out.println("NEXT");
        if (robot.isSafeMovement(Movement.RIGHT, currentGrid)) {
            moveRobot(Movement.RIGHT);
            if (robot.isSafeMovement(Movement.FORWARD, currentGrid)) {
                moveRobot(Movement.FORWARD);
            }
        } else if (robot.isSafeMovement(Movement.FORWARD, currentGrid)) {
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
    }
}