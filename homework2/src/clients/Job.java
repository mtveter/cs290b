package clients;

import java.rmi.RemoteException;

import api.Space;

public interface Job<T> {
	public void generateTasks(Space space) throws RemoteException;
	
	public T collectResults(Space space) throws RemoteException;
	
	public Object getAllResults();
}
