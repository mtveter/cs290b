package system;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
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
	private int id;
	private transient List<ComputeThread> threads= new ArrayList<ComputerImpl.ComputeThread>();
	private transient BlockingQueue<Result<?>> results= new LinkedBlockingQueue<Result<?>>();
	private transient BlockingQueue<Task<?>> tasks= new LinkedBlockingQueue<Task<?>>();
	int cores;
	private boolean multicore;
	private boolean amerlioration;
	static Space space;
	private static int buffer=10;
	private Shared sharedObject;
	private long latency =90;
	//private ComputerStatus computerstatus;
	private ComputerPreferences compPref;
	private int recursionLimit = 9;
 
	/**
	 * @throws RemoteException If there is a connection error
	 */
	protected ComputerImpl(int id, boolean mulitcore,boolean amerlioration) throws RemoteException {
		super();
		this.id = id;
		this.amerlioration = amerlioration;
		this.multicore = mulitcore;
		this.sharedObject= new TspShared(Double.MAX_VALUE); 
		this.compPref = new ComputerPreferences();
		
	}
	/**
	 * @see system.Computer Computer
	 */
	@Override
	public Result<?> execute(Task<?> task) throws RemoteException {
		Result<?> result = task.call();

//		System.out.println("in execute");

		System.out.println("in execute");
		MetaData md = new MetaData((int) getLatency(), -1, -1);
		
		result.setMetaData(md);

		return result;
	}
	
	private long getLatency(){
		//long time =170;
		//long time =200;
		//time = randomGenerator.nextInt(600);
		
		return this.latency;
		
		
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
		String domainName;
		boolean multicore;
		boolean prefetch;
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
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getId() {
		return this.id;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void getTask(Task<?> task) throws RemoteException {
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
		Result<?> r = results.take();
		return r;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean runsCores() throws RemoteException {
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
		
		public ComputeThread(int id){
			this.id = id;
		}
		
		@Override
		public void run() {
			
			while(true) try {
				
				Task<?> task = tasks.take();
				
				//Latency
				Thread.sleep(getLatency());
				
				
				
				Result<?> result;
				
				try {
					task.setRecLimit(recLimit);
					start = System.nanoTime();
					result = task.call();
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
	
	
	@Override
	public synchronized void setShared(Shared sharedObject) throws RemoteException {
		//System.out.println("COMPUTER changed shared object");
		//System.out.println("to "+sharedObject.get());
		if (this.sharedObject.isOlderThan(sharedObject)){
			this.sharedObject = sharedObject;
			//space.setShared(sharedObject);
		}
	}
	@Override
	public Shared getShared() {
		return sharedObject;
	}
	@Override

	public void setId(int id) throws RemoteException {
		this.id = id;
	}
	@Override
	public void setSharedForced(Shared sharedObject) throws RemoteException {
		// TODO Auto-generated method stub
		this.sharedObject = sharedObject;
	}

	public ComputerStatus getComputerStatus() throws RemoteException {
		// TODO Auto-generated method stub
		return new ComputerStatus();
	}
	@Override
	public void setComputerPreferences(ComputerStatus cs ) throws RemoteException {
		// TODO Auto-generated method stub
		
		//updates latest results
		this.compPref.takeStatus(cs);
		
		if( this.compPref.getSpeed() == Speed.SLOW){
			//set slow preferences
			this.compPref.buffer = compPref.slowBuffer;
			this.compPref.recLimit = compPref.slowRecLimit;
			
		}else if(this.compPref.getSpeed()== Speed.FAST){
			// set fastSettings
			this.compPref.buffer = compPref.fastBuffer;
			this.recursionLimit = compPref.fastRecLimit;
		}else{
			//set default settings
			this.compPref.buffer = compPref.initBuffer;
			this.recursionLimit = compPref.initRecLimit;
			
			
		}
		for(ComputeThread t: threads){
			t.recLimit=this.recursionLimit;
			System.out.println("Changed reclimit in thread to "+this.recursionLimit);
		}
		
	}

}
