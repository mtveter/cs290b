package system;

import java.io.Serializable;

import api.Result;
import api.Task;

public class Closure implements Runnable, Serializable{
	/** Generated serial identifier */
	private static final long serialVersionUID = 1L;
	private int joinCounter;
	private int n;
	private String parentId;
	private Task<?> task;
	private ResultAdder adder;
	
	public Closure(int joinCounter, int n, String parentId, Task<?> task) {
		this.joinCounter = joinCounter;
		this.parentId = parentId;
		this.task = task;
		this.adder= new ResultAdder(joinCounter);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
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
	
	public int getN() {
		return this.n;
	}
	public void receiveResult(Result<?> r){
		System.out.println(this.getTask().getId() + ": Closure recieved Result");
		if(joinCounter > 0){
			System.out.println(this.getTask().getId() + ": Result is being added to adder and joinCounter decremented");
			adder.addResult(r);
			joinCounter--;
		}	
	}
	public ResultAdder getAdder(){
		return this.adder;
	}
	
	public boolean isCompleted() {
		if(joinCounter == 0) {
			return true;
		}
		return false;
	}
	
}
