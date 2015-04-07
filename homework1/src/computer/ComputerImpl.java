package computer;

import java.rmi.RemoteException;

import api.Computer;
import api.Task;

public class ComputerImpl implements Computer{

	@Override
	public <T> T execute(Task<T> task) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

}
