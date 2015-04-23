package api;

import java.io.Serializable;
import java.rmi.RemoteException;

public interface Task<T> extends Serializable{
	
	public <V> V call() throws RemoteException;
	
	public String getId(); 
}
