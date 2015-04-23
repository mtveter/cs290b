package system;

import tasks.TaskFibonacci;

public class Closure implements Runnable{
	private int joinCounter;
	private int n;
	private String parentId;
	private TaskFibonacci task;
	
	public Closure(int joinCounter, int n, String parentId, TaskFibonacci task) {
		this.joinCounter = joinCounter;
		this.parentId = parentId;
		this.task = task;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
}
