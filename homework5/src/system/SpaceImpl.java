package system;

import gui.LatencyData;
import gui.SpaceListener;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.util.Random;

import java.util.concurrent.BlockingDeque;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import models.TasksProgressModel;
import api.Result;
import api.Result.Status;
import api.Space;
import api.Task;
import api.Task.Type;
import system.Closure;
import tasks.TaskTsp;
import util.TspBounds;

public class SpaceImpl extends UnicastRemoteObject implements Space {

	/** Generated serial identifier	 */
	private static final long serialVersionUID = 1L;
	/** Variable describing state of Space */
	private boolean isActive;
	/** Describes if Space implementation has feature to run some specified Task objects in Space */
	private boolean hasSpaceRunnableTasks;
	
	/* Model for progress of branch and bound  */
	private TasksProgressModel progressModel;
	
	private LatencyData latencyData;

	/* Integer representing the integer value to assign to the next registered computer
	 * Counting mechanism ensure uniqueness of all identifier values */
	//private int computerUniqueIdGenerator;

	private BlockingQueue<Computer>  registeredComputers = new LinkedBlockingQueue<Computer>();
	/* Lifo queue for tasks */
	private BlockingDeque<Task<?>> receivedTasks = new LinkedBlockingDeque<Task<?>>();
	private List<Closure> receivedClosures = new ArrayList<Closure>();
	private BlockingQueue<Result<?>> completedResult = new LinkedBlockingQueue<Result<?>>();
	private Shared sharedObject;
	
	long decompose_T;
	long compose_T;
	long maxSubtask_T;
	long T_1;
	
	private ArrayList<SpaceListener> listeners;

	public SpaceImpl() throws RemoteException {
		super();
		this.isActive = false;
		this.sharedObject = new TspShared(Double.MAX_VALUE);
		this.progressModel = new TasksProgressModel();
		this.latencyData = new LatencyData();
		this.listeners = new ArrayList<SpaceListener>();
	}
	/**
	 * @see api.Space Space
	 */
	@Override
	public void putAll(List<Task<?>> taskList) throws RemoteException {
		System.out.println("SPACE: List of tasks received from Job");
		firePropertyChanged(SpaceListener.MASTER_TASK_STARTED, null);
		
		this.sharedObject=new TspShared(Double.MAX_VALUE);
		for(Computer c:registeredComputers){
			c.setSharedForced(sharedObject);
		}
		
		List<Integer> cities = new ArrayList<Integer>();
		double[][] distances = null;
	
		
		for(Task<?> task :  taskList) {
			try {
				Closure initialClosure;
				if(task.getType() == Type.TSP){
					// Joincounter
					
					TaskTsp taskTsp = (TaskTsp) task;
					distances =taskTsp.distances;
					initialClosure = new Closure(taskTsp.getPartialCityList().size(),"TOP",task);
					receivedClosures.add(initialClosure);
					receivedTasks.putLast(task);
					
				}	
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		/* Sets the pruning model */
		this.progressModel.setTotalCities(distances.length);
		
		for (int i = 0; i < distances.length; i++) {
			cities.add(i);
		}
		
		System.out.println("THIS IS UPPER BOUND "+TspBounds.computeUpperBound(cities, distances));
		this.sharedObject = new TspShared(TspBounds.computeUpperBound(cities, distances));
	}
	/**
	 * @see api.Space Space
	 */
	@Override
	public Result<?> take() throws RemoteException {
		try {
			Result <?> r= completedResult.take();
			firePropertyChanged(SpaceListener.MASTER_TASK_FINISHED, null);
			System.out.println("SPACE: length of best tour is "+r.getTaskReturnDistance());
			double milli = 1000000.0;
			System.out.println("T_Decompose: "+decompose_T/milli + " ms");
			System.out.println("T_Compose: "+compose_T/milli + " ms");
			System.out.println("T_MaxSubtask: "+maxSubtask_T/milli + " ms");
			System.out.println("T_Infinity: "+(decompose_T + compose_T + maxSubtask_T)/milli + " ms");
			System.out.println("T_1: "+T_1/milli + " ms");
			return r;
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
		
		/* Sets a unique integer identifier for the computer */
		/*int id = computerUniqueIdGenerator;
		computer.setId(id);
		computerUniqueIdGenerator += 1;*/
		
		registeredComputers.add(computer);
		latencyData.addComputer(computer.getNameString());
		firePropertyChanged(SpaceListener.COMPUTER_ADDED, latencyData);
	}
	
	public boolean hasActiveComputers(){
		return !registeredComputers.isEmpty();
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
	 * @throws RemoteException 
	 */
	private void runComputerProxy(boolean hasSpaceRunnableTasks) throws RemoteException {
		this.hasSpaceRunnableTasks = hasSpaceRunnableTasks;
		int runner = 0;
		this.isActive = true; 
		// Thread runs as long as Space is active
		while(isActive) {
			/*if (!registeredComputers.isEmpty()){
				double value = 30.0+new Random().nextDouble()*70;
//				System.out.println("Latency value added: "+value+" for computer: "+registeredComputers.peek().getNameString());
				latencyData.addLatencyValue(registeredComputers.peek().getNameString(), value);
			}*/
			// Check if there are any Closure objects to process
			runner++;
			if(runner%30 == 1){
//				System.out.println("running");
			}
			if(!receivedClosures.isEmpty()) {
				//long composeStart = System.nanoTime();
				// If there exits a Closure it tries to merge completed Closure with parent Closure
				mergeCompletedClosures();
				
				// If the Top Closure is completed the final result can be put in the blocking queue to be collected by Client
				if(isTopClosureCompleted()) {
					try {
						Result<?> r = receivedClosures.get(0).getAdder().getResult();
						decompose_T = r.getTaskRunTime();
						completedResult.put(r);
						receivedClosures.remove(0);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				//long composeEnd = System.nanoTime();
				//compose_T += composeEnd - composeStart;
			}
			long decomposeStart = System.nanoTime();
			Task<?> task = null;
			//printClosures();
			try {
				task = receivedTasks.takeLast();
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
			long decomposeEnd = System.nanoTime();
			//decompose_T += decomposeEnd - decomposeStart;
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
					
					long taskRunTime = c.getAdder().getResult().getTaskRunTime();
					if (taskRunTime > maxSubtask_T) maxSubtask_T = taskRunTime;
					T_1 += taskRunTime;
					long composeStart = System.nanoTime(); 
					String parent = c.getParentId();
					// Compares the current Closure with other Closure to find parent
					for (Closure c2 : receivedClosures){
						// Closure cannot pass its result to itself
						if(!c.getTask().equals(c2.getTask().getId())){
							// Check if current Closure's parent is the Closure in current comparison iteration
							if(parent.equals(c2.getTask().getId())){
								// Passes result of completed Closure to parent Closure
								c2.receiveResult(c.getAdder().getResult());
								int level = c.getTask().getLevel();
								if(level == 3){
									progressModel.addCompletedTaskWeight(level);
								}
								
								if (parent.equals("0")){
									compose_T += System.nanoTime() - composeStart;
								}
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

	private void handleResult(Result<?> result) throws RemoteException {
		
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
	/*public static void main(String[] args) throws RemoteException {
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
	}*/

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
			ComputerStatus cs = new ComputerStatus();
			
			try {
				computer = registeredComputers.take();
				
				//Used to set the right preferences for the computer
				ComputerStatus status  = computer.getComputerStatus();
				
				try {
					computer.setShared(sharedObject);
					
				} catch (RemoteException e) {
					System.out.println("Could not set shared object to computer");
				}

			} catch (InterruptedException | RemoteException e2) {
				e2.printStackTrace();
			}

			try {
				if(!computer.runsCores()){
					/*
					 * Checks if the computer runs buffer, and also if there are enough waiting tasks, so the system 
					 * doesn't wait if there's not enough tasks.
					 */
					if(computer.bufferAvailable() && receivedTasks.size()>(computer.bufferSize())){
						computer.getTask(task);
						
						int available =computer.bufferSize()+computer.coreCount();

						// Sends tasks to computer
						for (int i = 0; i < available; i++) {
							computer.getTask(receivedTasks.takeLast());
						}

						/* Receives all the tasks from space, but they are processed as fast as they come in 
						 * to prevent unnecessary delay */
						for (int i = 0; i < available+1; i++) {
							Result<?> r = computer.sendResult();
							cs.addLatency(r.getLatency());
							processResult(r);

						}
						computer.setComputerPreferences(cs);
						registeredComputers.put(computer);
					}
					else {
						// if theres no multicore and no prefetching we just use the old execute method
						Result<?> result = (Result<?>) computer.execute(task);
						cs.addLatency(result.getLatency());
						processResult(result);
						computer.setComputerPreferences(cs);
						registeredComputers.put(computer);
					}

				}
				/* this is if the computer runs multiple cores */
				/* RUNS HERE */
				else {
					computer.getTask(task);
					/*
					 * Checks if the computer runs buffer, and also if there are enough waiting tasks, so the system 
					 * doesn't wait if there's not enough tasks.
					 */

					if(computer.bufferAvailable() /*&& receivedTasks.size()>(computer.bufferSize()+computer.coreCount())*/){
						int available;
						if(receivedTasks.size()>(computer.bufferSize()+computer.coreCount())){
							available = computer.bufferSize()+computer.coreCount();
						}
						else{
							available = receivedTasks.size();
						}
						
						for (int i = 0; i < available; i++) {
							Task task = receivedTasks.takeLast();
							task.setTime();
							computer.getTask(task);

						}
						for (int i = 0; i < available+1; i++) {
							Result<?> r = computer.sendResult();
							processResult(r);
							cs.addLatency(r.getLatency());
							
							if(r.getStatus().equals(Status.COMPLETED)){
								cs.addBottomcaseTime(r.getWorkTime());
							}
							else if (r.getStatus().equals(Status.WAITING)){
								cs.addTaskTime(r.getWorkTime());
							}
							}	
						computer.setComputerPreferences(cs);
						registeredComputers.put(computer);
						
						
						
					}else if (receivedTasks.size()>(computer.coreCount())){

						int available =computer.coreCount();
						
						for (int i = 0; i < available; i++) {
							computer.getTask(receivedTasks.takeLast());

						}
						for (int i = 0; i < available+1; i++) {
							Result<?> r = computer.sendResult();
							cs.addLatency(r.getLatency());
							processResult(r);

						}	
						computer.setComputerPreferences(cs);
						registeredComputers.put(computer);
						//printClosures();

					}
					else{
						/* if the computer can take more tasks, but there are not any more waiting */
						Result<?> r = computer.sendResult();
						cs.addLatency(r.getLatency());
						processResult(r);
						computer.setComputerPreferences(cs);

						registeredComputers.put(computer);
					}
				}	

				System.out.println("Computer avg btmct: "+cs.getAverageBottomcaseTime());
				System.out.println("Computer avg tasktime "+cs.getAverageTaskTime());
				System.out.println("Computer avg latency "+cs.getAverageLatency());
				latencyData.addLatencyValue(computer.getNameString(), cs.getAverageLatency());

			} catch (RemoteException e) {
				// If there's a RemoteException, task is put back in the queue
				try {
					receivedTasks.putLast(task);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				System.out.println("There was a remote exception: A computer might have been terminated");
				// Unregister the faulty computer from list of available computers in Space
				if(!computer.equals(null)) {
					registeredComputers.remove(computer);
					firePropertyChanged(SpaceListener.COMPUTER_REMOVED, registeredComputers.size());
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
		 * @throws RemoteException 
		 */
		private void processResult(Result<?> result) throws InterruptedException, RemoteException {

			if(result.getStatus().equals(Status.WAITING)) {
				List<Closure> closures = result.getChildClosures();
				// Add Closures from
				for (Closure closure : closures) {
					receivedClosures.add(closure);
					receivedTasks.putLast(closure.getTask());
				}
				/* Add generated tasks count */
				progressModel.increaseTotalGeneratedTasks(closures.size());
			}
			else if(result.getStatus().equals(Status.COMPLETED)) {
				double oldShared = (Double) getShared().get();
				double newShared = (Double) result.getTaskReturnDistance();
				
				if (newShared<oldShared){
					setShared(new TspShared(newShared));
				}
				// return to parent closure
				for(Closure c : receivedClosures){
					if(c.getTask().getId().equals(result.getId())){
						
						c.receiveResult(result);
						
						if(result.isPruned()){
							progressModel.increaseTotalPrunedTasks(result.getNrOfPrunedTasks());
							progressModel.increaseTotalPrunedLevels(result.getPruningDepth());
							c.setJoinCounter(0);
							c.getAdder().setResult(result);
						}
					}
				}
				/* Register that 1 more task has been completed */
				progressModel.increaseTotalCompletedTasks(1);
			}
			else {
				System.out.println("Result received did not have a valid Status");
			}
		}
	}

	/**
	 * Main method for creating Space
	 * @param args Not needed
	 * @throws RemoteException If there is a connection error
	 */
	public static void main(String[] args) throws RemoteException {
		boolean hasSpaceRunnableTasks = false;
		if(args.length > 0 && args[0].equals("true")) {
			hasSpaceRunnableTasks = true;
		}

		new SpaceImpl().startSpace(hasSpaceRunnableTasks);
	}
	
	public void startSpace(boolean hasSpaceRunnableTasks) throws RemoteException{
		// Construct and set a security manager
		System.setSecurityManager( new SecurityManager() );


		// Instantiate a computer server object
		//SpaceImpl space = new SpaceImpl();

		// Construct an RMI-registry within this JVM using the default port
		Registry registry = LocateRegistry.createRegistry( Space.PORT  );

		// Bind Compute Space server in RMI-registry
		registry.rebind( Space.SERVICE_NAME, this);

		// Print acknowledgement
		System.out.println("Computer Space: Ready. on port " + Space.PORT);

		runComputerProxy(hasSpaceRunnableTasks);
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
	
	@Override
	public synchronized void setShared(Shared sharedObject) throws RemoteException {
//		System.out.println("SPACE changed shared object");
		if (this.sharedObject.isOlderThan(sharedObject)){
			this.sharedObject = sharedObject;
			//Propagate to all computers
			for (Computer computer : registeredComputers){
				computer.setShared(sharedObject);
			}
		}
	}
	@Override
	public Shared getShared() {
		return sharedObject;
	}
	
    public void addSpaceListener(SpaceListener listener){
        listeners.add(listener);
    }
    
    public void firePropertyChanged(String propertyName, Object value){
        for (SpaceListener listener : listeners){
            listener.update(propertyName, value);
        }
    }
    
    public TasksProgressModel getTasksProgressModel(){
    	return progressModel;
    }
    
    public LatencyData getLatencyData(){
    	return latencyData;
    }
    
    private AtomicLong idCounter = new AtomicLong();

    public String createId() throws RemoteException {
        return String.valueOf(idCounter.getAndIncrement());
    };
    
}
