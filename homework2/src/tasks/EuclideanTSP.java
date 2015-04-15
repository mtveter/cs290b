package tasks;
import java.util.ArrayList;
import java.util.List;

import api.Task;

/**
 *  Implementation of Task interface to solve the general Euclidean Traveling Salesman Person Problem
 *	Implements a general approximation algorithm
 *  Algorithm constructs a minimum-weight spanning tree(MST), a Multigraph of MST, 
 *  and then relaxation on edges until tour has been found
 *  
 *  Does not produce an optimal solution since euclidean TSP is NP-complete
 */
public class EuclideanTSP implements Task<List<Integer>>{
	/**
	 * Generated serial ID
	 */
	private static final long serialVersionUID = 5779120438091516215L;
	/**
	 * Array to store cities to visit in TSP
	 */
	private final ArrayList<City> inputCities;
	
	/**
	 * 
	 * @param cities	The constructor takes a double[][] cities 
	 * 					that codes the x and y coordinates of city[i]: 
	 * 					cities[i][0] is the x-coordinate of city[i] 
	 * 					and cities[i][1] is the y-coordinate of city[i].
	 */
	public EuclideanTSP(double[][] cities){
		this.inputCities = new ArrayList<City>();
		addCitiesToList(cities);
		
	}
	/**
	 * Calculates the shortest path solution of TSP-problem
     * @return The order cities be visited in the tour, represented by their IDs.
     */
	public List<Integer> execute() {
		ArrayList<City> cities = new ArrayList<City>();
		ArrayList<Edge> MST = new ArrayList<Edge>();

		ArrayList<City> treeCities = new ArrayList<City>();
		for(City city : this.inputCities){
			cities.add(city);
		}

		treeCities.add(cities.remove(0));		//add start city to tree

		while(!cities.isEmpty()){		// while there are cities not connected to tree

			Edge lightestEdge = null;
			double minDistance = Double.MAX_VALUE;	// initialize minimum distance 

			for(City start : treeCities){		// for each city in tree

				for(City end : cities){		// for each city not yet added to tree

					double distance = distance(start, end);		// get the distance from start to end

					if( distance < minDistance ){	// if distance is a new minimum
						lightestEdge = new Edge(start, end);
						minDistance = distance;		// set new minimum distance
					}
				}
			}
			cities.remove(lightestEdge.getEnd());					// remove end from city array
			treeCities.add(lightestEdge.getEnd());
			MST.add(lightestEdge);		// add edge to tree (grow the tree with lightest edge possible)
		}

		ArrayList<City> MSTtour = MSTTour(MST);
		List<Integer> cityTour = getCityIDList(MSTtour);
		return cityTour;
	}
	/**
	 * Takes cities represented as integer in an array and converts them to City-objects and adds to list
	 * @param cities Cities to be added in list
	 * 
	 */
	private void addCitiesToList(double[][] cities) {
		for (int i = 0; i < cities.length; i++){
			City tempCity = new City(i, cities[i][0], cities[i][1]);
			this.inputCities.add(tempCity);
		}
	}
	/**
	 * Gets a list of ID's of the cities in a list
	 * @param cityList	An array of cities represented as City Objects
	 * @return			List of ID
	 */
	public List<Integer> getCityIDList(ArrayList<City> cityList) {
		List<Integer> cityIDs = new ArrayList<Integer>();
		for(int i = 0; i < cityList.size(); i++) {
			cityIDs.add(cityList.get(i).getId());
		}
		return cityIDs;
	}
	
	/**
	 * Takes a list of edges and creates a MST and calculates tour 
	 * @param m	List of edges that form a minimal spanning tree(MST)
	 * @return	
	 */
	private ArrayList<City> MSTTour(ArrayList<Edge> m){	
		
		ArrayList<Edge> MST = new ArrayList<Edge>();
		ArrayList<City> MSTTour = new ArrayList<City>();
		
		for(Edge edge : m){
			MST.add(edge);
		}
		
		return MSTTour(MST.get(0).getStart(), MST, MSTTour);
	}
	/**
	 * 
	 * @param city 		City to perform calculations on
	 * @param MST		Minimal spanning tree
	 * @param MSTTour	Current tour of minimal spanning tree
	 * @return			Final tour of minimal spanning tree
	 */
	private ArrayList<City> MSTTour(City city, ArrayList<Edge> MST, ArrayList<City> MSTTour){
		
		MSTTour.add(city);
		
		for(Edge edge : MST){
			City parent = edge.getStart();
			City child = edge.getEnd();
			if( parent.equals(city) && !MSTTour.contains(child)){	// for each child not already in tour
				MSTTour = MSTTour(edge.getEnd(), MST, MSTTour);
			}
		}
		return MSTTour;
	}

	//
	// Weight method
	//
	public static String weight(ArrayList<Edge> MST) {
		double weight = 0;
		for(Edge edge : MST){
			weight += distance(edge.getStart(), edge.getEnd());
		}
		return Double.toString(weight);
	}

	//
	// Length method
	//
	public static String length(ArrayList<City> MSTTour) {
		double length = 0;
		for (int i=0 ; i<MSTTour.size()-1 ; i++) {
			length += distance(MSTTour.get(i), MSTTour.get(i+1)) ;
		}
		return Double.toString(length);
	}

	//
	// Distance Method
	//
	private static double distance(City start, City end){
		double px = start.getX();
		double py = start.getY();
		double qx = end.getX();
		double qy = end.getY();

		return Math.sqrt(( (qx-px)*(qx-px) ) + ( (qy-py)*(qy-py) ));
	}

	@Override
	public List<Integer> call() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}
}

