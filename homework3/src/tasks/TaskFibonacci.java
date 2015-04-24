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

	public TaskFibonacci(int n,String id) {
		this.n = n;
		this.id = id;
	}

	@Override
	public Result<?> call() throws RemoteException {
		long taskStartTime = System.currentTimeMillis();
		// TODO Auto-generated method stub
		Result<?> result = null;
		System.out.println("running call");
		if (n < 2 && n >=0 ) {
			System.out.println("N = " + n);
			int taskReturnValue = n;
			long taskEndTime = System.currentTimeMillis();
			long taskRunTime = taskEndTime - taskStartTime;
			result = new Result(taskReturnValue, taskRunTime, this.getId());
			return result;
		}
		else if(n >=2 ){
			System.out.println("N = " + n);
			List<Closure> childClosures = new ArrayList<Closure>();
			TaskFibonacci subTask1 = new TaskFibonacci(n-1,this.id+(n-1));
			TaskFibonacci subTask2 = new TaskFibonacci(n-2,this.id+(n-2));
			Closure closure1 = null;
			Closure closure2 = null;
			
			if( n > 3) {
				closure1 = new Closure(2, this.n-1, this.id, subTask1);
				closure2 = new Closure(2, this.n-2, this.id, subTask2);
			}
			else if(n == 3) {
				closure1 = new Closure(2, this.n-1, this.id, subTask1);
				closure2 = new Closure(1, this.n-2, this.id, subTask2);
			}
			else if(n == 2) {
				closure1 = new Closure(1, this.n-1, this.id, subTask1);
				closure2 = new Closure(1, this.n-2, this.id, subTask2);
			}
			
			childClosures.add(closure1);
			childClosures.add(closure2);
			long taskEndTime = System.currentTimeMillis();
			long taskRunTime = taskEndTime - taskStartTime;
			result = new Result(childClosures, taskRunTime, this.getId());

		}
		return result;
	}

	@Override
	public String getId() {
		return this.id;
	}
	public String toString(){
		return "fibonachi";
	}
	@Override
	public boolean equals(Object o){
		if(this.id.equals((String) o)) {
			return true;
		}
		return false;
	}
}
