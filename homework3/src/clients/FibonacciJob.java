package clients;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import tasks.TaskFibonacci;
import api.Result;
import api.Space;
import api.Task;


/**
 * Implementation of {@link Job} interface for the Fibonacci recursion calculation
 */
public class FibonacciJob implements Job {
	
	private int n;
	/**	Start time of job in milliseconds */
	private long jobStartTime;
	/** Result of computed job */
	private int finalValue;

	public FibonacciJob(int n) {
		this.n = n;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void generateTasks(Space space) throws RemoteException {
		List<Task> taskList = new ArrayList<Task>();
		Task<Integer> taskFibo = new TaskFibonacci(this.n);
		taskList.add(taskFibo);
		this.jobStartTime = System.currentTimeMillis();
		space.putAll(taskList);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer collectResults(Space space) throws RemoteException {
		Result<Integer> result = (Result<Integer>) space.take();
		this.finalValue = result.getTaskReturnValue();
		System.out.println("Elapsed Time=" + (System.currentTimeMillis() - jobStartTime));
		return finalValue;
	}
}
