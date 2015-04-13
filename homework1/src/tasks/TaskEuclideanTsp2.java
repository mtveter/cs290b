package tasks;

import java.util.ArrayList;
import java.util.List;

import api.Task;

public class TaskEuclideanTsp2 implements Task<List<Integer>> {

	/**
	 * Calculates a tour that is the solution to the euclidean TSP problem by using
	 * shortest neighbor algorithm.
	 * TaskEuclideanTsp.java was originally used, but caused RMI-client to loop infinitely
	 */
	
	private static final long serialVersionUID = -2111720135431170695L;
	/** 2D array to store cities to visit in TSP */
	private final double[][] cities;
	/**
	 * 
	 * @param cities	The constructor takes a double[][] cities 
	 * 					that codes the x and y coordinates of city[i]: 
	 * 					cities[i][0] is the x-coordinate of city[i] 
	 * 					and cities[i][1] is the y-coordinate of city[i].
	 */
	public TaskEuclideanTsp2(double[][] cities){
		this.cities = cities;
	}
	/**
	 * Calculates the shortest path solution of TSP-problem
     * @return The order cities be visited in the tour, represented by their IDs.
     */
	@Override
	public List<Integer> execute() {
		// Create stack of city IDs
		List<Integer> citiesStack = new ArrayList<Integer>();
		for(int l = 0; l < cities.length; l++) {
			citiesStack.add(l);
		}
		
		List<Integer> cityTour = new ArrayList<Integer>();
		// Adds the first city to the tour, and pop it from stack of cities
		cityTour.add(0);
		citiesStack.remove(0);
		
		int counter = 0;
		// For each city: Find the closest neighboring city 
		while(counter < cities.length - 1) {
			int cityFromIndex = cityTour.indexOf(cityTour.get(cityTour.size() - 1));
			double[] cityFrom = cities[cityFromIndex];
			Integer closestCity = -1;
			double shortestDistance = 0;
			
			// 
			for(Integer cityToIndex : citiesStack) {
				if(cityToIndex != cityFromIndex){
					double[] cityTo = cities[cityToIndex];
					double tempDistance = calcEuclideanDistance(cityFrom, cityTo);
					// Replace current closest city if temporary distance is closer
					if(shortestDistance == 0 || tempDistance < shortestDistance) {
						shortestDistance = tempDistance;
						closestCity = cityToIndex;
					}
				}
			}
			// Add the closest city to tour, and pop it from stack of cities
			cityTour.add(closestCity);
			citiesStack.remove(closestCity);
			
			counter ++;
		}
		return cityTour;
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
