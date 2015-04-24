package system;

import java.io.Serializable;

import api.Result;

public class ResultAdder implements Serializable {

		int[] numbers;
		int position;
		Result<?> finalresult;		
	
	public ResultAdder(int joinCounter){
		numbers= new int[joinCounter];
		position = 0;
	}
	
	public void addResult(Result<?> result ){
		
		numbers[position] = (int) result.getTaskReturnValue();
		position++;
		
		if(position == numbers.length){
			System.out.println("in addResult");
			int tempresult = 0;
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
