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
import java.util.concurrent.TimeUnit;

import clients.ClientFibonacci;
import api.Result;
import api.Result.Status;
import api.Space;
import api.Task;
import api.Task.Type;
import system.Closure;
import tasks.TaskTsp;

public class SpaceImpl extends UnicastRemoteObject implements Space {

	/** Generated serial identifier	 */
	private static final long serialVersionUID = 1L;
	/** Variable describing state of Space */
	private boolean isActive;
	/** Describes if Space implementation has feature to run some specified Task objects in Space */
	private boolean hasSpaceRunnableTasks;

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
				Closure initialClosure;
				if(task.getType() == Type.FIB){
					// Generate closure for initial task
					initialClosure = new Closure(ClientFibonacci.joinCounter, "TOP", task);
					receivedClosures.add(initialClosure);
					receivedTasks.put(task);
				}
				else if(task.getType() == Type.TSP){
					// Joincounter
					TaskTsp taskTsp = (TaskTsp) task;
					initialClosure = new Closure(taskTsp.getPartialCityList().size(),"TOP",task);
					receivedClosures.add(initialClosure);
					receivedTasks.put(task);
				}	
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
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
		System.out.println("New computer registerd");
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
	 * @param hasSpaceRunnableTasks2 
	 */
	private void runComputerProxy(boolean hasSpaceRunnableTasks) {
		this.hasSpaceRunnableTasks = hasSpaceRunnableTasks;

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
						completedResult.put(receivedClosures.get(0).getAdder().getResult());
						receivedClosures.remove(0);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
			Task<?> task = null;
			try {
				task = receivedTasks.take();
				if(hasSpaceRunnableTasks) {
					/* Implementation of Space-Runnable tasks*/
					
					// IF Fibonacci task is a base case, then compute result locally on Space
					if(task.getType() == Type.FIB && task.getN() < 2) {
						LocalWorker worker = new LocalWorker(task);
						worker.run();
					}
					// IF TSP task is a base case, then compute result locally on Space
					else if(task.getType() == Type.TSP && task.getN() > TaskTsp.RECURSIONLIMIT) {
						LocalWorker worker = new LocalWorker(task);
						worker.run();
					}
					// Otherwise send task to computer
					else{
						ComputerProxy proxy = new ComputerProxy(task);
						proxy.run();
					}
				}
				else{
					ComputerProxy proxy = new ComputerProxy(task);
					proxy.run();

				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * Takes completed Closure objects and merges them upward in hierarchy
	 */
	private void mergeCompletedClosures() {

		boolean removeListIsEmpty = false;
		while(!removeListIsEmpty) {
			Iterator<Closure> iter = receivedClosures.iterator();
			List<Closure> removeList = new ArrayList<Closure>();
			// Iterates over all Closure objects
			while(iter.hasNext()){
				
				Closure c = iter.next();

				// Check if current Closure is completed
				if(c.isCompleted()){
					String parent = c.getParentId();
					// Compares the current Closure with other Closure to find parent
					for (Closure c2 : receivedClosures){
						// Closure cannot pass its result to itself
						if(!c.getTask().equals(c2.getTask().getId())){
							// Check if current Closure's parent is the Closure in current comparison iteration
							if(parent.equals(c2.getTask().getId())){
								// Passes result of completed Closure to parent Closure
								c2.receiveResult(c.getAdder().getResult());
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
			
			if(receivedClosures.get(0).isCompleted()){
				return true;
			}
		}
		return false;
	}
	private class LocalWorker implements Runnable{
		Task<?> task;

		public LocalWorker(Task<?> task) {
			this.task = task;
		}

		@Override
		public void run() {
			Result<?> result;
			try {
				result = task.call();

				handleResult(result);
			} catch (RemoteException e) {
				e.printStackTrace();
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

			Computer computer = null;
			try {
				computer = registeredComputers.take();

			} catch (InterruptedException e2) {
				e2.printStackTrace();
			}



			try {
				if(!computer.runsCores()){

					/**
					 * Checks if the computer runs buffer, and also if there are enough waiting tasks, so the system 
					 * doesn't wait if there's not enough tasks.
					 */
					if(computer.bufferAvailable() && receivedTasks.size()>(computer.bufferSize())){
						computer.getTask(task);

						int available =computer.bufferSize()+computer.coreCount();

						// Sends tasks to computer
						for (int i = 0; i < available; i++) {

							computer.getTask(receivedTasks.take());


						}

						/* Receives all the tasks from space, but they are processed as fast as they come in 
						 * to prevent unnecessary delay */
						for (int i = 0; i < available+1; i++) {
							Result<?> r = computer.sendResult();

							processResult(r);

						}
						registeredComputers.put(computer);

					}else{
						// if theres no multicore and no prefetching we just use the old execute method
						Result<?> result = (Result<?>) computer.execute(task);

						processResult(result);
						registeredComputers.put(computer);
					}


				}
				/* this is if the computer runs multiple cores */
				else{

					computer.getTask(task);
					
					/**
					 * Checks if the computer runs buffer, and also if there are enough waiting tasks, so the system 
					 * doesn't wait if there's not enough tasks.
					 */

					if(computer.bufferAvailable() && receivedTasks.size()>(computer.bufferSize()+computer.coreCount())){
						int available =computer.bufferSize()+computer.coreCount();
						
						for (int i = 0; i < available; i++) {
							computer.getTask(receivedTasks.take());

						}
						for (int i = 0; i < available+1; i++) {
							Result<?> r = computer.sendResult();
							processResult(r);
							}	
						
						registeredComputers.put(computer);

					}else if (receivedTasks.size()>(computer.coreCount())){

						int available =computer.coreCount();
						
						for (int i = 0; i < available; i++) {
							computer.getTask(receivedTasks.take());

						}
						for (int i = 0; i < available+1; i++) {
							Result<?> r = computer.sendResult();

							processResult(r);

						}	
		
						registeredComputers.put(computer);
						//printClosures();

					}
					else{
						/* if the computer can take more tasks, but there are not any more waiting */
						Result<?> r = computer.sendResult();
						
						processResult(r);

						registeredComputers.put(computer);
					}



				}

			} catch (RemoteException e) {
				// If there's a RemoteException, task is put back in the queue
				try {
					receivedTasks.put(task);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				System.out.println("There was a remote exception: A computer might have been terminated");
				// Unregister the faulty computer from list of available computers in Space
				if(!computer.equals(null)) {
					registeredComputers.remove(computer);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * Takes a result and handels it. 
		 * If it's done it's returned to it parent closure, 
		 * and if it still needs more processing it is put in the task queue.
		 * @param result
		 * @throws InterruptedException
		 */
		private void processResult(Result<?> result) throws InterruptedException {
			
			if(result.getStatus().equals(Status.WAITING)) {
				List<Closure> closures = result.getChildClosures();
				// Add Closures from
				for (Closure closure : closures) {
					receivedClosures.add(closure);
					receivedTasks.put(closure.getTask());
				}
			}
			else if(result.getStatus().equals(Status.COMPLETED)) {
				// return to parent closure
				for(Closure c : receivedClosures){
					if(c.getTask().getId().equals(result.getId())){
						c.receiveResult(result);
					}
				}					
			}
			else {
				System.out.println("Result received did not have a valid Status");
			}


		}
	}
	private void handleResult(Result<?> result) {
		if(result.getStatus().equals(Status.WAITING)) {
			List<Closure> closures = result.getChildClosures();
			// Add Closures from result
			for (Closure closure : closures) {
				receivedClosures.add(closure);
				try {
					receivedTasks.put(closure.getTask());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		else if(result.getStatus().equals(Status.COMPLETED)) {
			// return to parent closure
			for(Closure c : receivedClosures){
				if(c.getTask().getId().equals(result.getId())){
					c.receiveResult(result);
				}
			}					
		}
		else {
			System.out.println("Result received did not have a valid Status");
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

		boolean hasSpaceRunnableTasks = false;
		if(args.length > 0 && args[0].equals("true")) {
			hasSpaceRunnableTasks = true;
		}

		// Print acknowledgement
		System.out.println("Computer Space: Ready. on port " + Space.PORT);

		space.runComputerProxy(hasSpaceRunnableTasks);
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
