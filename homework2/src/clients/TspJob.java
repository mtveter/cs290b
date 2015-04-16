package clients;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tasks.TaskTsp;
import api.Result;
import api.Space;
import api.Task;

public class TspJob implements Job{

	final private double[][] cities;
	static private double[][] distances;

	private List<Task> partialTasks;
	private List<Integer> partialCityList;
	private List<Integer> shortestTour;
	private double shortestTourDistance;

	private long totalTaskTime, jobStartTime, totalJobTime;
	private int numOfTasks;

	/**
	 * @param cities A 2D-array with coordinates for the cities.
	 */
	public TspJob(double[][] cities) {
		this.cities = cities;
		initializeDistances();
	}


	/**
	 * Generates partial tasks and adds them to the {@link Space} given as argument to this method. 
	 * @param space The space the generated tasks should be added to.
	 */
	@Override
	public void generateTasks(Space space) throws RemoteException {
		jobStartTime = System.currentTimeMillis();
		partialTasks = new ArrayList<Task>();
		partialCityList = initialTour();
		for (int city : partialCityList){
			List<Integer> subPartialCityList = new ArrayList<>(partialCityList);
			subPartialCityList.remove((Integer) city);
			TaskTsp task = new TaskTsp(city, subPartialCityList, distances);
			partialTasks.add(task);
		}

		space.putAll(partialTasks);
	}

	/**
	 * Collects the results from the given {@link Space} argument, and compares them to determine the shortest tour.
	 */
	@Override
	public void collectResults(Space space) throws RemoteException {
		List<List<Integer>> partialResults = new ArrayList<List<Integer>>(Collections.nCopies(cities.length, null));
		numOfTasks = partialTasks.size();
		for (int i=0; i<numOfTasks; i++){
			Result<List<Integer>> r = space.take();
			int id = Integer.parseInt(r.getId());
			List<Integer> partialCityList = r.getTaskReturnValue();
			System.out.println("Partial Result (ID: "+id+"): "+partialCityList);
			System.out.println("Task Run Time: "+r.getTaskRunTime());
			totalTaskTime += r.getTaskRunTime();
			partialResults.set(id, partialCityList);
		}

		// Default shortest tour
		shortestTour = new ArrayList<>( partialCityList );
		shortestTour.add( 0, 0 );
		shortestTourDistance = TspJob.tourDistance( shortestTour );

		// Iterate through results and calculate the shortest tour
		for (int i=1; i<partialResults.size(); i++){
			List<Integer> tour = partialResults.get(i); 
			tour.add(0, 0);
			//tour.add(1, i);
			double tourDistance = TspJob.tourDistance( tour );
			if ( tourDistance < shortestTourDistance )
			{
				shortestTour = tour;
				shortestTourDistance = tourDistance;
			}
		}

		totalJobTime = System.currentTimeMillis() - jobStartTime;
	}

	/**
	 * @return A {@code List<Integer>} with the IDs for the cities in the correct order to achieve the shortest total distance.
	 */
	@Override
	public Object getAllResults() {
		System.out.println("\nTimes:");
		System.out.println("Total Task Time:\t\t" + totalTaskTime + " ms");
		System.out.println("Avg. Task Time:\t\t\t" + totalTaskTime / numOfTasks + " ms");
		System.out.println("Total Job Time:\t\t\t" + totalJobTime + " ms");
		System.out.println("Avg. Job Time (per Task):\t" + totalJobTime / numOfTasks + " ms");
		System.out.println();

		System.out.println("Shortest tour: "+shortestTour);
		System.out.println("Distance: "+shortestTourDistance);
		return shortestTour;
	}

	/**
	 * @author Peter Cappello
	 */
	private void initializeDistances()
	{
		distances = new double[ cities.length][ cities.length];
		for ( int i = 0; i < cities.length; i++ )
			for ( int j = 0; j < i; j++ )
			{
				distances[ i ][ j ] = distances[ j ][ i ] = distance( cities[ i ], cities[ j ] );
			}
	}

	/**
	 * @author Peter Cappello
	 */
	private List<Integer> initialTour()
	{
		List<Integer> tour = new ArrayList<>();
		for ( int city = 1; city < cities.length; city++ )
		{
			tour.add( city );
		}
		return tour;
	}

	/**
	 * @author Peter Cappello
	 */
	@Override
	public String toString()
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append( getClass() );
		stringBuilder.append( "\n\tCities:\n\t" );
		for ( int city = 0; city < cities.length; city++ )
		{
			stringBuilder.append( city ).append( ": ");
			stringBuilder.append( cities[ city ][ 0 ] ).append(' ');
			stringBuilder.append( cities[ city ][ 1 ] ).append("\n\t");
		}
		return stringBuilder.toString();
	}

	/**
	 * 
	 * @param tour
	 * @return Total distance of tour
	 * @author Peter Cappello
	 */
	public static double tourDistance( final List<Integer> tour  )
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

}
