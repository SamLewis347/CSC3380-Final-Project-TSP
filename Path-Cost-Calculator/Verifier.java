import java.util.*;
import java.io.*;
//checks if given path and weight is valid

public class Verifier {

    public static void main(String[] args) {
        int[][] graph;
        try {
            File graphFile = new File("input");
            Scanner graphReader = new Scanner(graphFile);
            int graphs = graphReader.nextInt();
            for(int graphAt = 0; graphAt < graphs; graphAt++){
                int size = graphReader.nextInt();
                graph = new int[size][size];
                for(int i = 0; i < size; i++){
                    for(int j = 0; j < size; j++){
                        graph[i][j] = graphReader.nextInt();
                    }
                }
                for(int i = 0; i < size; i++){
                    for(int j = 0; j < size; j++){
                        System.out.print(graph[i][j] + " ");
                    }
                    System.out.println("");
                }
            }
            
        }
        catch (FileNotFoundException e){
            System.out.println("File not Found");
            e.printStackTrace();
        }
        
        
    }
    
}
