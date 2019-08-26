package mdpalgo.utils;

import mdpalgo.models.Grid;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class GridDescriptor {
    private static final String MAPS_DIR = "maps/";
    public static int[][] loadGrid(String filename) {
        int[][] grid = new int[Grid.ROWS][Grid.COLS];
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try {
            Path path = Paths.get(classLoader.getResource(MAPS_DIR + filename + ".txt").toURI());
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
//            System.out.println(lines.get(0) + "\n" + lines.get(1) + "\n" + lines.get(2));
            for (int i = 0; i < Grid.ROWS; i++) {
                for (int j = 0; j < Grid.COLS; j++) {
                    grid[i][j] = Character.getNumericValue(lines.get(i).charAt(j));
                }
            }
        } catch (URISyntaxException | IOException e) {
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
