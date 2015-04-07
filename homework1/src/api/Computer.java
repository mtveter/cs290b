package api;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Computer extends Remote, Serializable{
	public <T> T execute(Task<T> task) throws RemoteException;
}
