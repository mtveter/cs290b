package system;

import java.io.Serializable;

import api.Result;
import api.Task;

public class Closure implements Serializable{
	/** Generated serial identifier */
	private static final long serialVersionUID = 1L;
	/**  */
	private int joinCounter;
	/** Identifier of Closure that is parent in recursion tree */
	private String parentId;
	/** Task with 1-to-1 relationship with Closure*/
	private Task<?> task;
	/** ResultAdder with 1-to-1 relationship with Closure*/
	private ResultAdder adder;
	
	public Closure(int joinCounter, String parentId, Task<?> task) {
		this.joinCounter = joinCounter;
		this.parentId = parentId;
		this.task = task;
		this.adder= new ResultAdder(joinCounter);
	}
	
	public Task<?> getTask() {
		return this.task;
	}
	
	public String getParentId() {
		return this.parentId;
	}
	
	public int getJoinCounter() {
		return this.joinCounter;
	}
	/**
	 * Receives a Result and passes it to its ResultAdder
	 * @param r	Result to be processed by Closure
	 */
	public void receiveResult(Result<?> r){
		System.out.println(this.getTask().getId() + ": Closure recieved Result");
		// JoinCounter cannot be less than 0
		
		if(joinCounter > 0){
			// Passes result to ResultAdder and decrements joinCounter
			System.out.println(this.getTask().getId() + ": Result is being added to adder and joinCounter decremented");
			adder.addResult(r);
			joinCounter--;
		}	
	}
	public ResultAdder getAdder(){
		return this.adder;
	}
	/**
	 * Indicates if Closure has received all arguments from children
	 * @return True it is completed, false if not
	 */
	public boolean isCompleted() {
		if(joinCounter == 0) {
			return true;
		}
		return false;
	}
}
