package system;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import api.Result;

public class ResultAdder implements Serializable {
		
	/** Generated serial identifier */
	private static final long serialVersionUID = 1L;
	/** Array Storing the arguments from results, length is equivalent to join counter */
	double[] numbers;
	
	List<List<Integer>> tours = new ArrayList<List<Integer>>();
	/** Current position in array, does not exceed length of 'numbers' */
	int position;
	/** Final result after received all arguments*/
	Result<?> finalresult;		
	
	public ResultAdder(int joinCounter){
		numbers= new double[joinCounter];
		position = 0;
	}
	/**
	 * Takes a results adds it to list of received arguments
	 * and sets the final result if all arguments have been received
	 * @param result Result of addition of arguments
	 */
	public void addResult(Result<?> result ){
		if (result.getTaskReturnDistance() == null) {
			numbers[position] = (Integer) result.getTaskReturnValue();
			position++;
			
			// Checks if all arguments(results) have been received
			if(position == numbers.length){
				int tempresult = 0;
				// Adds the partial results into a final result value
				for(double i : numbers){
					tempresult += i;
				}
				finalresult = new Result<>(tempresult, result.getTaskRunTime(), result.getId());
			}
		}
		else {
			numbers[position] = (Double) result.getTaskReturnDistance();
			tours.add((List<Integer>) result.getTaskReturnValue()); 
			position++;
			
			// Checks if all arguments(results) have been received
			if(position == numbers.length){
				double tempResult = numbers[0];
				List<Integer> tempTour = tours.get(0);
				// Adds the partial results into a final result value
				for(int  i = 0; i < numbers.length; i++){
					if(numbers[i] < tempResult) {
						
						tempResult = numbers[i];
						tempTour = tours.get(i);
					}
				}
				finalresult = new Result<>(tempTour ,tempResult, result.getTaskRunTime(), result.getId());
			}
		}
	}
	
	public Result<?> getResult(){
		return finalresult;
	}
	public void setResult(Result<?> result ){
		finalresult = result;
		
	}
}
