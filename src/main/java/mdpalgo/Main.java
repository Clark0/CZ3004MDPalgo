package mdpalgo;

import mdpalgo.simulator.Simulator;
import mdpalgo.utils.Connection;

public class Main {

    public static void main(String[] args) {
        Simulator simulator = new Simulator();
        Simulator.testAndroid = false;
        Simulator.testRobot = true;
        Simulator.testImage = false;
        Connection.setLocalConnection(false);
        simulator.simulate();
    }
}
