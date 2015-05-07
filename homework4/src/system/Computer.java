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
	 * @throws RemoteException
	 */
	public boolean runsCores() throws RemoteException;
	
	/**
	 * Sends the task from space to computer and puts it in its queue
	 * @param task to be executed
	 * @throws RemoteException
	 */
	
	public void getTask(Task<?> task) throws RemoteException;
	
	/**
	 * Returns result from result queue in computer to space.
	 * @return Result from task
	 * @throws RemoteException
	 * @throws InterruptedException
	 */
	
	public Result<?> sendResult() throws RemoteException, InterruptedException;
	
	/**
	 * Checks if the computer has a buffer to prefetch tasks
	 * @return
	 * @throws RemoteException
	 */
	
	public boolean bufferAvailable() throws RemoteException;
	
	/**
	 *  Checks what the buffer size of the computer is 
	 * @return the buffer size
	 * @throws RemoteException
	 */
	
	public int bufferSize() throws RemoteException;
	
	/**
	 * 
	 * @return numbers of cores on the computer
	 * @throws RemoteException
	 */
	public int coreCount() throws RemoteException;
	
	
	
}
