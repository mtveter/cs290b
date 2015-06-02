package Models;

import tasks.TaskTsp;

/* Model accessing maintaining progress data of tasks in branch and bound composition and decomposition */
public class TasksProgressModel {
	
	/** Current number of total pruned tasks */
	private int totalPrunedTasks;
	/** Current number of total generated tasks */
	private int totalGeneratedTasks;
	/** Current number of total completed tasks */
	private int totalCompletedTasks;
	/** Total number of tasks that would have been generated by execution of job with no pruning mechanism */
	private int totalTasks;

	public TasksProgressModel(int n) {
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
	/**
	 * Increases count of total pruned tasks
	 * @param prunedTasks	Number of newly pruned tasks
	 */
	public void increaseTotalPrunedTasks(int prunedTasks) {
		this.totalPrunedTasks += prunedTasks;
	}
	/**
	 * Increases count of total generated tasks
	 * @param genTasks		Number of newly generated tasks
	 */
	public void increaseTotalGeneratedTasks(int genTasks) {
		this.totalGeneratedTasks += genTasks;
	}
	/**
	 * Increases count of total completed tasks
	 * @param completedTasks	Number of newly completed tasks
	 */
	public void increaseTotalCompletedTasks(int completedTasks) {
		this.totalCompletedTasks += completedTasks;
	}
	/**
	 * Calculates ratio of pruned tasks relative to generated tasks
	 * @return	Ratio
	 */
	public double getPrunedOfGeneratedTasksRatio() {
		return ((double)totalPrunedTasks)/totalGeneratedTasks;
	}
	/**
	 * Calculates ratio of pruned tasks relative to total tasks
	 * @return	Ratio
	 */
	public double getPrunedOfTotalTasksRatio(){
		return ((double)totalPrunedTasks)/totalTasks;
	}
	/**
	 * Calculates ratio of completed tasks relative to total tasks
	 * @return	Ratio
	 */
	public double getCompletedOfTotalTasksRatio(){
		return ((double)totalCompletedTasks)/totalTasks;
	}
	/**
	 * Calculates ratio of generated tasks relative to total tasks
	 * @return	Ratio
	 */
	public double getGeneratedOfTotalTasksRatio(){
		return ((double)totalGeneratedTasks)/totalTasks;
	}
}
