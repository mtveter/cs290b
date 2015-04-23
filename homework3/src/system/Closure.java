package system;

import api.Task;

public class Closure implements Runnable{
	private int joinCounter;
	private int n;
	private String parentId;
	private Task<?> task;
	
	public Closure(int joinCounter, int n, String parentId, Task<?> task) {
		this.joinCounter = joinCounter;
		this.parentId = parentId;
		this.task = task;
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
	
}
