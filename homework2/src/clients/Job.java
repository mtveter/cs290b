package clients;

import java.rmi.RemoteException;

import api.Space;

public interface Job<T> {
	/**
	 * Generate subtasks of Job and passes them to somputer Space
	 * @param space
	 * @throws RemoteException If there is a connection error
	 */
	public void generateTasks(Space space) throws RemoteException;
	/**
	 * Collects the results of execution of tasks in a job
	 * @param space 			Computer Space to connect to
	 * @return					Result of executed Job
	 * @throws RemoteException	If there is a connection error
	 */
	public Object collectResults(Space space) throws RemoteException;
}
