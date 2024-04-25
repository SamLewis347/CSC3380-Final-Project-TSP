import java.util.*;
import java.io.*;
//checks if given path and weight is valid
public class Verifier {

    public static void main(String[] args) {
        int[][] graph;
        boolean flag = true;
        ArrayList<Integer> incorrect = new ArrayList<>();
        try {
            //gets graph and answer file
            File graphFile = new File("graph.txt");
            File answerFile = new File("results.txt");
            Scanner graphReader = new Scanner(graphFile);
            Scanner answerReader = new Scanner(answerFile);
            int graphs = graphReader.nextInt();
            
            for(int graphAt = 0; graphAt < graphs; graphAt++){
                //reads in graph from file
                int size = graphReader.nextInt();
                graph = new int[size][size];
                for(int i = 0; i < size; i++){
                    for(int j = 0; j < size; j++){
                        graph[i][j] = graphReader.nextInt();
                    }
                }
                
                
                
                
                //reads in cycle and cost and makes sure that a cycle is valid
                boolean[] visited = new boolean[size];
                ArrayList<Integer> cycle = new ArrayList<>();
                int node;
                int cost;
                int costActual;
                for(int i = 0; i < size; i++){
                    //grabs next node in cycle
                    node = answerReader.nextInt();
                    //checks if node was already visited
                    //if so, the path is invalid
                    if(visited[node] == true){
                        flag = false;
                        incorrect.add(graphAt + 1);
                        continue;
                    }
                    visited[node] = true;
                    cycle.add(node);
                }
                //if number of nodes in cycle does not equal number of vertices, it is invalid
                if (cycle.size() != size && flag == true){
                    flag = false;
                    incorrect.add(graphAt + 1);
                    continue;
                }
                
                
                //reads in cost of path and checks if it is valid
                cost = answerReader.nextInt();
                costActual = 0;
                for(int i = 0; i < cycle.size(); i++){
                    int at = cycle.get(i);
                    int next;
                    if(i == size-1){
                        next = cycle.get(0);
                    }
                    else{
                        next = cycle.get(i+1);
                    }
                    costActual += graph[at][next];
                }
                
                if (costActual != cost){
                    incorrect.add(graphAt+1);
                }
            }
            if (incorrect.isEmpty()){
                System.out.println("All cases are valid!");
            }
            else{
                System.out.println("The following cases are invalid or incorrect:");
                for(int i = 0; i < incorrect.size(); i++){
                    System.out.print(incorrect.get(i) + " ");
                }
            }
        }
        catch (FileNotFoundException e){
            System.out.println("File not Found");
            e.printStackTrace();
        }
        
        
    }
    
}
