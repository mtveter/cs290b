package api;

import java.io.Serializable;
import java.rmi.RemoteException;

public interface Task<T> extends Serializable{
	
	public Integer divide() throws RemoteException;
	
	public Integer conquer() throws RemoteException;
	
	public String getId(); 
}
