package system;

import java.io.Serializable;

import api.Task;

public class Closure implements Runnable, Serializable{
	private int joinCounter;
	private int n;
	private String parentId;
	private Task<?> task;
	private ResultAdder adder;
	private boolean notCompleted;
	
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
	public void recieveResult(int i){
		if(joinCounter>0){
			joinCounter--;
			adder.addResult(i);
			if(joinCounter==0){
				//get final result from ResultAdder and return to parent
				if(adder.getResult()!=0){
					notCompleted=false;
				}
			}
			
		}
		
		
	}
	@Override
	public boolean equals(Object o){
		Closure c = (Closure)o;
		return this.getTask().getId().equals(c.getTask().getId());
		
	}
	
}
