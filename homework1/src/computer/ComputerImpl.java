package computer;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import api.Computer;
import api.Task;

public class ComputerImpl extends UnicastRemoteObject implements Computer{

	public ComputerImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public <T> T execute(Task<T> task) throws RemoteException {
		// TODO Auto-generated method stub
		return task.execute();
	}
	
	public static void main(String[] args) throws Exception
    {
        // construct & set a security manager (unnecessary in this case)
        System.setSecurityManager( new SecurityManager() );

        // instantiate a server object
        ComputerImpl computer = new ComputerImpl(); // can throw RemoteException

        // construct an rmiregistry within this JVM using the default port
        Registry registry = LocateRegistry.createRegistry( Computer.PORT  );

        // bind server in rmiregistry. Can throw exceptions. See api.
        registry.rebind( Computer.SERVICE_NAME, computer );

        System.out.println("Computer Server: Ready. on port " + Computer.PORT);
    
    }
}
