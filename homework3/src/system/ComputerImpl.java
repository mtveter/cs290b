package system;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import api.Result;
import api.Space;
import api.Task;

public class ComputerImpl extends UnicastRemoteObject implements Computer {

	/** Generated serial ID	 */
	private static final long serialVersionUID = -3092303569928556422L;
	/**
	 * @throws RemoteException If there is a connection error
	 */
	protected ComputerImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * @see system.Computer Computer
	 */
	@Override
	public Result<?> execute(Task<?> task) throws RemoteException {
//		System.out.println("Executing task");
//		System.out.println(task.toString());
		
		Result<?> result = task.call();
		
		return result;
	}
	/**
	 * @see system.Computer Computer
	 */
	@Override
	public void exit() throws RemoteException {
		System.exit(0);
	}
	/**
	 * Main method for creating a Computer
	 * @param args IP-address of remote server, 'localhost' is default if no argument is passed
	 */
	public static void main(String[] args) {
		// If no argument is passed, then connect to local host, otherwise to IPv4 specified 
		
		String domainName;
		if(args.length > 0){
			domainName = args[0];
		}
		else{domainName = "localhost";}
		
		 // Construct and set a security manager
		System.setSecurityManager( new SecurityManager() );
		
		
		
		// Get url of remote space
		String url = "rmi://" + domainName + ":" + Space.PORT + "/" + Space.SERVICE_NAME;
		// Try to get remote reference from rmiregistry and register new computer to space
		try {
			Space space = (Space) Naming.lookup( url );
			Computer computer = new ComputerImpl();
			space.register(computer);
		}catch (  MalformedURLException malformedException) {
		    System.err.println("Bad URL: " + malformedException);
		  }
		catch (  NotBoundException notBoundException) {
		    System.err.println("Not Bound: " + notBoundException);
		  }
		catch (  RemoteException remoteException) {
		    System.err.println("Remote Exception: " + remoteException);
		  }
		
		// Print acknowledgement
		System.out.println("Computer started and registered at space " + domainName);
	}

}
