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
	
	/** The first city for this partial task. */
	private int firstCity;
	
	/** The partial list of cities that are to be permuted (excluding the first city). */
	private List<Integer> partialCityList;
	
	List<Integer> lockedCities;
	
	/** An array containing the computed values for the distances between all cities. */
	private static double[][] distances;

	/*
	 * @param firstCity The first city for this partial task.
	 * @param partialCityList The partial list of cities that are to be permuted (excluding the first city).
	 * @param distances An array containing the computed values for the distances between all cities.
	 * @param id Identifier of Task
	 * @param lockedCities are the cities that have
	 */
	
	
	public TaskTsp(List<Integer>lockedCities, List<Integer> partialCityList, double[][] distances,String id){
		//this.firstCity = firstCity;
		this.partialCityList = partialCityList;
		this.distances = distances;
		this.lockedCities= this.lockedCities;
	}

	/**
	 * {@inheritDoc}
	 * Finds the shortest tour by permuting the partial city list and comparing the tour distances. Slightly modified version of the code from HW1 by P. Cappello.
	 * @return Returns the shortest tour for the partial list of cities starting from {@link #firstCity}.
	 */
	@Override
	public Result<?> call() throws RemoteException 
	{
		Result<?> result = null;
		List<Closure> childClosures = new ArrayList<Closure>();
		long taskStartTime = System.currentTimeMillis();
		
		
		//divide into subtasks
		if(partialCityList.size()>8){
			
			
			for (int city : partialCityList){
				
				List<Integer> subPartialCityList = new ArrayList<>();
				subPartialCityList.addAll(partialCityList);
				List<Integer> newLockedList=new ArrayList<>();
				
				newLockedList.addAll(lockedCities);
				subPartialCityList.remove((Integer) city);
				newLockedList.add((Integer)city);
				
				TaskTsp task = new TaskTsp(newLockedList, subPartialCityList, distances,this.id+firstCity);
				//partialTasks.add(task);
				
				Closure c = new Closure(partialCityList.size()-1, task.id, task);
				childClosures.add(c);
				
			}
			
			result = new Result(childClosures,taskStartTime,this.id);
			return result;
			
		}
		else{
			
			//Calculate the result
			
			List<Integer> tour = new ArrayList<>();
			tour.addAll(lockedCities);
						
			
			PermutationEnumerator<Integer> p = new PermutationEnumerator<Integer>(partialCityList);
			
			p.next();
			
			
			result= new Result(tour, taskStartTime, this.id);			
		
			return result;
		}
			
			
		
	}
	
	/**
	 * 
	 * @param tour The tour of cities
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


	@Override
	public String getId() {
		return firstCity+"";
	}
	
  

	@Override
	public Type getType() {
		return Type.TSP;
	}
	
	public List<Integer> getPartialCityList(){
		return this.partialCityList;
		
	}

}
