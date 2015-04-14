package system;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import api.Result;
import api.Space;
import api.Task;

public class SpaceImpl extends UnicastRemoteObject implements Space{
	/**
	 * Generated Serial ID
	 */
	private static final long serialVersionUID = 4974914619819929236L;
	
	private boolean isActive;
	
	private BlockingQueue<Computer>  registeredComputers = new LinkedBlockingQueue<Computer>();
	private BlockingQueue<Task> receivedTasks = new LinkedBlockingQueue<Task>();
	private BlockingQueue<Result> receivedResults = new LinkedBlockingQueue<Result>();

	protected SpaceImpl() throws RemoteException {
		super();
		this.isActive = false;
	}

	@Override
	public void putAll(List<Task> taskList) throws RemoteException {
		for(Task task :  taskList) {
			try {
				receivedTasks.put(task);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Result take() throws RemoteException {
		try {
			return receivedResults.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

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
	
	@Override
	public void register(Computer computer) throws RemoteException {
		// TODO: Unsure if this will suffice. Might need improvement
		registeredComputers.add(computer);
	}
	
	public boolean isActive() {
		return this.isActive;
	}
	
	private void runComputerProxy() {
		this.isActive = true; 
		ComputerProxy proxy = new ComputerProxy();
		proxy.start();
	}


	public class ComputerProxy extends Thread{
		
		@Override
		public void run() {
			while(isActive) {
				Task task = null;
				try {
					task = receivedTasks.take();
					Computer computer = registeredComputers.take();
					Result result = (Result) computer.execute(task);
					receivedResults.put(result);
					
				} catch (RemoteException | InterruptedException e) {
					try {
						receivedTasks.put(task);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					e.printStackTrace();
				}
				try {
					this.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}



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
	
}
