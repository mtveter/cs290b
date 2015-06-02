package system;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import api.Result;
import api.Space;
import api.Task;

public class ComputerImplCores extends ComputerImpl implements Runnable {

	
	/** Generated Serial ID */
	private static final long serialVersionUID = 2894424877938702903L;
	private transient List<ComputeThread> threads= new ArrayList<ComputerImplCores.ComputeThread>();
	private transient BlockingQueue<Result<?>> results= new LinkedBlockingQueue<Result<?>>();
	private transient BlockingQueue<Task<?>> tasks= new LinkedBlockingQueue<Task<?>>();
	private int cores;
	private static boolean runscores;
	private static boolean prefetch;
	static Space space;
	
	protected ComputerImplCores(int id) throws RemoteException {
		super(id, runscores, prefetch);
	}
	
	public boolean runsCores(){
		return true;
	}
	
	@Override
	public Result<?> sendResult() throws RemoteException, InterruptedException {
		Result<?> result = results.take();
		return result;
	}
	
	public void getTask(Task<?> task){
		tasks.add(task);
	}
	
	public void createThreads(){
		//create threads running
		cores = Runtime.getRuntime().availableProcessors();
		for(int i = 0; i < cores; i++){
			ComputeThread thread = new ComputeThread(i);
			threads.add(thread);
			thread.start();
		}
	}
	
	private class ComputeThread extends Thread {

		private final int id;
		public ComputeThread(int id){
			this.id = id;
		}
		public long getId(){
			return this.id;
		}

		@Override
		public void run() {
			while(true) try {
				Task<?> task = tasks.take();
				Result<?> result;
				try {
					result = task.call();
					results.put(result);
				} catch (RemoteException e) {
					System.out.println("There was a remote exception when trying to execute task");
				}
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}
	}








	

}
