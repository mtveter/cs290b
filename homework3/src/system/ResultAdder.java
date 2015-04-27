package system;

import java.io.Serializable;

import api.Result;

public class ResultAdder implements Serializable {
		
	/** Generated serial identifier */
	private static final long serialVersionUID = 1L;
	/** Array Storing the arguments from results, length is equivalent to join counter */
	int[] numbers;
	/** Current position in array, does not exceed length of 'numbers' */
	int position;
	/** Final result after received all arguments*/
	Result<?> finalresult;		
	
	public ResultAdder(int joinCounter){
		numbers= new int[joinCounter];
		position = 0;
	}
	/**
	 * Takes a results adds it to list of received arguments
	 * and sets the final result if all arguments have been received
	 * @param result Result of addition of arguments
	 */
	public void addResult(Result<?> result ){
		numbers[position] = (Integer) result.getTaskReturnValue();
		position++;
		
		// Checks if all arguments(results) have been received
		if(position == numbers.length){
//			System.out.println("in addResult");
			int tempresult = 0;
			// Adds the partial results into a final result value
			for(int i : numbers){
				tempresult += i;
			}
			finalresult = new Result(tempresult, result.getTaskRunTime(), result.getId());
		}
	}
	
	public Result<?> getResult(){
		return finalresult;
	}
}
