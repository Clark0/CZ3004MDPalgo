package mdpalgo;

import mdpalgo.algorithm.Exploration;
import mdpalgo.constants.Direction;
import mdpalgo.models.Grid;
import mdpalgo.models.Robot;

import static mdpalgo.utils.ArenaPrintUtil.refreshArena;

public class Main {

    public static void main(String[] args) {
        Grid realGrid = Grid.loadGridFromFile("map1");
        Grid currentGrid = Grid.initCurrentGrid();
        Robot robot = new Robot(Grid.START_ROW, Grid.START_COL, Direction.NORTH);
        refreshArena(realGrid, robot);

        int timeLimit = 60;
        Exploration exploration = new Exploration(currentGrid, realGrid, robot, timeLimit);
        exploration.explore();
    }
}
