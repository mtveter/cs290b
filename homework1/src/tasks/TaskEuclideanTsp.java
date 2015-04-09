package tasks;

import java.util.ArrayList;
import java.util.List;

import api.Task;

public class TaskEuclideanTsp implements Task<List<Integer>> {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -2111720135431170695L;
	/** 2D array to store cities to visit in TSP */
	private final double[][] cities;
	
	public TaskEuclideanTsp(double[][] cities){
		this.cities = cities;
	}

	@Override
	public List<Integer> execute() {
		List<Integer> citiesList = new ArrayList<Integer>();
		for(int k = 0; k < cities.length; k++) {
			citiesList.add(k);
		}
		List<Integer> citiesStack = new ArrayList<Integer>();
		for(int l = 0; l < cities.length; l++) {
			citiesStack.add(l);
		}
		
		List<Integer> cityTour = new ArrayList<Integer>();
		cityTour.add(0);
		citiesStack.remove(0);
		// For each city: Find the closest neighboring city
		int counter = 0;
		while(counter < citiesList.size() - 1) {
			int cityFromIndex = cityTour.indexOf(cityTour.get(cityTour.size() - 1));
			double[] cityFrom = cities[cityFromIndex];
			Integer closestCity = -1;
			double shortestDistance = 0;
			
			for(Integer cityToIndex : citiesStack) {
				if(cityToIndex != cityFromIndex){
					double[] cityTo = cities[cityToIndex];
					double tempDistance = calcEuclideanDistance(cityFrom, cityTo);
					if(shortestDistance == 0 || tempDistance < shortestDistance) {
						System.out.println("gogo");
						shortestDistance = tempDistance;
						closestCity = cityToIndex;
					}
				}
			}
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
