package test;

import java.util.ArrayList;
import java.util.List;

import util.TspBounds;

public class Test {
	public static void main(String args[]){
//		final double[][] CITIES = 
//		    {
//		    	{ 1, 0 },
//		    	{ 0, 0 },
//		    	{ 3, 0 },
//		    	{ 3, 4 },
//		    	{ 0, 4}
//		    };
		final double[][] CITIES = 
		    {
		    	{ 0, 0 },
		    	{ 1, 1 },
		    	{ 3, 1 },
		    	{ 2, 4 },
		    	{ 1, 6 },
		    	{ 6, 6 }
		    };
		double[][] distances = initializeDistances(CITIES);
		
		// List for lower bound
		List<Integer> cityIdListLB = new ArrayList<Integer>();
		for(int i = 1; i < CITIES.length; i++){
			cityIdListLB.add(i);
		}
		// List for upper bound
		List<Integer> cityIdListUB = new ArrayList<Integer>();
		for(int i = 0; i < CITIES.length; i++){
			cityIdListUB.add(i);
		}
		
		double resultLB = TspBounds.computeLowerBound(true, 0, 0, cityIdListLB, distances);
		System.out.println("Lower bound of cities: " + resultLB);
		
		double resultUB = TspBounds.computeUpperBound(cityIdListUB, distances);
		System.out.println("Upper bound of cities: " + resultUB);
	}
	
	
	private static double[][] initializeDistances(double[][] cities)
	{	
		double[][] distances;
		distances = new double[ cities.length][ cities.length];
		for ( int i = 0; i < cities.length; i++ )
			for ( int j = 0; j < i; j++ )
			{
				distances[ i ][ j ] = distances[ j ][ i ] = distance( cities[ i ], cities[ j ] );
			}
		return distances;
	}
	
	/**
	 * Calculates the Euclidean distance between two points.
	 * @param city1 An array with coordinates for the first city.
	 * @param city2 An array with coordinates for the second city.
	 * @return distance
	 */
	private static double distance( final double[] city1, final double[] city2 )
	{
		final double deltaX = city1[ 0 ] - city2[ 0 ];
		final double deltaY = city1[ 1 ] - city2[ 1 ];
		return Math.sqrt( deltaX * deltaX + deltaY * deltaY );
	}
}
