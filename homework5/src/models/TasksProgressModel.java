package models;

/* Model accessing maintaining progress data of tasks in branch and bound composition and decomposition */
public class TasksProgressModel {
	
	/** Counter for the total of all levels pruned*/
	private int totalPrunedLevels;
	/** Current number of total pruned tasks */
	private int totalPrunedTasks;
	/** Current number of total generated tasks */
	private int totalGeneratedTasks;
	/** Current number of total completed tasks */
	private int totalCompletedTasks;
	/** Total number of cities in the job received */ 
	private int numberOfCities;
	/** Percentage value of total execution completed */
	private double tasksCompletedPercentage;
	
	private int prunedCounter;
	
	public TasksProgressModel() {
	}
	public void setTotalCities(int n){
		this.numberOfCities = n;
	}
	public int getTotalCities() {
		return this.numberOfCities;
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
	public double getTasksCompletedPercentage() {
		return tasksCompletedPercentage;
	}
	/**
	 *  Finds the percentage weight value of the completed task and adds it to total percentage progress
	 * @param numberOfLevels Number of levels down the in tree the task that is completed is
	 */
	public void addCompletedTaskWeight(int numberOfLevels) {
		double tempWeight = 100.0;
		for(int level = 1; level < numberOfLevels; level++) {
			tempWeight /= (numberOfCities - level); 
		}
		tasksCompletedPercentage += tempWeight;
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
	 * Average pruning depth
	 * @return The average pruning depth of the execution
	 */
	public double getAveragePruningDepth() {
		if (prunedCounter != 0) {
			return (double)totalPrunedLevels/prunedCounter;
		}
		return 0;
	}
	/**
	 * Update the current average pruning depth according to the new pruned subtree
	 * @param level The level of the depth in the tree a task's children were pruned
	 */
	public void increaseTotalPrunedLevels(int level){
		if(level <= numberOfCities) {
			prunedCounter += 1;
			totalPrunedLevels += (numberOfCities - level);
		}
	}
}
