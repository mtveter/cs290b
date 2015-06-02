package system;

import tasks.TaskTsp;

public class Pruning {
	
	private int totalPrunedTasks;
	private int totalGeneratedTasks;
	private int totalCompletedTasks;
	private int totalTasks;

	public Pruning(int n) {
		totalPrunedTasks = 0;
		totalGeneratedTasks = 0;
		totalCompletedTasks = 0;
		setTotalTasks(n);
	}
	private void setTotalTasks(int n){
		this.totalTasks = n;
		for(int i = n - 1; i > TaskTsp.RECURSIONLIMIT; i--) {
			this.totalTasks *= i;
		}
	}
	public int getTotalPrunedTasks() {
		return totalPrunedTasks;
	}
	public int getTotalGeneratedTasks() {
		return totalGeneratedTasks;
	}
	public int getTotalCompletedTasks() {
		return totalCompletedTasks;
	}
	public int getTotalTasks() {
		return totalTasks;
	}
	
	public void increaseTotalPrunedTasks(int prunedTasks) {
		this.totalPrunedTasks += prunedTasks;
	}
	public void increaseTotalGeneratedTasks(int genTasks) {
		this.totalGeneratedTasks += genTasks;
	}
	public void increaseTotalCompletedTasks(int completedTasks) {
		this.totalCompletedTasks += completedTasks;
	}
}
