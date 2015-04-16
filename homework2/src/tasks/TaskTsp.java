package tasks;

import java.util.ArrayList;
import java.util.List;

import util.PermutationEnumerator;
import api.Task;

public class TaskTsp implements Task<List<Integer>>
{	
	/** The first city for this partial task. */
	private int firstCity;
	
	/** The partial list of cities that are to be permuted (excluding the first city). */
	private List<Integer> partialCityList;
	
	/** An array containing the computed values for the distances between all cities. */
	private double[][] distances;

	/*
	 * @param firstCity The first city for this partial task.
	 * @param partialCityList The partial list of cities that are to be permuted (excluding the first city).
	 * @param distances An array containing the computed values for the distances between all cities.
	 */
	public TaskTsp(int firstCity, List<Integer> partialCityList, double[][] distances){
		this.firstCity = firstCity;
		this.partialCityList = partialCityList;
		this.distances = distances;
	}

	/**
	 * Finds the shortest tour by permuting the partial city list and comparing the tour distances. Slightly modified version of the code from HW1 by P. Cappello.
	 * @return Returns the shortest tour for the partial list of cities starting from {@link #firstCity}.
	 * @author Peter Cappello
	 */
	@Override
	public List<Integer> call() 
	{
		List<Integer> shortestTour = new ArrayList<>( partialCityList );
		shortestTour.add( 0, firstCity );
		double shortestTourDistance = tourDistance( shortestTour );

		PermutationEnumerator<Integer> permutationEnumerator = new PermutationEnumerator<>( partialCityList );
		for ( List<Integer> subtour = permutationEnumerator.next(); subtour != null; subtour = permutationEnumerator.next() ) 
		{
			List<Integer> tour = new ArrayList<>( subtour );
			tour.add( 0, firstCity );
			if ( tour.indexOf( 1 ) >  tour.indexOf( 2 ) )
			{
				continue; // skip tour; it is the reverse of another.
			}
			double tourDistance = tourDistance( tour );
			if ( tourDistance < shortestTourDistance )
			{
				shortestTour = tour;
				shortestTourDistance = tourDistance;
			}
		}
		return shortestTour;
	}

	@Override
	public String getId() {
		return firstCity+"";
	}
	
    /**
     * @author Peter Cappello
     */
	private double tourDistance( final List<Integer> tour  )
	{
		double cost = distances[ tour.get( tour.size() - 1 ) ][ tour.get( 0 ) ];

		for ( int city = 0; city < tour.size() - 1; city ++ )
		{
			cost += distances[ tour.get( city ) ][ tour.get( city + 1 ) ];
		}
		return cost;
	}
}