/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.rmi.RemoteException;

import models.TasksProgressModel;
import system.SpaceImpl;

/**
 *
 * @author Varun
 */
public class SpaceController implements SpaceListener {
    
    SpaceImpl spaceImpl;
    private boolean hasSpaceRunnableTasks;
    
    LatencyData latencyData;
    TasksProgressModel tasksProgressModel;
    SpaceConsole console;
    
    private final long UPDATE_INTERVAL = 500;
	private boolean isSpaceActive;
	private boolean isTaskActive;
	
	private TimeLeftEstimation timeLeftEstimation;
    
    public SpaceController(SpaceConsole console){
    	this.console = console;
    }
    
    public void startSpace(){
        try {
			spaceImpl = new SpaceImpl();
	        spaceImpl.addSpaceListener(this);
	        Thread spaceThread = new Thread(new Runnable(){
				@Override
				public void run() {
					try {
						isSpaceActive = true;
						spaceImpl.startSpace(hasSpaceRunnableTasks);
					} catch (RemoteException e) {
						e.printStackTrace();
					}	
				}	
	        });
	        spaceThread.start();
	    	latencyData = spaceImpl.getLatencyData();
	    	tasksProgressModel = spaceImpl.getTasksProgressModel();
	    	console.setSpaceActive();
	    	Thread t = new Thread(new Runnable(){
				@Override
				public void run() {
					while (true) {
						if (isTaskActive()) updateConsole();
						try {
							Thread.sleep(UPDATE_INTERVAL);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
	    		
	    	});
	    	t.start();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
    }
    
    public void setHasSpaceRunnableTasks(boolean b){
        hasSpaceRunnableTasks = b;
    }

    @Override
    public void update(String propertyName, Object value) {
        System.out.println(propertyName+": "+value);
        if (propertyName.equals(SpaceListener.COMPUTER_ADDED)){
        	console.updateComputersList();        	
        } else if (propertyName.equals(SpaceListener.MASTER_TASK_STARTED)){
        	isTaskActive = true;
        	timeLeftEstimation = new TimeLeftEstimation();
        	timeLeftEstimation.start();
        	console.setTaskStarted();
        } else if (propertyName.equals(SpaceListener.MASTER_TASK_FINISHED)){
        	updateConsole();
        	isTaskActive = false;
        	timeLeftEstimation.finished();
        	console.setTaskFinished();
        }
    }
    
    private void updateConsole(){
    	// Update GUI Console
    	console.setActiveTasks(tasksProgressModel.getTotalGeneratedTasks());
    	console.setFinishedTasks(tasksProgressModel.getTotalCompletedTasks());
    	console.setPercentageCompleted(tasksProgressModel.getTasksCompletedPercentage());
    	
    	console.setAvgPruningDepth(tasksProgressModel.getPrunedOfTotalTasksRatio());
    	console.setMaxDepth((int) tasksProgressModel.getPrunedOfGeneratedTasksRatio());
    	
    	int progress = (int)(tasksProgressModel.getTasksCompletedPercentage());
    	console.setProgress(progress);
    	if (progress > 5){
    		timeLeftEstimation.updateEstimation(progress);
    		console.setEstimatedTimeLeft(timeLeftEstimation.toString());
    	}
    }
    
    public LatencyData getLatencyData(){
    	return latencyData;
    }

	public boolean isSpaceActive() {
		return isSpaceActive;
	}
	
	public boolean hasActiveComputers(){
		return spaceImpl.hasActiveComputers();
	}

	public boolean isTaskActive() {
		return isTaskActive;
	}
    
}
