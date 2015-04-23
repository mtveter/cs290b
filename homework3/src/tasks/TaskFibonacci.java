package tasks;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import system.Closure;
import api.Result;
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
	public Result<?> call() throws RemoteException {
		long taskStartTime = System.currentTimeMillis();
		// TODO Auto-generated method stub
		Result<?> result = null;
		if (n < 2 && n >=0 ) {
			int taskReturnValue = n;
			long taskEndTime = System.currentTimeMillis();
			long taskRunTime = taskEndTime - taskStartTime;
			result = new Result(taskReturnValue, taskRunTime, this.getId());
			return result;
		}
		else if(n > 2){
			List<Closure> childClosures = new ArrayList<Closure>();
			TaskFibonacci subTask1 = new TaskFibonacci(n-1);
			TaskFibonacci subTask2 = new TaskFibonacci(n-2);
			Closure closure1 = new Closure(2, this.n-1, this.id, subTask1);
			Closure closure2 = new Closure(2, this.n-2, this.id, subTask2);
			
			childClosures.add(closure1);
			childClosures.add(closure2);
			long taskEndTime = System.currentTimeMillis();
			long taskRunTime = taskEndTime - taskStartTime;
			result = new Result(taskRunTime, this.getId(), childClosures);

		}
		return result;
	}

	@Override
	public String getId() {
		return this.id;
	}
}
