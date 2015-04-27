package clients;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import tasks.TaskFibonacci;
import api.Space;
import api.Task;


/**
 * Implementation of {@link Job} interface for the Fibonacci recursion calculation
 */
public class FibonacciJob implements Job {
	
	/** The N'th fibonacci number*/
	private int n;
	/**	Start time of job in milliseconds */
	private long jobStartTime;
	/** Result of computed job */
	private int finalValue;

	public FibonacciJob(int n) {
		this.n = n;
	}
	/**
	 * @see clients.Job Job
	 */
	@Override
	public void generateTasks(Space space) throws RemoteException {
		List<Task<?>> taskList = new ArrayList<Task<?>>();
		Task<Integer> taskFibo = new TaskFibonacci(this.n, ""+n);
		taskList.add(taskFibo);
		this.jobStartTime = System.currentTimeMillis();
		space.putAll(taskList);
	}
	/**
	 * @see clients.Job Job
	 */
	@Override
	public Integer collectResults(Space space) throws RemoteException {
		this.finalValue = (Integer) space.take().getTaskReturnValue();
		System.out.println("Elapsed Time=" + (System.currentTimeMillis() - jobStartTime));
		return finalValue;
	}
}
