package tasks;

import java.util.ArrayList;
import java.util.List;

import util.PermutationEnumerator;
import api.Task;

public class TaskTsp implements Task<List<Integer>>
{	
	private int firstCity;
	private List<Integer> partialCityList;
	private double[][] distances;

	public TaskTsp(int firstCity, List<Integer> partialCityList, double[][] distances){
		this.firstCity = firstCity;
		this.partialCityList = partialCityList;
		this.distances = distances;
	}

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