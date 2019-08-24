package utils;

import models.Grid;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class GridDescriptor {
    private static final String MAPS_DIR = "maps/";
    public static int[][] loadGrid(String filename) {
        int[][] grid = new int[Grid.ROWS][Grid.COLS];
        try (BufferedReader br =
                     new BufferedReader(new FileReader(MAPS_DIR + filename + ".txt"))){

            String line = br.readLine();
            StringBuilder sb = new StringBuilder();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }

            String s = sb.toString();
            for (int i = 0; i < Grid.ROWS; i++) {
                for (int j = 0; j < Grid.COLS; j++) {
                    grid[i][j] = Character.getNumericValue(s.charAt(i * Grid.COLS + j));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return grid;
    }
}
