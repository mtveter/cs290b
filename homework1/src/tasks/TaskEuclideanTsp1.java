package tasks;

import tasks.Permute;
import java.util.ArrayList;
import java.util.List;

import api.Task;


/**
 * Implementation of Task interface to solve the general Euclidean Traveling Salesman Person Problem
 */
public final class TaskEuclideanTsp1 implements Task<List<Integer>>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7295824401317037138L;
	/** 2D array to store cities to visit in TSP */
	private final double[][] cities;
	/** 2D List of integer that gives the shortest path solution, stored with ID of each city */
	private final List<List<Integer>> permutations;
	
	/**
	 * 
	 * @param cities	The constructor takes a double[][] cities 
	 * 					that codes the x and y coordinates of city[i]: 
	 * 					cities[i][0] is the x-coordinate of city[i] 
	 * 					and cities[i][1] is the y-coordinate of city[i].
	 */
	public TaskEuclideanTsp1(double[][] cities) {
		this.cities = cities;
		// TODO: Pass parameter as the numbers from 0->cities.size() instead og having to manually change sequence
		// Calculates the factorial permutations
		permutations = Permute.permute(0,1,2,3,4,5,6,7,8,9);
	}
	/**
	 * Calculates the shortest path solution of TSP-problem
     * @return The order cities be visited in the tour, represented by their IDs.
     */
	@Override
	public List<Integer> execute() {
		List<Integer> cityTour = new ArrayList<Integer>();
		cityTour = null;
		
		double shortestEuclideanDistance = 0;
		
		// Go through all possible permutations to find shortest cycle
		for(int i = 0; i < permutations.size(); i++) {
			List<Integer> tempTour = permutations.get(i);
			double tempEuclideanDistance = calcTourDistance(tempTour);
			
			// IF: it is the first tour, set the current shortest path to the resulting distance
			if(cityTour == null) {
				shortestEuclideanDistance = tempEuclideanDistance;
				cityTour = tempTour;
			}
			// Otherwise check if next permutation result in a shorter path than current path
			else {
				// IF: Current is shorter, update the temporary shortest distance and corresponding tour
				if(tempEuclideanDistance < shortestEuclideanDistance) {
					shortestEuclideanDistance = tempEuclideanDistance;
					cityTour = tempTour;
				}
			}
		}
		return cityTour;
	}
	/**
	 * Calculates the shortest path solution of TSP-problem
	 * @param cityList	List of integer that represent a tour
     * @return 			The total distance traveling traversing the tour
     */
	private double calcTourDistance(List<Integer> cityList) {
		double tempDistance = 0;
		// Iterate all edges between the neighboring cities and calculate the distance
		for(int i = 0; i < cityList.size(); i++) {
			int cityFromId = cityList.get(i);
			
			// IF: last city in list, calculate distance to firste city in list
			if(i == cityList.size() - 1) {
				int cityToId = cityList.get(0);
				tempDistance += calcEuclideanDistance(cities[cityFromId], cities[cityToId]);
			}
			else {
				int cityToId = cityList.get(i+1);
				tempDistance += calcEuclideanDistance(cities[cityFromId], cities[cityToId]);
			}
		}
		return tempDistance;
	}
	/**
	 * Calculates the euclidean distance between two cities
	 * @param cityFrom	The starting city
	 * @param cityTo	The destination city
     * @return 			The euclidean distance between the two cities
     */
	private double calcEuclideanDistance(double[] cityFrom, double[] cityTo) {
		double distance = 0;
		// Performs the basic algebraic formula for euclidean distance between two points in the 2D-plane
		double xDistance = Math.exp(cityTo[0] - cityFrom[0]);
		double yDistance = Math.exp(cityTo[1] - cityFrom[1]);
		distance = Math.sqrt(xDistance + yDistance);
		
		return distance;
	}
}
