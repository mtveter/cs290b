package tasks;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import system.Closure;
import api.Result;
import api.Task;

public final class TaskFibonacci implements Task<Integer>, Serializable{
	
	/** Generated serial ID */
	private static final long serialVersionUID = 1L;
	/** Identifier of task */
	private String id;
	/** The N'th fibonacci number*/
	private int n;

	public TaskFibonacci(int n, String id) {
		this.n = n;
		this.id = id;
	}
	/**
	 * @see api.Task Task
	 */
	@Override
	public Result<?> call() throws RemoteException {
		long taskStartTime = System.currentTimeMillis();
		
		Result<?> result = null;
		System.out.println("running call");
		
		// If Task is of base case where n=0 or n=1
		if (n < 2 && n >=0 ) {
			System.out.println("N = " + n);
			int taskReturnValue = n;
			long taskEndTime = System.currentTimeMillis();
			long taskRunTime = taskEndTime - taskStartTime;
			result = new Result(taskReturnValue, taskRunTime, this.getId());
			return result;
		}
		// If Task is not of base case, and must create recursive Task objects
		else if(n >=2 ){
			System.out.println("N = " + n);
			List<Closure> childClosures = new ArrayList<Closure>();
			
			/* Instantiates Task for n-1 and n-2 children of Closure
			 * N is appended to identifier of parent Task to represent identifier of child Task
			 */
			TaskFibonacci subTask1 = new TaskFibonacci(n-1,this.id+(n-1));
			TaskFibonacci subTask2 = new TaskFibonacci(n-2,this.id+(n-2));
			
			Closure closure1 = null;
			Closure closure2 = null;
			
			
			if( n > 3) {
				/* If n > 3, both Closure children is set with joinCounter=2
				 * Since both Closure children will wait for 2 arguments
				 */
				closure1 = new Closure(2, this.id, subTask1);
				closure2 = new Closure(2, this.id, subTask2);
			}
			else if(n == 3) {
				/* If n == 3, 1 Closure child is set with joinCounter=2, and the other with joinCounter=1 
				 * Since one child will wait for 2 arguments, while the other for only 1 argument
				 */
				closure1 = new Closure(2, this.id, subTask1);
				closure2 = new Closure(1, this.id, subTask2);
			}
			else if(n == 2) {
				/* If n == 2, both Closure children is set with joinCounter=1
				 * Since both Closure children will wait for only 1 argument
				 */
				closure1 = new Closure(1, this.id, subTask1);
				closure2 = new Closure(1, this.id, subTask2);
			}
			// Add Closure children to list and passes it to a new Result object to be returned
			childClosures.add(closure1);
			childClosures.add(closure2);
			long taskEndTime = System.currentTimeMillis();
			long taskRunTime = taskEndTime - taskStartTime;
			result = new Result<>(childClosures, taskRunTime, this.getId());

		}
		return result;
	}
	
	/**
	 * @see api.Task Task
	 */
	@Override
	public String getId() {
		return this.id;
	}
	public String toString(){
		return "fibonacci";
	}
	/**
	 * {@inheritDoc}}
	 */
	@Override
	public boolean equals(Object o){
		if(this.id.equals((String) o)) {
			return true;
		}
		return false;
	}
	@Override
	public Type getType() {
		return Type.FIB;
	}
}
