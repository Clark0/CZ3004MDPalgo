package mdpalgo;

import mdpalgo.algorithm.Exploration;
import mdpalgo.algorithm.FastestPath;
import mdpalgo.constants.Direction;
import mdpalgo.models.Grid;
import mdpalgo.models.Robot;
import mdpalgo.simulator.Simulator;
import mdpalgo.utils.Connection;

import static mdpalgo.utils.ArenaPrintUtil.refreshArena;

public class Main {

    public static void main(String[] args) {
        Connection connection = Connection.getConnection();
        connection.openConnection();
//        Grid realGrid = Grid.loadGridFromFile("map1");
//        Grid currentGrid = Grid.initCurrentGrid();
//        Robot robot = new Robot(Grid.START_ROW, Grid.START_COL, Direction.NORTH);
//        refreshArena(realGrid, robot);
//
//        int timeLimit = 60;
//        Exploration exploration = new Exploration(currentGrid, realGrid, robot, timeLimit);
//        exploration.explore();
//        FastestPath fastestPath = new FastestPath(currentGrid, realGrid, robot, Grid.START_ROW, Grid.START_COL);
//        fastestPath.runFastestPath();
//

        Simulator simulator = new Simulator();
        simulator.simulate();
        connection.closeConnection();


    }
}
