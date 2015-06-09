package gui;

public interface SpaceConsole {
	
	public void setComputers(int n);
	
	public void setGeneratedTasks(int n);
	public void setCompletedTasks(int n);
	public void setActiveTasks(int n);
	//public void setFinishedTasks(int n);

	public void setAvgPruningDepth(double d);
	public void setPruningEfficiency(double d);
	
	public void setProgress(int n);
	public void setStatus(String s);
	public void setEstimatedTimeLeft(String s);
	
	public void updateComputersList();
	
	public void setSpaceActive();
	public void setTaskStarted();
	public void setTaskFinished();

}
