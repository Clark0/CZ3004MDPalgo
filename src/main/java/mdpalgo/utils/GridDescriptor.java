package mdpalgo.utils;

import mdpalgo.models.Grid;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class GridDescriptor {
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

    public static String serializeGrid(Grid grid) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Grid.ROWS; i++) {
            for (int j = 0; j < Grid.COLS; j++) {
                sb.append(grid.getCell(i, j));
            }
        }

        return sb.toString();
    }

    private static Grid unserializeGrid(String serialized) {
        Grid grid = Grid.initCurrentGrid();
        for (int i = Grid.ROWS - 1; i >= 0; i--) {
            for (int j = Grid.COLS - 1; j >= 0; j--) {
                grid.setCell(i, j, serialized.charAt(i * Grid.COLS + j));
            }
        }
        return grid;
    }

    public static void main(String[] args) {
        ArenaPrintUtil.printArena(Grid.loadGridFromFile("map1"));
    }
}
