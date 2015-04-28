package tasks;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import system.Closure;
import util.PermutationEnumerator;
import api.Result;
import api.Task;

public final class TaskTsp implements Task<List<Integer>>{

	/**	Generated serial ID */
	private static final long serialVersionUID = 1L;
	/** Identifier of task */
	private String id;
	
	/** The partial list of cities that are to be permuted (excluding the first city). */
	private List<Integer> partialCityList;
	
	private List<Integer> lockedCities;
	
	/** An array containing the computed values for the distances between all cities. */
	private double[][] distances;
	/** The limit to size of partial cities to by subivided and executed by Computer*/
	private static final int RECURSIONLIMIT = 8; 

	/**
	 * @param lockedCities 	List of cities with a locke position in tour for this partial task.
	 * @param partialCityList The partial list of cities that are to be permuted (excluding the first city).
	 * @param distances An array containing the computed values for the distances between all cities.
	 * @param id Identifier of Task
	 * @param lockedCities are the cities that have
	 */

	public TaskTsp(List<Integer>lockedCities, List<Integer> partialCityList, double[][] distances,String id){
		this.lockedCities = lockedCities;
		this.partialCityList = partialCityList;
		this.distances = distances;
		this.lockedCities = lockedCities;
		this.id = id;
	}


	/**
	 * {@inheritDoc}
	 * Finds the shortest tour by permuting the partial city list and comparing the tour distances. Slightly modified version of the code from HW1 by P. Cappello.
	 * @return Returns the shortest tour for the partial list of cities starting from first city reference.
	 */
	@Override
	public Result<?> call() throws RemoteException 
	{
		Result<?> result = null;
		List<Closure> childClosures = new ArrayList<Closure>();
		long taskStartTime = System.currentTimeMillis();
		
		int n = partialCityList.size();
		if(n == TaskTsp.RECURSIONLIMIT) {
			//int firstCity  = partialCityList.remove(0);
			//int firstCity = lockedCities.get(lockedCities.size()-1);
			List<Integer> shortestTour = new ArrayList<>( lockedCities );
			shortestTour.addAll(partialCityList);
			double shortestTourDistance = tourDistance( shortestTour );

			PermutationEnumerator<Integer> permutationEnumerator = new PermutationEnumerator<>( partialCityList );
			for ( List<Integer> subtour = permutationEnumerator.next(); subtour != null; subtour = permutationEnumerator.next() ) 
			{
				List<Integer> tour = new ArrayList<>( subtour );
				tour.addAll( 0, lockedCities );
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
			return new Result<>(shortestTour, shortestTourDistance, 0l, this.id);
		}
		else if(n > TaskTsp.RECURSIONLIMIT) {
			
			if(n > TaskTsp.RECURSIONLIMIT + 1) {
				for (int city : partialCityList) {
					
					List<Integer> newLockedList = new ArrayList<>(lockedCities);					
					List<Integer> subPartialCityList = new ArrayList<>(partialCityList);
					
					subPartialCityList.remove((Integer) city);
					newLockedList.add((Integer)city);
					
					TaskTsp task = new TaskTsp(newLockedList, subPartialCityList, distances, this.id+city);
					//partialTasks.add(task);
					
					Closure c = new Closure(subPartialCityList.size(), this.id, task);
					childClosures.add(c);	
				}
			}
			else if(n == TaskTsp.RECURSIONLIMIT + 1) {
				for (int city : partialCityList) {
		
					List<Integer> newLockedList = new ArrayList<>(lockedCities);					
					List<Integer> subPartialCityList = new ArrayList<>(partialCityList);
					
					
					subPartialCityList.remove((Integer) city);
					newLockedList.add((Integer)city);
					
					TaskTsp task = new TaskTsp(newLockedList, subPartialCityList, distances, this.id+city);
					
					Closure c = new Closure(1, this.id, task);
					childClosures.add(c);
				}
			}
			else {System.out.println("SPACE: The length of the partial city list is not correct");}
			long taskEndTime = System.currentTimeMillis();
			long taskRunTime = taskEndTime - taskStartTime;
			result = new Result<>(childClosures, taskRunTime, this.getId());
		}
		else {System.out.println("SPACE: The length of the partial city list is not correct");}
		return result;
	}
	
	/**
	 * 
	 * @param tour The tour of cities
	 * @return Total distance of tour
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

	/**
	 * Calculates the Euclidean distance between two points.
	 * @param city1 An array with coordinates for the first city.
	 * @param city2 An array with coordinates for the second city.
	 * @return distance
	 * @author Peter Cappello
	 */
	private static double distance( final double[] city1, final double[] city2 )
	{
		final double deltaX = city1[ 0 ] - city2[ 0 ];
		final double deltaY = city1[ 1 ] - city2[ 1 ];
		return Math.sqrt( deltaX * deltaX + deltaY * deltaY );
	}

	@Override
	public String getId() {
		return this.id;
	}
	
  

	@Override
	public Type getType() {
		return Type.TSP;
	}
	
	public List<Integer> getPartialCityList(){
		return this.partialCityList;
		
	}
}
