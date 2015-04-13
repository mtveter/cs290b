package system;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import api.Result;
import api.Space;
import api.Task;

public class SpaceImpl extends UnicastRemoteObject implements Space{

	/**
	 * Generated Serial ID
	 */
	private static final long serialVersionUID = 4974914619819929236L;

	protected SpaceImpl() throws RemoteException {
		super();
	}

	@Override
	public void putAll(List<Task> taskList) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Result take() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void exit() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void register(Computer computer) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

}
