package api;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import system.Computer;
import system.Shared;

/**
 *
 * @author Peter Cappello
 */
public interface Space extends Remote 
{
	/** Default port number of RMI registry	 */
    public static int PORT = 8001;
    /** Service name of RMI registry */
    public static String SERVICE_NAME = "Space";

    /**
     * Tasks a list of tasks from and puts them in a local BlockingQueue
     * @param taskList			List of Task objects to adding to queue	
     * @throws RemoteException	Communication-related exception that may occur during the execution of a remote method call
     */
    void putAll ( List<Task<?>> taskList ) throws RemoteException;
    /**
     * 
     * @return Final result of execution of a Job
     * @throws RemoteException	Communication-related exception that may occur during the execution of a remote method call
     */
    Result<?> take() throws RemoteException;
    /**
     * Terminate the Space server
     * @throws RemoteException	Communication-related exception that may occur during the execution of a remote method call
     */
    void exit() throws RemoteException;
    /**
     * Register a computer to the Space's list of available Computer objects
     * @param computer 			Computer object that performs computations
     * @throws RemoteException	Communication-related exception that may occur during the execution of a remote method call
     */
    void register( Computer computer ) throws RemoteException;
	
    void setShared(Shared sharedObject) throws RemoteException;
    
    Shared getShared() throws RemoteException;
}
