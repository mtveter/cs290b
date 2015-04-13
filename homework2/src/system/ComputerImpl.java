package system;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ComputerImpl extends UnicastRemoteObject implements Computer{

	protected ComputerImpl() throws RemoteException {
		super();
	}

}
