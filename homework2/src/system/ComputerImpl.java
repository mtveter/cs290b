package system;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ComputerImpl extends UnicastRemoteObject implements Computer{

	/**
	 * Generated Serial ID
	 */
	private static final long serialVersionUID = 8694827504541478153L;

	protected ComputerImpl() throws RemoteException {
		super();
	}

}
