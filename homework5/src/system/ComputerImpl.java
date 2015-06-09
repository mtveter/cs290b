package system;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import system.ComputerPreferences.Speed;
import api.Result;
import api.Space;
import api.Task;

public class ComputerImpl extends UnicastRemoteObject implements Computer,Runnable {

	/** Generated serial ID	 */
	private static final long serialVersionUID = -3092303569928556422L;
	private String id;
	private transient List<ComputeThread> threads= new ArrayList<ComputerImpl.ComputeThread>();
	private transient BlockingQueue<Result<?>> results= new LinkedBlockingQueue<Result<?>>();
	private transient BlockingQueue<Task<?>> tasks= new LinkedBlockingQueue<Task<?>>();
	int cores;
	/** True if multiple worker threads functionality enabled */
	private boolean multicore;
	/** True if amelioration functionality enabled */
	private boolean amerlioration;
	static Space space;
	private static int buffer = 5;
	/** Object shared with other Computer's and Space */
	private Shared sharedObject;

	private long latency = 50;
	private Random randomGenerator= new Random();
	private ComputerPreferences compPref;
	private int recursionLimit = 7;
	int variance = 10;
	private boolean simulateLatency = false;
	private boolean dynamic = true;

	/**
	 * 
	 * @param id				Identifier
	 * @param mulitcore			Multiple core workers variable
	 * @param amerlioration		Amelioration variable
	 * @throws RemoteException	If there is a communication error when remote is referenced
	 */
	protected ComputerImpl(String id, boolean mulitcore,boolean amerlioration, String domainName) throws RemoteException, MalformedURLException, NotBoundException {
		super();
		this.id = id;
		this.amerlioration = amerlioration;
		this.multicore = mulitcore;
		this.sharedObject= new TspShared(Double.MAX_VALUE);
		this.compPref = new ComputerPreferences();
		
		// Construct and set a security manager
		System.setSecurityManager( new SecurityManager() );

		// Get url of remote space
		String url = "rmi://" + domainName + ":" + Space.PORT + "/" + Space.SERVICE_NAME;
		// Try to get remote reference from rmiregistry and register new computer to space

		space = (Space) Naming.lookup( url );
		this.id = space.createId();
		if(runsCores()) {	
			createThreads();
			space.register(this);
		}
		else {
			space.register(this);
			run();
		}
		
		// Print acknowledgement
		System.out.println("Computer started and registered at space " + domainName);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Result<?> execute(Task<?> task) throws RemoteException {
		if(getSimlulateLatency()){
			try {
				Thread.sleep(getLatency());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Result<?> result = task.call();

		MetaData md = new MetaData((int) getLatency(), -1, -1);
		
		result.setMetaData(md);

		return result;
	}
	
	private long getLatency(){
		int currentVar = randomGenerator.nextInt(variance);
		
		if(randomGenerator.nextBoolean()){
			currentVar= currentVar *-1;
		}
		
		return this.latency+=currentVar;
		
	}
	
	/**
	 * checks if the computer uses buffer.
	 * @return True if amelioration is used
	 */
	public boolean useAmerlioration(){
		return this.amerlioration;
	}
	
	/**
	 * @see system.Computer Computer
	 */
	@Override
	public void exit() throws RemoteException {
		if(getSimlulateLatency()){
			try {
				Thread.sleep(getLatency());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.exit(0);
	}
	/**
	 * Main method for creating a Computer
	 * @param args IP-address of remote server, 'localhost' is default if no argument is passed
	
	 * @throws RemoteException 			If there is a communication error when remote is referenced
	 * @throws MalformedURLException 	URL specified is not valid
	 * @throws NotBoundException 		Attempt to lookup or unbind in the registry a name that has no associated binding.
	 */
	public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException{
		// If no argument is passed, then connect to local host, otherwise to IPv4 specified 
		boolean multicore;
		boolean prefetch;
		String domainName;
		if(args.length > 0) {
			domainName = args[0];
			multicore =(args.length > 1)? Boolean.parseBoolean(args[1]): true;
			prefetch = (args.length > 2)? Boolean.parseBoolean(args[2]): true;
			
		}
		else {
			domainName = "localhost";
			multicore=true;
			prefetch= true;
			
		}
		new ComputerImpl("N/A", multicore, prefetch, domainName);
/*
		// Constructs and set a security manager
		System.setSecurityManager( new SecurityManager() );

		// Get url of remote space
		String url = "rmi://" + domainName + ":" + Space.PORT + "/" + Space.SERVICE_NAME;
		// Try to get remote reference from rmiregistry and register new computer to space
		Space space;

		space = (Space) Naming.lookup( url );
		
		
		Computer computer = new ComputerImpl(1,multicore,prefetch);
		
		// create threads if the computer runs multiple cores
		if(computer.runsCores()){	
			((ComputerImpl) computer).createThreads();
			space.register(computer);
		}
		else{
			
			space.register(computer);
			((ComputerImpl) computer).run();
			
		}
		// Print acknowledgement
		System.out.println("Computer started and registered at space " + domainName);
		*/
	}

	public String getId() {
		if(getSimlulateLatency()){
			try {
				Thread.sleep(getLatency());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return this.id;
	}

	/*@Override
	public int getId() {
		return this.id;
	}*/
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void getTask(Task<?> task) throws RemoteException {
		if(getSimlulateLatency()){
			try {
				Thread.sleep(getLatency());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		task.setComputer(this);
		tasks.add(task);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean bufferAvailable(){
		
		if(tasks.size()<=compPref.buffer && useAmerlioration() ){
			return true;
		}else{
			return false;
		}
		
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int bufferSize(){
		return buffer-tasks.size();
	}
	
	public void setBuffer(int size){
		this.buffer = size;
	}
	/**
	 * Creates the threads according to how many processors the computer has available
	 */
	public void createThreads(){
		//create threads running f
				cores =Runtime.getRuntime().availableProcessors();
				for(int i=0; i<cores; i++){
					ComputeThread thread = new ComputeThread(i);
					threads.add(thread);
					thread.start();
				}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Result<?> sendResult() throws RemoteException, InterruptedException {
		if(getSimlulateLatency()){
			try {
				Thread.sleep(getLatency());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Result<?> r = results.take();
		return r;
	}
	/**
	 * {@inheritDoc}
	 */
	public boolean runsCores() throws RemoteException {
		if(getSimlulateLatency()){
			try {
				Thread.sleep(getLatency());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return this.multicore;
	}
	/**
	 * Thread that only does work on tasks
	 * @author steffenfb
	 *
	 */
	private class ComputeThread extends Thread  {
		long start;
		long stop;
		long workTime;
		private  int id;
		Random randomGenerator = new Random();
		private int recLimit = 6;
		private int latency;
		
		public ComputeThread(int id){
			this.id = id;
		}
		
		@Override
		public void run() {
			
			while(true) try {
				
				Task<?> task = tasks.take();
				
				//Latency
				if (getSimlulateLatency()){
				Thread.sleep(getLatency());
				}
				
				
				Result<?> result;
				
				try {
					task.setRecLimit(recLimit);
					start = System.nanoTime();
					result = task.call();
					
					result.setSimlateLatency(getSimlulateLatency());
					
					result.setStartTime(task.getTime());
					
					
					stop = System.nanoTime();
					workTime = (stop-start)/1000000;
					
					result.setWorkTime(workTime);
					
					result.setLatency(getLatency());
					
					results.put(result);
					
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}catch(InterruptedException e){	}
		}
		
		
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int coreCount() throws RemoteException {
		if(getSimlulateLatency()){
			try {
				Thread.sleep(getLatency());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return this.cores;
	}
	
	/**
	 * for when the computer runs on single core, but use a queue to pref-etch
	 */
	@Override
	public void run() {
		
		while(true) try {
			
			Task<?> task = tasks.take();
			
			Result<?> result;
			try {
				task.setRecLimit(recursionLimit);
				result = task.call();
				MetaData md = new MetaData((int) getLatency(), -1, -1);
				
				result.setMetaData(md);
				
				results.put(result);
				
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}catch(InterruptedException e){	}	
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void setShared(Shared sharedObject) throws RemoteException {
		if(getSimlulateLatency()){
			try {
				Thread.sleep(getLatency());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (this.sharedObject.isOlderThan(sharedObject)){
			this.sharedObject = sharedObject;
		}
	}
	/**
	 * {@inheritDoc}
	 */
	public Shared getShared() {
		return sharedObject;
	}
	public boolean getSimlulateLatency(){
		return this.simulateLatency;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getNameString() throws RemoteException {
		if(getSimlulateLatency()){
			try {
				Thread.sleep(getLatency());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			return "Computer "+id+" ["+getClientHost()+"]";
		} catch (ServerNotActiveException e) {
			e.printStackTrace();
		}
		return "Computer "+id+" [N/A]";
	}

	/*@Override
	public void setId(int id) throws RemoteException {
		this.id = id;
	}
	/**
	 * {@inheritDoc}
	 */
	public void setSharedForced(Shared sharedObject) throws RemoteException {
		if(getSimlulateLatency()){
			try {
				Thread.sleep(getLatency());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.sharedObject = sharedObject;
	}
	/**
	 * {@inheritDoc}
	 */
	
	public ComputerStatus getComputerStatus() throws RemoteException {
		if(getSimlulateLatency()){
			try {
				Thread.sleep(getLatency());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return new ComputerStatus();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setComputerPreferences(ComputerStatus cs ) throws RemoteException {
		//updates latest results
		
		if(dynamic){
		this.compPref.takeStatus(cs);
		
		if( this.compPref.getSpeed() == Speed.SLOW){
			//set slow preferences
			this.compPref.buffer = ComputerPreferences.slowBuffer;
			this.compPref.recLimit = ComputerPreferences.slowRecLimit;
			
		}else if(this.compPref.getSpeed()== Speed.FAST){
			// set fastSettings
			this.compPref.buffer = ComputerPreferences.fastBuffer;
			this.recursionLimit = ComputerPreferences.fastRecLimit;
		}
		for(ComputeThread t: threads){
			t.recLimit=this.compPref.recLimit;
		}}
	}
}
