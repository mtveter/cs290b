package system;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;





import api.Result;
import api.Space;
import api.Task;

public class ComputerImpl extends UnicastRemoteObject implements Computer {



	/** Generated serial ID	 */
	private static final long serialVersionUID = -3092303569928556422L;
	private static int id;
	private transient List<ComputeThread> threads= new ArrayList<ComputerImpl.ComputeThread>();
	private transient BlockingQueue<Result<?>> results= new LinkedBlockingQueue<Result<?>>();
	private transient BlockingQueue<Task<?>> tasks= new LinkedBlockingQueue<Task<?>>();
	int cores;
	private boolean multicore;
	private boolean amerlioration;
	static Space space;
	private static int buffer=5;

	/**
	 * @throws RemoteException If there is a connection error
	 */
	protected ComputerImpl(int id, boolean mulitcore,boolean amerlioration) throws RemoteException {
		super();
		this.id=id;
		this.amerlioration=amerlioration;
		this.multicore=mulitcore;
		

		
		// TODO Auto-generated constructor stub
	}
	/**
	 * @see system.Computer Computer
	 */
	@Override
	public Result<?> execute(Task<?> task) throws RemoteException {
		//		System.out.println("Executing task");
		//		System.out.println(task.toString());

		Result<?> result = task.call();

		return result;
	}
	
	public boolean useAmerlioration(){
		return this.amerlioration;
	}
	
	
	
	/**
	 * @see system.Computer Computer
	 */
	@Override
	public void exit() throws RemoteException {
		System.exit(0);
	}
	/**
	 * Main method for creating a Computer
	 * @param args IP-address of remote server, 'localhost' is default if no argument is passed
	
	 * @throws RemoteException 
	 * @throws MalformedURLException 
	 * @throws NotBoundException 
	 */
	public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException{
		// If no argument is passed, then connect to local host, otherwise to IPv4 specified 
		String domainName;
		boolean multicore;
		boolean prefetch;
		if(args.length > 0){
			domainName = args[0];
			multicore=(args.length > 1)? Boolean.parseBoolean(args[1]): true;
			prefetch= (args.length > 2)? Boolean.parseBoolean(args[2]): true;
			
		}
		else{
		domainName = "localhost";
		multicore=true;
		prefetch= false;
		}

		// Construct and set a security manager
		System.setSecurityManager( new SecurityManager() );

		//Runtime.getRuntime().availableProcessors();

		// Get url of remote space
		String url = "rmi://" + domainName + ":" + Space.PORT + "/" + Space.SERVICE_NAME;
		// Try to get remote reference from rmiregistry and register new computer to space
		Space space;

		space = (Space) Naming.lookup( url );
		
		
		Computer computer = new ComputerImpl(1,multicore,prefetch);
		((ComputerImpl) computer).createThreads();
		space.register(computer);

		// Print acknowledgement
		System.out.println("Computer started and registered at space " + domainName);
	}
	
	
	public int getId() {
		// TODO Auto-generated method stub
		return this.id;
	}
	@Override
	public void getTask(Task<?> task) throws RemoteException {
		tasks.add(task);
		
	}
	public boolean bufferAvailable(){
		if(tasks.size()<=4 && useAmerlioration() ){
			return true;
		}else{
			return false;
		}
		
	}
	public void createThreads(){
		//create threads running f
				cores =Runtime.getRuntime().availableProcessors();
				for(int i=0; i<cores; i++){
					ComputeThread thread = new ComputeThread(i);
					threads.add(thread);
					thread.start();
				}
	}
	
	
	@Override
	public Result<?> sendResult() throws RemoteException, InterruptedException {
		// TODO Auto-generated method stub
		System.out.println("Sendt result back");
		Result<?> r = results.take();
		return r;
	}
	@Override
	public boolean runsCores() throws RemoteException {
		// TODO Auto-generated method stub
		return this.multicore;
	}
	
	
	private class ComputeThread extends Thread implements Computer {

		private  int id;
		
		public ComputeThread(int id){
			this.id = id;
		}
		

		@Override
		public void run() {
			
			while(true) try {
				System.out.println("thread "+getId()+" running");
				Task<?> task = tasks.take();
				System.out.println("Took "+task.getId()+" tasks");
				Result<?> result;
				try {
					
					result = task.call();
					results.put(result);
					System.out.println("Result put to queue");
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//Log.debug("-"+id+"- "+task+" = "+result);



			}catch(InterruptedException e){	}
			
			
			
			
		}
		@Override
		public Result<?> execute(Task<?> task) throws RemoteException {
			// TODO Auto-generated method stub
			Result<?> result = task.call();

			return result;
			
		}
		@Override
		public void exit() throws RemoteException {
			// TODO Auto-generated method stub
			
		}
		
		
		@Override
		public void getTask(Task<?> task) throws RemoteException {
			// TODO Auto-generated method stub
			
		}


		@Override
		public Result<?> sendResult() throws RemoteException, InterruptedException {
			// TODO Auto-generated method stub
			System.out.println("Sendt result back");
			Result<?> r = results.take();
			return r;
		}

		
		//TODO: needs to be set as argument
		@Override
		public boolean runsCores() throws RemoteException {
			// TODO Auto-generated method stub
			return false;
		}


		@Override
		public boolean bufferAvailable() throws RemoteException {
			// TODO Auto-generated method stub
			return false;
		}
	}


	
	

}
