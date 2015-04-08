package computer;

import java.rmi.RemoteException;
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
		return null;
	}

}
