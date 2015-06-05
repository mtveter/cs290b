package system;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
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
	
	protected ComputerImplCores(String id, String domainName) throws RemoteException, MalformedURLException, NotBoundException {
		super(id, runscores, prefetch, domainName);
		//this.runscores=true;
		//this.prefetch=prefetch;
	}
	
	public boolean runsCores(){
		return true;
	}
	
	@Override
	public Result<?> sendResult() throws RemoteException, InterruptedException {
		Result<?> result = results.take();
		return result;
	}
	
	
	/*public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException{
		// If no argument is passed, then connect to local host, otherwise to IPv4 specified 
		String domainName;
		if(args.length > 0){
			domainName = args[0];
		}
		else{domainName = "localhost";}

		// Construct and set a security manager
		System.setSecurityManager( new SecurityManager() );

		

		// Get url of remote space
		String url = "rmi://" + domainName + ":" + Space.PORT + "/" + Space.SERVICE_NAME;
		// Try to get remote reference from rmiregistry and register new computer to space
		

		space = (Space) Naming.lookup( url );
		Computer computer = new ComputerImplCores(space.createId(), domainName);
		((ComputerImplCores) computer).createThreads();
		space.register(computer);

		// Print acknowledgement
		System.out.println("ComputerCore started and registered at space " + domainName);
		//((ComputerImplCores) computer).run();
	}*/

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
