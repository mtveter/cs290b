package gui;

public interface SpaceConsole {
	
	public void setActiveTasks(int n);
	public void setFinishedTasks(int n);
	public void setTotalTasks(int n);
	public void setAvgPruningDepth(double d);
	public void setMaxDepth(int n);
	
	public void setProgress(int n);
	public void setStatus(String s);
	public void setEstimatedTimeLeft(String s);
	
	//public void setLatencies(String computer, ArrayList<Double> latencies);
	public void setLatencyData(LatencyData latencyData);

}
