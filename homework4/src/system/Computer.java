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
	
	//public int getId() throws RemoteException;
	
	public boolean runsCores() throws RemoteException;
	
	public void getTask(Task<?> task) throws RemoteException;
	
	public Result<?> sendResult() throws RemoteException, InterruptedException;
	
	public boolean bufferAvailable() throws RemoteException;
	
}
