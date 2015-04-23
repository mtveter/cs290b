package system;

import java.io.Serializable;

public class ResultAdder implements Serializable {

	
		int[] numbers;
		int position;
		int finalresult;
		
	
	public ResultAdder(int joinCounter){
		
		numbers= new int[joinCounter];
		position = 0;
		
		
	}
	
	
	public void addResult(int num ){
		
		numbers[position]= num;
		position++;
		
		if(position==numbers.length){
			for(int i : numbers){
				finalresult+=i;
			}
		}
		
	}
	
	public int getResult(){
		return finalresult;
		
	}
	
	
}
