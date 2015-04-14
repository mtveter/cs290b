package system;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import api.Task;

public class ComputerImpl extends UnicastRemoteObject implements Computer{

	/**
	 * Generated Serial ID
	 */
	private static final long serialVersionUID = 8694827504541478153L;

	public ComputerImpl() throws RemoteException {
		super();
	}

	@Override
	public <V> V execute(Task<V> task) throws RemoteException {
		// TODO Auto-generated method stub
		return task.call();
	}
	
	public static void main(String[] args) {
		
	}

}
