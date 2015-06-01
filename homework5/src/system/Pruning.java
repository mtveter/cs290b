package system;

public class Pruning {
	
	private int totalPrunedTasks;

	public Pruning() {
		totalPrunedTasks = 0;
	}
	
	public int getNrOfPrunedTasks() {
		return this.totalPrunedTasks;
	}
	
	public void addPrunedTasks(int prunedTasks) {
		this.totalPrunedTasks += prunedTasks;
	}
}
