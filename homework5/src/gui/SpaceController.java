/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.rmi.RemoteException;

import Models.TasksProgressModel;
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
    
    public SpaceController(SpaceConsole console){
    	this.console = console;
    	Thread t = new Thread(new Runnable(){
			@Override
			public void run() {
				while (true) {
					updateConsole();
					try {
						Thread.sleep(UPDATE_INTERVAL);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
    		
    	});
    	t.start();
    }
    
    public void startSpace(){
        try {
			spaceImpl = new SpaceImpl();
	        spaceImpl.addSpaceListener(this);
	        spaceImpl.startSpace(hasSpaceRunnableTasks);
	    	latencyData = spaceImpl.getLatencyData();
	    	tasksProgressModel = spaceImpl.getTasksProgressModel();
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
    }
    
    int n = 0; //For testing
    private void updateConsole(){
    	// Update GUI Console
    	console.setActiveTasks(n++);
    	
    	//Example of use:
    	console.setFinishedTasks(tasksProgressModel.getTotalCompletedTasks());
    	console.setTotalTasks(tasksProgressModel.getTotalTasks());
    	
    	console.setProgress(n);
    	if (n >= 100) console.setStatus("Done.");
    }
    
}
