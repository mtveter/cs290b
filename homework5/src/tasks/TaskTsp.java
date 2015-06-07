package tasks;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import api.Result;
import api.Task;
import system.Closure;
import system.Computer;
import system.Shared;
import util.PermutationEnumerator;
import util.TspBounds;

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
	
	private boolean pruning = true;

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
	
	public int getLevel(){
		return 1 + id.length() - id.replace(".", "").length();
	}
	
	/*public static void main(String[] args) {
		Task task = new TaskTsp(null, new ArrayList<Integer>(), null, "0.12.4.5.8.9");
		System.out.println("Level: "+task.getLevel());
	}*/

	/**
	 * {@inheritDoc}
	 * Finds the shortest tour by permuting the partial city list and comparing the tour distances. Slightly modified version of the code from HW1 by P. Cappello.
	 * @return Returns the shortest tour for the partial list of cities starting from first city reference.
	 */
	@Override
	public Result<?> call() throws RemoteException {
		
		Result<?> result = null;
		List<Closure> childClosures = new ArrayList<Closure>();
		long taskStartTime = System.nanoTime();
		
		if(pruning && isOverUpperBound()){
			List<Integer> a = new ArrayList<Integer>();
			a.addAll(lockedCities);
			a.addAll(partialCityList);
			
			/* Calculate number of tasks that is pruned */
			Integer nrOfPrunedTasks = null;
			nrOfPrunedTasks = new Integer(1);
			for(int i = TaskTsp.RECURSIONLIMIT; i <= this.n; i++) {
				nrOfPrunedTasks *= i;
			}
			return new Result<>(a, 160.0, 0l, this.id, true, nrOfPrunedTasks, getLevel());
		}

		if(n == TaskTsp.RECURSIONLIMIT) {
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
					
					TaskTsp task = new TaskTsp(newLockedList, subPartialCityList, distances, this.id+ "." + city);

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
	/**
	 * Checks if the lower bound of a partial tour is higher then the established upper bound
	 * @return					True if the lowerbound is over upperbound
	 * @throws RemoteException If the remote object could not be accessed
	 */
	private boolean isOverUpperBound() throws RemoteException {
		if(getComputer().getShared().get() != null){
			double upperbound = (double) getComputer().getShared().get();
			
			double lowerbound = 0;
			Integer firstLocked = lockedCities.get(0);
			Integer lastLocked = lockedCities.get(lockedCities.size() - 1);
			
			if(lockedCities.size() == 1 || getComputer().getShared().getLbAdjacencyMapValue(lastLocked).size() > 2) {
				lowerbound = computeLowerBound(true, firstLocked, lastLocked, partialCityList);
			}
			else if(getComputer().getShared().getLbAdjacencyMapValue(lastLocked).size() == 2) {
				removeCityFromMst(2, lastLocked);
				lowerbound = computeLowerBound(false, firstLocked, lastLocked, partialCityList);
			}
			else if(getComputer().getShared().getLbAdjacencyMapValue(lastLocked).size() == 1) {
				removeCityFromMst(1, lastLocked);
				lowerbound = computeLowerBound(false, firstLocked, lastLocked, partialCityList);
			}
			
			// Adds the cost of distance from
			lowerbound += pathDistance(lockedCities);
			
			if(upperbound < lowerbound){
//				System.out.println("PRUNED Task(s)");
				return true;
			}
		}
		return false;
	}
	/**
	 * Calculates total distance of a given tour
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
	 * Calculates the total distance of traversing a path in the order given
	 * @param tour 	List of cities in path
	 * @return		Total distance of path
	 */
	private double pathDistance( final List<Integer> path  )
	{
		double cost = 0;

		for ( int city = 0; city < path.size() - 1; city ++ )
		{
			cost += distances[ path.get( city ) ][ path.get( city + 1 ) ];
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

	
	/**
	 * Calculates the lower
	 * @param newMst				true if a new mst needs to be computed
	 * @param city 					Start node in TSP problem
	 * @param cityIdentifierList	List of identifier of all cities in subproblem
	 * @param cities				Array of all distances between cities in the multi-graph
	 * @return	The total value of the lower bound
	 */

public double computeLowerBound(boolean newMst, Integer firstLocked, Integer startCityId, List<Integer> cityIdentifierList) {
		double result = 0; 
		
		try {
			
			if(cityIdentifierList.size() < 2) {
				System.out.println("There has to be atleast 2 cities to find a lowerbound with a MST");
				return result;
			}
			// 
			if(newMst) {
				// Returns total cost of minimal cost spanning tree generated by Prim's algorithm
				double mstCost = primsMstCost(cityIdentifierList);
				result += mstCost;
			}
			else {
				result += getComputer().getShared().getCurrentMstCost();
			}
			
			double shortestDistance1 = 0;
			for(int i = 0; i < cityIdentifierList.size(); i++) {
				int cityId = (int) cityIdentifierList.get(i);
				int tempStartCityId = (int) firstLocked;
				double tempDistance = distances[tempStartCityId][cityId];
				if(tempDistance < shortestDistance1 || shortestDistance1 == 0) {
					shortestDistance1 = tempDistance;
				}
			}
			
			double shortestDistance2 = 0;
			for(int i = 0; i < cityIdentifierList.size(); i++) {
				int cityId = (int) cityIdentifierList.get(i);
				int tempStartCityId = (int) startCityId;
				double tempDistance = distances[tempStartCityId][cityId];
				if(tempDistance < shortestDistance2 || shortestDistance2 == 0) {
					shortestDistance2 = tempDistance;
				}
			}
			result += shortestDistance1;
			result += shortestDistance2;	
		
		} catch (RemoteException e) {
			System.out.println("Error: Shared object could not be acessed");
		}
		return result;
	}
	/**
	 * Takes the last locked city and removes it from the minimal cost spanning tree
	 * @param num		Number of neighbors of the last locked city
	 * @param city		Last locked City
	 */
	
	public void removeCityFromMst(Integer num, Integer city) {
		try {
			Shared shared = getComputer().getShared();
			
			// The new locked city only has 1 neighbor
			if(num == 1) {
				Integer neighbor = getComputer().getShared().getLbAdjacencyMapValue(city).get(0);
				shared.removeNeighborFromCity(neighbor, city);
				shared.clearAdjList(city);
				
				shared.decrementCurrentMstCost(distances[city][neighbor]);
			}
			// The new locked city only has 2 neighbors
			else if(num == 2) {
				Integer neighbor1 = getComputer().getShared().getLbAdjacencyMapValue(city).get(0);
				Integer neighbor2 = getComputer().getShared().getLbAdjacencyMapValue(city).get(1);
				
				shared.removeNeighborFromCity(neighbor1, city);
				shared.addNeighborToCity(neighbor1, neighbor2);
				
				shared.removeNeighborFromCity(neighbor2, city);
				shared.addNeighborToCity(neighbor2, neighbor1);
				
				shared.clearAdjList(city);
				
				double currentMstCostDifference = 0;
				currentMstCostDifference += distances[city][neighbor1];
				currentMstCostDifference += distances[city][neighbor2];
				currentMstCostDifference -= distances[neighbor1][neighbor2];
	
				shared.incrementCurrentMstCost(currentMstCostDifference);
			}
		} catch (RemoteException e) {
			System.out.println("Error: Shared object could not be acessed");
		}
	}
	/**
	 * Calculates a new MST with its cost and updates the shared object with accordingly
	 * @param cityIdList	List of identifiers of all cities in partial list to find a MST of
	 * @return				Cost of all edges in the calculated MST of the given input cities
	 */
	
	private double primsMstCost(List<Integer> cityIdList)  {
		
		double costOfMst = 0;
		try {
			Shared shared = getComputer().getShared();
			
			// Visited cities
			List<Integer> visited = new ArrayList<Integer>();
			// Unvisited cities
			List<Integer> unvisited = new ArrayList<Integer>(cityIdList);
			
			Integer cityStart = unvisited.get(0);
			unvisited.remove(0);
			visited.add(cityStart);
			
			int counter = cityIdList.size();
			while(counter > 1){

				double shortestDistance = 0;
				int cityToVisitNum = 0;
				Integer tempCityToVisit = null;
				Integer tempCityFromVisited = null;
				
				// Double loop to check all visited cities paths to all unvisited cities
				for(int  i = 0; i < visited.size(); i++) {
					Integer cityFrom = visited.get(i);
					
					for(int  j = 0; j < unvisited.size(); j++) {
						Integer cityTo = unvisited.get(j);
						
						
						if(shortestDistance == 0){
							tempCityFromVisited = new Integer(cityFrom);
							shortestDistance = distances[cityFrom][cityTo];
							tempCityToVisit = unvisited.get(j);
							cityToVisitNum = i;
						}
						else{
							double tempDistance = distances[cityFrom][cityTo];
						
							if(tempDistance < shortestDistance) {
								tempCityFromVisited = new Integer(cityFrom);
								shortestDistance = tempDistance;
								tempCityToVisit = unvisited.get(j);
								cityToVisitNum = j;
							}
						}
					}
				}
				unvisited.remove(cityToVisitNum);
				//System.out.println("City TO VISIT: " + tempCityToVisit);
				visited.add(tempCityToVisit);
				
				shared.addNeighborToCity(tempCityToVisit, tempCityFromVisited);
				shared.addNeighborToCity(tempCityFromVisited, tempCityToVisit);
				
				costOfMst += shortestDistance;
				
				counter--;
			}
			shared.setCurrentMstCost(costOfMst);
		} catch (RemoteException e) {
			System.out.println("Error: Shared object could not be acessed");
		}
		
		return costOfMst;
	}
	
	
		
 /** Setting the new recursion limit, but checking that it's over the cities to be permuted.
  *  If the recursion limit is to high, it's set to as high as possible.
  * @see api.Task#setRecLimit(int)
  */
	public void setRecLimit(int i){
		
		if(i<=n){
				this.RECURSIONLIMIT = i;}
		else{
					this.RECURSIONLIMIT = n;
				}

	}
}
