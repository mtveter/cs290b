package api;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * Interface of a Computer 'Server'
 */
public interface Computer extends Remote, Serializable{
	/** Default service name of registry */
	public static String SERVICE_NAME = "Computer";
	/** Default port number of RMI */
	public static int PORT = 1099;
	
	/**
	 * @param	task {@link Task}Task to be executed
	 * @return 		A type T 
	 * @throws 		RemoteException if there is a communication error
	 */
	public <T> T execute(Task<T> task) throws RemoteException;
}
