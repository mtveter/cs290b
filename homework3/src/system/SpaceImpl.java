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

	/** Generated serial identifier	 */
	private static final long serialVersionUID = 1L;

	private boolean isActive;

	private BlockingQueue<Computer>  registeredComputers = new LinkedBlockingQueue<Computer>();
	private BlockingQueue<Task<?>> receivedTasks = new LinkedBlockingQueue<Task<?>>();
	private List<Closure> receivedClosures = new ArrayList<Closure>();
	private BlockingQueue<Result<?>> completedResult = new LinkedBlockingQueue<Result<?>>();



	public SpaceImpl() throws RemoteException {
		super();
		this.isActive = false;
	}
	/**
	 * @see api.Space Space
	 */
	@Override
	public void putAll(List<Task<?>> taskList) throws RemoteException {
		System.out.println("SPACE: List of tasks received from Job");
		for(Task<?> task :  taskList) {
			try {
				// Generate closure for initial task
				Closure initialClosure = new Closure(ClientFibonacci.joinCounter, ClientFibonacci.N, "TOP", task);
				receivedClosures.add(initialClosure);
				receivedTasks.put(task);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//		System.out.println("SPACE: List of tasks is now put");
	}
	/**
	 * @see api.Space Space
	 */
	@Override
	public Result<?> take() throws RemoteException {
		try {
			return completedResult.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * @see api.Space Space
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
	 * @see api.Space Space
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
			// Check if there are any Closure objects to process
			if(!receivedClosures.isEmpty()) {
				
				// If there exits a Closure it tries to merge completed Closure with parent Closure
				mergeCompletedClosures();
				
				// If the Top Closure is completed the final result can be put in the blocking queue to be collected by Client
				if(isTopClosureCompleted()) {
					try {
						System.out.println("Added final result to results ");
						completedResult.put(receivedClosures.get(0).getAdder().getResult());
						receivedClosures.remove(0);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			// For Debug purpose
			printClosures();
			
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
		}
	}
	/**
	 * Takes completed Closure objects and merges them upward in hierarchy
	 */
	private void mergeCompletedClosures() {
		System.out.println("----RUNING COMPOSER---");

		boolean removeListIsEmpty = false;
		while(!removeListIsEmpty) {
			Iterator<Closure> iter = receivedClosures.iterator();
			List<Closure> removeList = new ArrayList<Closure>();
			// Iterates over all Closure objects
			while(iter.hasNext()){
//				System.out.println("Taking a closure");
				Closure c = iter.next();
				
				// Check if current Closure is completed
				if(c.isCompleted()){
					System.out.println(c.getTask().getId() + ": is completed and ready to be merged");
					String parent = c.getParentId();
					// Compares the current Closure with other Closure to find parent
					for (Closure c2 : receivedClosures){
						// Closure cannot pass its result to itself
						if(!c.getTask().equals(c2.getTask().getId())){
							// Check if current Closure's parent is the Closure in current comparison iteration
							if(parent.equals(c2.getTask().getId())){
								// Passes result of completed Closure to parent Closure
								c2.receiveResult(c.getAdder().getResult());
								System.out.println("ID: " + c2.getTask().getId() + " Received result from ID: " + c2.getTask().getId());
			
								removeList.add(c);
							}
						}
					}
				}
			}
			// Removes all Closure objects that are passed to parent, from Space's list 
			for(Closure c : removeList){
				receivedClosures.remove(c);
			}
			// If there are no more completed Closure objects to process --> break the while loop
			if(removeList.size() == 0){
				removeListIsEmpty = true;
			}
		}
	}
	/**
	 * Indicates whether the Top Closure is completed or not
	 * @return Returns true if the top Closure of the hierarchy is completed
	 */
	private boolean isTopClosureCompleted() {
		if(receivedClosures.get(0).getParentId().equals("TOP")){
			System.out.println("First closure is top");
			if(receivedClosures.get(0).isCompleted()){
				return true;
			}
		}
		return false;
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
			// Takes Task in the head of queue, allocates it to a computer, and executes the operation on the task
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
					System.out.println("Result is of type n=0 og n=1");
					// return to parent closure
					for(Closure c : receivedClosures){
//						System.out.println("task id "+c.getTask().getId()+"  result id  "+result.getId());
						if(c.getTask().equals(result.getId())){
							c.receiveResult(result);
						}
					}					
				}
				else {
					System.out.println("Result received did not have a valid Status");
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

	/**
	 * Print method to print all Closure object with their current join counter
	 */
	public void printClosures(){
		System.out.println();
		for(Closure c: receivedClosures){
			
			System.out.print(c.getTask().getId()+ ": " + c.getJoinCounter() + " // ");
			//System.out.println("Closure "+c.getParentId());
		}
		System.out.println();
	}
}
