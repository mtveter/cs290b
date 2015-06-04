package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class TspBounds {
	
	public static double computeUpperBound(List<Integer> cityIdList, double[][] distances){
		double result = 0;
		
		// Visited cities
		List<Integer> visited = new ArrayList<Integer>();
		// Unvisited cities
		List<Integer> unvisited = new ArrayList<Integer>(cityIdList);
		
		Integer cityToVisit = unvisited.get(0);
		unvisited.remove(0);
		visited.add(cityToVisit);
		
		int counter = cityIdList.size();
		while(counter > 1){
			
			double shortestDistance = 0;
			int cityToVisitNum = 0;
			Integer tempCityToVisit = null;
			for(int i = 0; i < unvisited.size(); i++){
				if(shortestDistance == 0){
					shortestDistance = distances[cityToVisit][unvisited.get(i)];
					tempCityToVisit = unvisited.get(i);
					cityToVisitNum = i;
				}
				else{
					double tempDistance = distances[cityToVisit][unvisited.get(i)];
					if(tempDistance < shortestDistance) {
						shortestDistance = tempDistance;
						tempCityToVisit = unvisited.get(i);
						cityToVisitNum = i;
					}
				}
			}
			unvisited.remove(cityToVisitNum);
			visited.add(tempCityToVisit);
			cityToVisit = tempCityToVisit;
			
			result += shortestDistance;
			
			counter--;
		}
		Integer startCity = visited.get(0);
		Integer endCity = visited.get(visited.size() - 1);
		// Add Cost from last city in tour to start city
		result += distances[startCity][endCity];
		
		return result;
	}
}
