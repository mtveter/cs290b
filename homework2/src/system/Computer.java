package system;

import java.rmi.RemoteException;

import api.Task;

/**
 * Interface of a Computer 'Server'
 */
public interface Computer {
	/**
	 * @param	task {@link Task}Task to be executed
	 * @return 		A type V
	 * @throws 		RemoteException if there is a communication error
	 */
	public <V> V execute(Task<V> task) throws RemoteException;
}
