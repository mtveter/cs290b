package api;

import java.io.Serializable;
import java.rmi.RemoteException;
/**
 * Task interface for implementing Task objects
 * @param <T>
 */
public interface Task<T> extends Serializable{
	/**
	 * @return 					Object to call on
	 * @throws RemoteException	Communication-related exception that may occur during the execution of a remote method call
	 */
	public <V> V call() throws RemoteException;
	/**
	 * @return 					Identifier of Task
	 * @throws RemoteException 	Communication-related exception that may occur during the execution of a remote method call
	 */
	public String getId() throws RemoteException ; 
}
