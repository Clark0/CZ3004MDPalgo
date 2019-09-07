package mdpalgo.utils;

import mdpalgo.models.Grid;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class GridDescriptor {
    public static final char UNEXPLORED = '0';
    public static final char EXPLORED = '1';
    public static final char FREE = '0';
    public static final char OBSTACLE = '1';

    private static final String MAPS_DIR = "maps/";
    // should not call directly; virtual wall is not
    public static Grid loadGrid(String filename) {
        Grid grid = Grid.initCurrentGrid();
        try {
            InputStream inputStream = GridDescriptor.class.getClassLoader().getResourceAsStream(MAPS_DIR + filename + ".txt");
            List<String> lines = IOUtils.readLines(inputStream, StandardCharsets.UTF_8);
            for (int j = Grid.ROWS - 1; j >= 0; j--) {
                for (int i = 0; i < Grid.COLS; i++) {
                    char c = lines.get(j).charAt(i);
                    if (c == '0') {
                        grid.setExplored(Grid.ROWS - j - 1, i);
                    } else if (c == '1') {
                        grid.setObstacle(Grid.ROWS - j - 1, i);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return grid;
    }

    private static String binaryToHex(String s) {
        StringBuilder sb = new StringBuilder();
        int left = 0, right = 0;
        for (left = 0; left + 4 <= s.length(); left += 4) {
            right = left + 4;
            int bin = Integer.parseInt(s.substring(left, right), 2);
            sb.append(Integer.toHexString(bin));
        }

        return sb.toString();
    }

    private static String hexToBinary(String s) {
        StringBuilder sb = new StringBuilder();
        String paddings = "0000";
        for (int i = 0; i < s.length(); i += 1) {
            int hex = Integer.parseInt(String.valueOf(s.charAt(i)), 16);
            String binary = Integer.toBinaryString(hex);
            String padding = paddings.substring(0, paddings.length() - binary.length());
            sb.append(padding);
            sb.append(binary);
        }

        return sb.toString();
    }

    public static String serializeGrid(Grid grid) {
        StringBuilder exploredDescriptor = new StringBuilder();
        StringBuilder obstacleDescriptor = new StringBuilder();
        // padding
        exploredDescriptor.append(11);
        for (int i = 0; i < Grid.ROWS; i++) {
            for (int j = 0; j < Grid.COLS; j++) {
                if (grid.isUnknown(i, j)) {
                    // Unknown cell represented by 0
                    exploredDescriptor.append(UNEXPLORED);
                } else {
                    exploredDescriptor.append(EXPLORED);

                    // know cell represented by 1
                    char obstacleState = grid.isObstacle(i, j) ? OBSTACLE : FREE;
                    obstacleDescriptor.append(obstacleState);
                }
            }
        }

        // padding bit sequence
        exploredDescriptor.append(11);
        while (obstacleDescriptor.length() % 8 != 0) {
            obstacleDescriptor.append('0');
        }

        return binaryToHex(exploredDescriptor.toString()) + ';' + binaryToHex(obstacleDescriptor.toString());
    }

    private static Grid unserializeGrid(String serialized) {
        Grid grid = new Grid();
        String[] result = serialized.split(";");
        String exploredDescriptor = result[0];
        String obstacleDescriptor = result[1];

        // hex to bin string
        exploredDescriptor = hexToBinary(exploredDescriptor);
        obstacleDescriptor = hexToBinary(obstacleDescriptor);

        // remove padding
        exploredDescriptor = exploredDescriptor.substring(2, exploredDescriptor.length() - 2);


        int exploredIndex = 0, obstacleIndex = 0;
        for (int i = 0; i < Grid.ROWS; i++) {
            for (int j = 0; j < Grid.COLS; j++) {
                if (exploredDescriptor.charAt(exploredIndex++) == EXPLORED) {
                    if (obstacleDescriptor.charAt(obstacleIndex++) == OBSTACLE) {
                        grid.setObstacle(i, j);
                    } else {
                        grid.setExplored(i, j);
                    }
                }
                // the default grid cell state is unknown
            }
        }

        return grid;
    }

    public static void main(String[] args) {
        Grid grid = Grid.loadGridFromFile("week8");
        ArenaPrintUtil.printArena(grid);
        String serialized = GridDescriptor.serializeGrid(grid);
        Grid unserializedGrid = GridDescriptor.unserializeGrid(serialized);
        ArenaPrintUtil.printArena(unserializedGrid);
    }
}
