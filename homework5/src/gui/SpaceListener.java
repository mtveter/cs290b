/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

/**
 *
 * @author Varun
 */
public interface SpaceListener {
    
    public static final String MASTER_TASK_STARTED = "MasterTaskStarted";
    public static final String MASTER_TASK_FINISHED = "MasterTaskFinished";
    public static final String OVERALL_PROGRESS = "OverallProgress";
    public static final String COMPUTER_ADDED = "ComputerAdded";
    public static final String COMPUTER_REMOVED = "ComputerRemoved";
    
    public void update(String propertyName, Object value);
    
}
