package computer;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import api.Computer;
import api.Task;

/**
 * Implementation of Computer interface to create RMI-Server
 */
public class ComputerImpl extends UnicastRemoteObject implements Computer{

	public ComputerImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public <T> T execute(Task<T> task) throws RemoteException {
		return task.execute();
	}
	
	/**
	 * Main class to run computer server
	 * 
	 * @param args			No arguments
	 * @throws Exception	Can throw exceptions due to errors with registry or remote server
	 */
	public static void main(String[] args) throws Exception
    {
        // Construct and set a security manager
        System.setSecurityManager( new SecurityManager() );

        // Instantiate a computer server object
        ComputerImpl computer = new ComputerImpl();

        // Construct an RMI-registry within this JVM using the default port
        Registry registry = LocateRegistry.createRegistry( Computer.PORT  );

        // Bind computer server in RMI-registry
        registry.rebind( Computer.SERVICE_NAME, computer );
        
        // Print acknowledgement
        System.out.println("Computer Server: Ready. on port " + Computer.PORT);
    
    }
}
