package gui;

public interface SpaceConsole {
	
	public void setActiveTasks(int n);
	public void setFinishedTasks(int n);
	public void setPercentageCompleted(double d);
	public void setAvgPruningDepth(double d);
	public void setMaxDepth(int n);
	
	public void setProgress(int d);
	public void setStatus(String s);
	public void setEstimatedTimeLeft(String s);
	
	//public void setLatencies(String computer, ArrayList<Double> latencies);
	//public void addLatencyValue(String computer, double value);
	
	public void updateComputersList();
	
	//public void updateLatencies();
	
	public void setSpaceActive();
	public void setTaskStarted();
	public void setTaskFinished();

}
