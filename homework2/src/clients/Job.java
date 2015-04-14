package clients;

import java.rmi.RemoteException;

import api.Space;

public interface Job<V> {
	// Where the implementor specifies how the problem is broken up into Tasks.
	public void generateTasks(Space space) throws RemoteException; //
	// Where the implementor recombines the results from each Task into the final result.
	public V collectResults(Space space) throws RemoteException;
}
