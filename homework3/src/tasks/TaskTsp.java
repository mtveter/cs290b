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
	
	/** An array containing the computed values for the distances between all cities. */
	private double[][] distances;

	/*
	 * @param firstCity The first city for this partial task.
	 * @param partialCityList The partial list of cities that are to be permuted (excluding the first city).
	 * @param distances An array containing the computed values for the distances between all cities.
	 * @param id Identifier of Task
	 */
	public TaskTsp(int firstCity, List<Integer> partialCityList, double[][] distances,String id){
		this.firstCity = firstCity;
		this.partialCityList = partialCityList;
		this.distances = distances;
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
		
		if(partialCityList.size()>1){
			//List<TaskTsp>partialTasks = new ArrayList<TaskTsp>();
			
			for (int city : partialCityList){
				List<Integer> subPartialCityList = new ArrayList<>(partialCityList);
				subPartialCityList.remove((Integer) city);
				TaskTsp task = new TaskTsp(city, subPartialCityList, distances,this.id+firstCity);
				//partialTasks.add(task);
				
				
				
				
				Closure c = new Closure(partialCityList.size()-1, task.id, task);
				childClosures.add(c);
			}
			
			result = new Result(childClosures,taskStartTime,this.id);
			return result;
			
		}
		else{
			List<Integer> tour = new ArrayList<>( partialCityList );
			tour.add( 0, firstCity );
			
			
			result= new Result(tour, taskStartTime, this.id);			
		
			return result;
		}
			
			
		
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

	@Override
	public Type getType() {
		return Type.TSP;
	}
	
	public List<Integer> getPartialCityList(){
		return this.partialCityList;
		
	}

}
