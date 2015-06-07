package api;

import java.io.Serializable;
import java.rmi.RemoteException;

import system.Computer;
/**
 * Task interface for implementing Task objects
 * @param <T> 	Value to return
 */
public interface Task<T> extends Serializable{

	public static enum Type{FIB,TSP}
	/**
	 * @param <V> 				Object
	 * @return	V				Object to call on
	 * @throws 	RemoteException	Communication-related exception that may occur during the execution of a remote method call
	 */
	public <V> V call() throws RemoteException;
	/**
	 * @return 					Identifier of Task
	 */
	public String getId(); 
	
	public Type getType();
	
	public int getN();
	
	public Computer getComputer();

	public void setComputer(Computer computer);
	
	public void setRecLimit(int i);
	
	public int getLevel();
	
}
