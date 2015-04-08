package tasks;

import tasks.Permute;
import java.util.ArrayList;
import java.util.List;

import api.Task;

public class TaskEuclideanTsp implements Task<List<Integer>>{
	private double[][] cities;
	

	public TaskEuclideanTsp(double[][] cities) {
		this.cities = cities;
	}

	@Override
	public List<Integer> execute() {
		List<Integer> cityTour = new ArrayList<Integer>();
		cityTour = null;
		
		double shortestEuclideanDistance = 0;
		List<List<Integer>> permutations = Permute.permute(0,1,2,3,4,5,6,7,8,9);
		// Go through all possible permutations to find shortest cycle
		for(int i = 0; i < permutations.size(); i++) {
			List<Integer> tempTour = permutations.get(i);
			double tempEuclideanDistance = calcTourDistance(tempTour);
			
			// If it is first tour, set the current shortest path to the resulting distance
			if(cityTour == null) {
				shortestEuclideanDistance = tempEuclideanDistance;
				cityTour = tempTour;
			}
			// Otherwise check if next permutation result in a shorter path than current path
			else {
				if(tempEuclideanDistance < shortestEuclideanDistance) {
					shortestEuclideanDistance = tempEuclideanDistance;
					cityTour = tempTour;
				}
			}
		}
		return cityTour;
	}
	private double calcTourDistance(List<Integer> cityList) {
		double tempDistance = 0;
		for(int i = 0; i < cityList.size(); i++) {
			int cityFromId = cityList.get(i);
			
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
	
	private double calcEuclideanDistance(double[] cityFrom, double[] cityTo) {
		double distance = 0;
		
		double xDistance = Math.exp(cityTo[0] - cityFrom[0]);
		double yDistance = Math.exp(cityTo[1] - cityFrom[1]);
		distance = Math.sqrt(xDistance + yDistance);
		
		return distance;
	}
}
