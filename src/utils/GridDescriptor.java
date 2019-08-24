package utils;

import models.Grid;
import simulator.Arena;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class GridDescriptor {
    public static void loadGrid(Arena arena, String filename) {
    	
        try (BufferedReader br = new BufferedReader(new FileReader("maps/" + filename + ".txt"))){

            String line = br.readLine();
            StringBuilder sb = new StringBuilder();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }

            String s = sb.toString();
            int stringCount = 0;
            
            for (int i = Grid.ROWS - 1; i >= 0; i--) {
                for (int j = 0; j < Grid.COLS; j++) {
                	
    				if(s.charAt(stringCount) == '1')
    					arena.obstacleDetected(i, j, true);
    				
    				stringCount++;
    				
                }
            }
            
            arena.showEntireMapExplored();

        } catch (IOException e) {
        	
            e.printStackTrace();
        
        }
    }
}
