package system;

import java.rmi.Remote;
import java.rmi.RemoteException;

import api.Result;
import api.Task;

public interface Computer extends Remote {
	/**
	 * @param	task {@link Task}Task to be divide
	 * @return 		A type V
	 * @throws 		RemoteException if there is a communication error
	 */
	public Result divide(Task<?> task) throws RemoteException;
	/**
	 * @param	task {@link Task}Task to be conquered
	 * @return 		A type V
	 * @throws 		RemoteException if there is a communication error
	 */
	public Result conquer(Task<?> task) throws RemoteException;
	
	/**
	 * Terminate program
	 * @throws RemoteException If there is a communication error
	 */
	public void exit() throws RemoteException;
}
