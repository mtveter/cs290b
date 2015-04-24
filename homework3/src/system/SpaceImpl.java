package system;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import clients.ClientFibonacci;
import api.Result;
import api.Result.Status;
import api.Space;
import api.Task;
import system.Closure;

public class SpaceImpl extends UnicastRemoteObject implements Space {

	private boolean isActive;

	private BlockingQueue<Computer>  registeredComputers = new LinkedBlockingQueue<Computer>();
	private BlockingQueue<Task> receivedTasks = new LinkedBlockingQueue<Task>();
	private BlockingQueue<Result> receivedResults = new LinkedBlockingQueue<Result>();
	private List<Closure> receivedClosures = new ArrayList<Closure>();
	private BlockingQueue<Result> completedResult = new LinkedBlockingQueue<Result>();



	public SpaceImpl() throws RemoteException {
		super();
		this.isActive = false;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void putAll(List<Task<?>> taskList) throws RemoteException {
		// TODO Need to be fixed to work with HW3?
		System.out.println("SPACE: List of tasks received from Job");
		for(Task<?> task :  taskList) {
			try {
				// Generate closure for initial task
				Closure initialClosure = new Closure(ClientFibonacci.joinCounter, ClientFibonacci.N, "TOP", task);
				receivedTasks.put(task);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//		System.out.println("SPACE: List of tasks is now put");
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Result<?> takeCompleted() throws RemoteException {
		try {
			return completedResult.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	public Result<?> take() throws RemoteException {
		try {
			return receivedResults.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void exit() throws RemoteException {
		// Pop each registered computer from head of queue and exit each until all are shut down
		while(!registeredComputers.isEmpty()) {
			Computer computer;
			try {
				computer = registeredComputers.take();
				computer.exit();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Exit space server
		System.exit(0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void register(Computer computer) throws RemoteException {
		registeredComputers.add(computer);
	}

	/**
	 * Checks if space is running
	 * @return True if space is running, false if not
	 */
	public boolean isActive() {
		return this.isActive;
	}
	/**
	 * Initiates the Space, sets it active and runs a new ComputerProxy thread
	 */
	private void runComputerProxy() {
		this.isActive = true; 
		// Thread runs as long as Space is active
		while(isActive) {
			
			//List<Closure> remove
			Iterator<Closure> i=receivedClosures.iterator();
			while(i.hasNext()){
			Closure c= i.next();

				if(c.isCompleted()){
					
					String parent = c.getParentId();
					for (Closure c2 : receivedClosures){
						
						if(!c.getTask().equals(c2.getTask().getId())){
							if(parent == c2.getParentId()){
								c2.receiveResult(c.getAdder().getResult());
								i.remove();							
							}
						}
					}
				}
			}
			
			if(receivedClosures.size()>0 && receivedClosures.get(0).getParentId().equals("TOP")){
				System.out.println("First closure is top");
				if(receivedClosures.get(0).isCompleted()){
					try {
						System.out.println("Added to results ");
						completedResult.put(receivedClosures.get(0).getAdder().getResult());
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}

			Task<?> task = null;
			try {
				task = receivedTasks.take();
				System.out.println("SPACE: Task is taken");
				ComputerProxy proxy = new ComputerProxy(task);
				proxy.run();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("closure list size"+receivedClosures.size());
			if(receivedClosures.size()==14){
				printClosures();
			}

		}
	}
	/**
	 * Thread that allocate tasks to computers and execute computation
	 */
	public class ComputerProxy implements Runnable{
		Task<?> task;

		public ComputerProxy(Task<?> task) {
			this.task = task;
		}
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			System.out.println("SPACE: Proxy is running");
			// Takes task in head of queue, allocates it to a computer, and execute operation
			try {

				Computer computer = registeredComputers.take();
				System.out.println("SPACE: Computer is taken");

				Result<?> result = (Result<?>) computer.execute(task);
				System.out.println("SPACE: Result is received from Computer");

				System.out.println("this is result: "+result.toString());
				//receivedResults.put(result);
				if(result.getStatus().equals(Status.WAITING)) {
					List<Closure> closures = result.getChildClosures();
					// Add Closures from
					for (Closure closure : closures) {
						receivedClosures.add(closure);
						receivedTasks.put(closure.getTask());
					}
				}
				else if(result.getStatus().equals(Status.COMPLETED)) {
					System.out.println("Result is of type n=0 og n = 1");
					//return to parent closure
					for(Closure c: receivedClosures){
						System.out.println("task id "+c.getTask().getId()+"  restult id  "+result.getId());
						if(c.getTask().equals(result.getId())){

							c.receiveResult(result);
						}
					}
				}
				else {
					System.out.println("Result received dit not have a valid Status");
				}
				registeredComputers.put(computer);

			} catch (RemoteException e) {
				// If there's a RemoteException, task is put back in the queue
				try {
					receivedTasks.put(task);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * Main method for creating Space
	 * @param args Not needed
	 * @throws RemoteException If there is a connection error
	 */
	public static void main(String[] args) throws RemoteException {
		// Construct and set a security manager
		System.setSecurityManager( new SecurityManager() );

		// Instantiate a computer server object
		SpaceImpl space = new SpaceImpl();

		// Construct an RMI-registry within this JVM using the default port
		Registry registry = LocateRegistry.createRegistry( Space.PORT  );

		// Bind Compute Space server in RMI-registry
		registry.rebind( Space.SERVICE_NAME, space);

		// Print acknowledgement
		System.out.println("Computer Space: Ready. on port " + Space.PORT);

		space.runComputerProxy();
	}

	public void printClosures(){

		System.out.println();
		for(Closure c: receivedClosures){
			System.out.print(c.getTask().getId()+"   ");
			//System.out.println("Closure "+c.getParentId());
		}
	}

}
