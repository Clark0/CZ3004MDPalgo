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
        Simulator simulator = new Simulator();
        simulator.simulate();
    }
}
