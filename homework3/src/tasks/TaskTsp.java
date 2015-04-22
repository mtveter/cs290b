package tasks;

import java.rmi.RemoteException;
import java.util.List;

import api.Task;

public class TaskTsp implements Task<List<Integer>>{

	/**	Generated serial ID */
	private static final long serialVersionUID = 1L;
	/** Identifier of task */
	private String id;

	@Override
	public Integer divide() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer conquer() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getId() {
		return this.id;
	}

}
