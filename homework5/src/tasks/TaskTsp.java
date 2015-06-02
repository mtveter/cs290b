package tasks;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import system.Closure;
import system.Shared;
import system.TspShared;
import util.PermutationEnumerator;
import util.TspBounds;
import api.Result;
import api.Task;

public final class TaskTsp extends BaseTask<List<Integer>>{

	/**	Generated serial ID */
	private static final long serialVersionUID = 1L;
	/** Identifier of task */
	private String id;

	private final int n;

	/** The partial list of cities that are to be permuted (excluding the first city). */
	private List<Integer> partialCityList;

	private List<Integer> lockedCities;

	/** An array containing the computed values for the distances between all cities. */
	public double[][] distances;
	
	/** The limit to size of partial cities to by subdivided and executed by Computer*/
	public static int RECURSIONLIMIT = 7; 
	
	private boolean pruning=true;

	/**
	 * @param lockedCities 	List of cities with a locked position in tour for this partial task.
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
		this.n = partialCityList.size();
	}


	/**
	 * {@inheritDoc}
	 * Finds the shortest tour by permuting the partial city list and comparing the tour distances. Slightly modified version of the code from HW1 by P. Cappello.
	 * @return Returns the shortest tour for the partial list of cities starting from first city reference.
	 */
	@Override
	public Result<?> call() throws RemoteException 
	{
		if(this.id.equals("062")){
			System.out.println("thisis locked "+this.lockedCities);
		}
		
		Result<?> result = null;
		List<Closure> childClosures = new ArrayList<Closure>();
		long taskStartTime = System.nanoTime();
		if(pruning && isOverUpperBound()){
			//return a result with infinite lenght.
			//System.out.println("TASK: Pruned stop! ");
			//System.out.println(lockedCities);
			List<Integer> a = new ArrayList<Integer>();
			a.addAll(lockedCities);
			a.addAll(partialCityList);
			return new Result<>(a ,160.0,0l, this.id,true);
			/*TaskTsp task = new TaskTsp(lockedCities, partialCityList, distances, this.id+3);
			
			Closure c = new Closure(1, this.id, task);
			c.getAdder().addResult(new Result<>(lockedCities,160.0,0l,this.id));
			childClosures.add(c);
			return new Result<>(childClosures, 1009, this.getId());*/
			
		}



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
			long taskEndTime = System.nanoTime();
			long taskRunTime = taskEndTime - taskStartTime;
			
			return new Result<>(shortestTour, shortestTourDistance, taskRunTime, this.id);
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
			long taskEndTime = System.nanoTime();
			long taskRunTime = taskEndTime - taskStartTime;
			result = new Result<>(childClosures, taskRunTime, this.getId());
		}
		else {System.out.println("SPACE: The length of the partial city list is not correct");}
		return result;
	}

	private boolean isOverUpperBound() throws RemoteException {
		// TODO Auto-generated method stub
		if(getComputer().getShared().get() != null){
			double upperbound = (double) getComputer().getShared().get();
			
			double lowerbound = TspBounds.computeLowerBound(lockedCities.get(lockedCities.size()-1), partialCityList, distances);
			if(lockedCities.size() > 1){
				lowerbound+= distances[lockedCities.get(0)][lockedCities.get(1)];
			}
			if(upperbound < lowerbound){
				return true;
			}
		}
		return false;
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
	private double tourDistance2( final List<Integer> tour  )
	{
		double cost = 0;

		for ( int city = 0; city < tour.size() - 1; city ++ )
		{
			cost += distances[ tour.get( city ) ][ tour.get( city + 1 ) ];
		}
		return cost;
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
	@Override
	public int getN() {
		return this.n;
	}
	public void setRecLimit(int i){
		this.RECURSIONLIMIT = i;
	}
}
