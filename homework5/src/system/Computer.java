package system;

import java.rmi.Remote;
import java.rmi.RemoteException;

import api.Result;
import api.Task;

public interface Computer extends Remote {
	/**
	 * @param	task {@link Task}Task to be executed
	 * @return 		A type V
	 * @throws 		RemoteException if there is a communication error
	 */
	public Result<?> execute(Task<?> task) throws RemoteException;
	/**
	 * Terminate program
	 * @throws RemoteException If there is a communication error
	 */
	public void exit() throws RemoteException;
	
	/**
	 * 
	 * @return if the computer runs multiple threads
	 * @throws RemoteException	 If there is a communication error when remote is referenced
	 */
	public boolean runsCores() throws RemoteException;
	/**
	 * Sends the task from space to computer and puts it in its queue
	 * @param task to be executed
	 * @throws RemoteException If there is a communication error when remote is referenced
	 */
	public void getTask(Task<?> task) throws RemoteException;
	/**
	 * Returns result from result queue in computer to space.
	 * @return Result from task
	 * @throws RemoteException If there is a communication error when remote is referenced
	 * @throws InterruptedException If thread is interrupted
	 */
	public Result<?> sendResult() throws RemoteException, InterruptedException;
	/**
	 * Checks if the computer has a buffer to pre-fetch tasks
	 * @return True is there is available buffer slots
	 * @throws RemoteException If there is a communication error when remote is referenced
	 */
	public boolean bufferAvailable() throws RemoteException;
	/**
	 *  Checks what the buffer size of the computer is 
	 * @return The buffer size
	 * @throws RemoteException If there is a communication error when remote is referenced
	 */
	public int bufferSize() throws RemoteException;
	/**
	 * Gets the core count of a processor
	 * @return Numbers of cores on the computer
	 * @throws RemoteException If there is a communication error when remote is referenced
	 */
	public int coreCount() throws RemoteException;
	/**
	 * Sets the Shared object of the Computer
	 * @param sharedObject		Shared object
	 * @throws RemoteException	If there is a communication error when remote is referenced
	 */
	public void setShared(Shared sharedObject) throws RemoteException;
	/**
	 * Forced the set of the Shared object of the Computer
	 * @param sharedObject		Shared object
	 * @throws RemoteException	If there is a communication error when remote is referenced
	 */
	public void setSharedForced(Shared sharedObject) throws RemoteException;
	/**
	 * Gets the Shared object
	 * @return					Shared object
	 * @throws RemoteException	If there is a communication error when remote is referenced
	 */
	public Shared getShared() throws RemoteException;
	/**
	 * Gets the unique identifier of the Computer
	 * @return					Computer identifier
	 * @throws RemoteException	If there is a communication error when remote is referenced
	 */
	public int getId() throws RemoteException;
	/**
	 * Sets the unique identifier of the Computer
	 * @param id				Identifier
	 * @throws RemoteException	If there is a communication error when remote is referenced
	 */
	public void setId(int id) throws RemoteException;
	/**
	 * Gets the ComputerStatus of the Computer
	 * @return					The ComputerStatus object of the Computer
	 * @throws RemoteException	If there is a communication error when remote is referenced
	 */
	public ComputerStatus getComputerStatus() throws RemoteException;
	/**
	 * Sets the ComputerStatus of the Computer
	 * @param cs				ComputerStatus object
	 * @throws RemoteException	If there is a communication error when remote is referenced
	 */
	public void setComputerPreferences( ComputerStatus cs) throws RemoteException;

}
