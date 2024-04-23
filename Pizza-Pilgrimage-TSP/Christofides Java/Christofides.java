import java.util.*;

public class Christofides {

    private int[][] distances;
    private int[] minSpanningDegrees;
    private City[] cities;
    private int N;
	//stores file name of map
	private String fileName;

    /*
    Constructor
    */
    public Christofides(String fileName) {

		this.fileName = fileName;
		// Initialize arrays
        this.cities = FileIO.openMap(fileName);
        this.N = cities.length;
        distances = new int[N][N];

        // Fill with max value
        for (int i = 0; i < distances.length; i++) {
            Arrays.fill(distances[i], Integer.MAX_VALUE);
        }
        minSpanningDegrees = new int[N];
    }

    /*
    Return distance between two city IDs
     */
    private int getDistance(int id1, int id2) {

        // If distance already calculated, return
        if (distances[id1][id2] != Integer.MAX_VALUE)
            return distances[id1][id2];

        // If same id, return MAX_VALUE
        else if (id1 == id2)
            return Integer.MAX_VALUE;

        // Calculate and return
        else {
            distances[id1][id2] = Math.round((float) Math.sqrt(Math.pow(cities[id1].getX() - cities[id2].getX(), 2) +
                    Math.pow(cities[id1].getY() - cities[id2].getY(), 2)));
            distances[id2][id1] = distances[id1][id2];
            return distances[id1][id2];
        }
    }

    /*
    Return a set of Edges that comprise the minimum spanning tree
     */
    private Set<Edge> createMinSpanningTree() {

        Set<Integer> vertices = new HashSet<Integer>();
        Set<Edge> minSpanningTree = new HashSet<Edge>();
        int minimumDistanceToTree[] = new int[N];
        Arrays.fill(minimumDistanceToTree, Integer.MAX_VALUE);

        // Add vertex ids to set
        for (int i = 1; i < N; i++) {
            vertices.add(i);
        }

        // Queue to hold edges
        PriorityQueue<Edge> pq = new PriorityQueue<>();

        // Set first city ID
        int addedCityId = 0;

        // Iterate until each vertex has been removed from set (added to tree)
        while (!vertices.isEmpty()) {
            // Compare distance from recently added vertex to remaining vertices outside tree
            for (Integer v : vertices) {
                int distance = getDistance(addedCityId, v);
                // If distance from recent vertex to outside vertex is less than current min, add edge to queue
                if (distance < minimumDistanceToTree[v])
                    pq.add(new Edge(addedCityId, v, distance));
            }

            // Remove edges from queue until we find minimum edge weight reaching vertex outside tree
            Edge edge;
            do {
                edge = pq.poll();
            } while (!vertices.contains(edge.getDestination()));

            minSpanningTree.add(edge);                  // Add edge to tree
            minSpanningDegrees[edge.getSource()]++;     // Update degrees for vertices in tree
            minSpanningDegrees[edge.getDestination()]++;
            vertices.remove(edge.getDestination());     // Remove vertex from outside set
            addedCityId = edge.getDestination();        // Set newest added city
        }

        return minSpanningTree;
    }

    /*
    Get set of IDs for all odd degree vertices in the minimum spanning tree
     */
    private Set<Integer> getOddDegreeMinSpanningVertices() {

        Set<Integer> oddDegreeVertices = new HashSet<>();

        // Iterate through degree values, adding odd IDs to set
        for (int i = 0; i < N; i++) {
            if (minSpanningDegrees[i] % 2 == 1)
                oddDegreeVertices.add(i);
        }

        return oddDegreeVertices;
    }

    /*
    Get approximate minimum weight perfect matching set of edges from given set of vertices
     */
    private Set<Edge> getMinWeightPerfectMatching(Set<Integer> vertices) {

        Set<Edge> perfectMatchingEdges = new HashSet<>();

        PriorityQueue<Edge> pq = new PriorityQueue<>();

        // Add edges to priority queue
        for (Integer i : vertices) {
            for (Integer j : vertices) {
                if (i != j) {
                    pq.add(new Edge(i, j, getDistance(i, j)));
                }
            }
        }

        // Iterate until all vertices removed and matched
        while (!vertices.isEmpty()) {

            Edge edge;
            do {
                edge = pq.poll();
            } while ((!vertices.contains(edge.getDestination())) || (!vertices.contains(edge.getSource())));

            // Check if we can optimize pair better
            int biggestSwapDifference = 0;
            Edge edgeToSwap = null;
            for (Edge oldEdge : perfectMatchingEdges) {

                int pairDistance = oldEdge.getWeight() + edge.getWeight();
                int swapPairDistance1 = getDistance(oldEdge.getSource(), edge.getSource()) +
                        getDistance(oldEdge.getDestination(), edge.getDestination());
                int swapPairDistance2 = getDistance(oldEdge.getSource(), edge.getDestination()) +
                        getDistance(oldEdge.getDestination(), edge.getSource());

                if (swapPairDistance1 < swapPairDistance2 && swapPairDistance1 - pairDistance < biggestSwapDifference) {
                    biggestSwapDifference = swapPairDistance1 - pairDistance;
                    edgeToSwap = oldEdge;
                } else if (swapPairDistance2 - pairDistance < biggestSwapDifference) {
                    biggestSwapDifference = swapPairDistance2 - pairDistance;
                    edgeToSwap = oldEdge;
                }
            }

            // Swap edge for optimized edges
            if (biggestSwapDifference < 0 && edgeToSwap != null) {
                int id1 = edgeToSwap.getSource();
                int id2 = edgeToSwap.getDestination();
                int id3 = edge.getSource();
                int id4 = edge.getDestination();
                int swapPairDistance1 = getDistance(id1, id3) + getDistance(id2, id4);
                int swapPairDistance2 = getDistance(id1, id4) + getDistance(id2, id3);
                if (swapPairDistance1 < swapPairDistance2) {
                    perfectMatchingEdges.remove(edgeToSwap);
                    perfectMatchingEdges.add(new Edge(id1, id3, getDistance(id1, id3)));
                    perfectMatchingEdges.add(new Edge(id2, id4, getDistance(id2, id4)));
                }
                else {
                    perfectMatchingEdges.remove(edgeToSwap);
                    perfectMatchingEdges.add(new Edge(id1, id4, getDistance(id1, id4)));
                    perfectMatchingEdges.add(new Edge(id2, id3, getDistance(id2, id3)));
                }
            }

            // Add edge to set, remove both endpoints from vertices set
            if (biggestSwapDifference == 0)
                perfectMatchingEdges.add(edge);
            vertices.remove(edge.getSource());
            vertices.remove(edge.getDestination());
        }

        return perfectMatchingEdges;
    }

    /*
    Find set of edges that form a Eulerian circuit given a set of edges
     */
    private List<City> findEulerianCircuit(Set<Edge> multigraph) {

        List<City> eulerian = new ArrayList<City>();
		Stack<Integer> ids = new Stack<Integer>();
		Stack<Edge> edges = new Stack<Edge>();
        Iterator<Edge> it = multigraph.iterator();
		
		Edge e = it.next();
		ids.push(e.getSource());
		
		while(!multigraph.isEmpty()){
			
			if(e.getSource() == ids.peek())
			{
				multigraph.remove(e);
				
				ids.push(e.getDestination());
				//reset iterator becuase removes can make it have a null reference
				it = multigraph.iterator();
				
			}
			else if(e.getDestination() == ids.peek())
			{
				multigraph.remove(e);
				ids.push(e.getSource());
				//reset iterator becuase removes can make it have a null reference
				it = multigraph.iterator();
			}
			
			
			if(!it.hasNext())
			{
				eulerian.add(0, this.cities[ids.pop()]);
				it = multigraph.iterator();
			}
			
			
			if(it.hasNext())
			{
				e = it.next();
			}
		}
		
		while(!ids.isEmpty())
		{
			eulerian.add(0, this.cities[ids.pop()]);
		}

        return eulerian;
    }

    /*
    Find set of edges that form a Hamiltonian circuit given a set of edges that form a Eulerian circuit
     */
    private void findHamiltonianCircuit(List<City> eulerian) {
		
        int totalDistance = 0;
		//stores the path taken
		Stack<City> path = new Stack<City>();
		Set<City> usedVertices = new HashSet<>();
		Iterator<City> it = eulerian.iterator();
		
		//push first city so that there isn't a null city at the top of the stack
		City curCity = it.next();
		int startId = curCity.getId();
		usedVertices.add(curCity);
		path.push(curCity);
		
		while(it.hasNext()){
			curCity = it.next();
			if(!usedVertices.contains(curCity))
			{
				path.push(curCity);
				usedVertices.add(curCity);
			}
		}

        // Change stack to city array
        City[] cities = new City[N];
        path.toArray(cities);

        // Optimize path
        cities = optimizeHamiltonianPath(cities);
        totalDistance = calculateTotalDistance(cities);

        // Return to stack
        path.clear();
        for (int i = 0; i < cities.length; i++) {
            path.push(cities[i]);
        }

        // Write to file
        FileIO.writeMap(totalDistance, path, fileName + ".tour");
    }

    private City[] optimizeHamiltonianPath(City[] cities) {

        int lastDistance;

        do {

            lastDistance = calculateTotalDistance(cities);

            // Remove edge to form path
            for (int i = 0; i < N; i++) {

                boolean changed = false;

                // Set maximumGain for current iteration
                int maximumGain = 0;
                int reverseFrom = 0;

                int x = i + 1;
                int y = i + 2;
                if (x >= N) x %= N;
                if (y >= N) y %= N;

                // Check each subsequent edge
                while (y != i) {

                    // Get difference from swapping edges
                    int diff = getDistance(cities[x].getId(), cities[y].getId()) -
                            getDistance(cities[i].getId(), cities[x].getId());

                    // If improvement, save edge
                    if (diff > maximumGain) {
                        maximumGain = diff;
                        reverseFrom = y;
                    }

                    if (x + 1 >= N) x = 0;
                    else x++;
                    if (y + 1 >= N) y = 0;
                    else y++;
                }

                // If improvement found
                if (maximumGain > 0) {

                    // Create copy of array
                    City[] citiesCopy = Arrays.copyOf(cities, cities.length);
                    int reverseTo = i < reverseFrom ? i + N : i;

                    // Reverse section of path
                    while (reverseTo > reverseFrom) {
                        City temp = citiesCopy[reverseTo % N];
                        citiesCopy[reverseTo % N] = citiesCopy[reverseFrom % N];
                        citiesCopy[reverseFrom % N] = temp;
                        reverseTo--;
                        reverseFrom++;
                    }

                    // Check new distance against old
                    if (calculateTotalDistance(citiesCopy) < calculateTotalDistance(cities)) {
                        changed = true;
                        cities = citiesCopy;
                    }
                }

                if (changed) {
                    i--; // Repeat same loop
                }
            }
        } while (lastDistance > calculateTotalDistance(cities));    // Repeat until no improvement made

        return cities;
    }

    /*
        Sum up the weight of given edges that form a hamiltonian circuit
         */
    private int calculateTotalDistance(City[] cities) {

        int distance = 0;

        for (int i = 0; i < cities.length - 1; i++) {
            distance += getDistance(cities[i].getId(), cities[i + 1].getId());
        }

        distance += getDistance(cities[cities.length - 1].getId(), cities[0].getId());
        return distance;
    }

    /*
    Call methods, in order, to solve TSP using the Christofides method
     */
    public void solve() {

        long timeStart = System.nanoTime();

        Set<Edge> MST = createMinSpanningTree();                        // Create MST
        Set<Integer> oddVertices = getOddDegreeMinSpanningVertices();   // Get odd degree vertices from MST
        Set<Edge> PM = getMinWeightPerfectMatching(oddVertices);        // Get min-weight perfect matching

        Set<Edge> multiGraph = new HashSet<>();                         // Combine sets
        multiGraph.addAll(MST);
        multiGraph.addAll(PM);

        Set<Edge> eulerianSet = new HashSet<>();                           // Find Eulerian circuit
        List<City> eulerian = new ArrayList<City>();
		eulerian = findEulerianCircuit(multiGraph);
		

        Set<Edge> hamiltonian = new HashSet<>();                        // Find Hamiltonian circuit
        
		findHamiltonianCircuit(eulerian);

        long timeStop = System.nanoTime();
        double totalTime = ((timeStop - timeStart) / 1000000);
        System.out.println("Total time elapsed in ms: " + totalTime);
        totalTime /= 1000;
        System.out.println("Total time elapsed in secs: " + totalTime);
    }

    public static void main(String[] args) {
        String fileName = FileIO.getFileName();
		Christofides test = new Christofides(fileName);    //TODO - replace with file input
        /*
        for (int i = 0; i < test.N; i++) {
            for (int j = 0; j < test.N; j++) {
                test.getDistance(i, j);
            }
        }*/
        test.solve();
    }
}
