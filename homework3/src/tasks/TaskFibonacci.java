package tasks;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import api.Task;

public class TaskFibonacci implements Task<Integer>, Serializable{
	
	/** Generated serial ID */
	private static final long serialVersionUID = 1L;
	/** Identifier of task */
	private String id;
	/** */
	private int n;

	public TaskFibonacci(int n) {
		this.n = n;
	}

	@Override
	public Integer divide() throws RemoteException {
		// TODO Auto-generated method stub
		Integer T = null;
		if (n < 2 && n >=0 ) {
			T = n;
			return T;
		}
		else if(n > 2){
			List<Task<Integer>> subTaskList = new ArrayList<Task<Integer>>();
			TaskFibonacci subTask1 = new TaskFibonacci(n-1);
			TaskFibonacci subTask2 = new TaskFibonacci(n-2);
			subTaskList.add(subTask1);
			subTaskList.add(subTask2);
		}
		return T;
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
