package tasks;

import java.rmi.RemoteException;

import api.Task;

public class TaskFibonacci implements Task<Integer>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** Identifier of task */
	private String id;

	public TaskFibonacci(int n) {
		
	}

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
