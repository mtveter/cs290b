package system;

import java.rmi.RemoteException;
import java.util.List;

import api.Result;
import api.Task;
import api.Result.Status;

import system.SpaceImpl;

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
			try {
				computer.setShared(sharedObject);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				System.out.println("Could not set shared object to computer");
			}

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
	 * @throws RemoteException 
	 */
	private void processResult(Result<?> result) throws InterruptedException, RemoteException {
		List<Closure> cl = result.getChildClosures();
		if(result.getId().equals("062")){
			System.out.println("got the 062 result");
			
			for(Closure c: receivedClosures){
				if(c.getTask().getId().equals("06")){
					System.out.println("There is a 06 closure");
				}
				if(c.getTask().getId().equals("062")){
					System.out.println("There is a 062 closure");
				}
			}
			
			
		}
		
		if(cl!=null){
		for(Closure c: cl){
			if(c.getParentId().equals("062")){
				System.out.println("Size "+cl.size());
				System.out.println("the taks id in closure 062 is "+c.getTask().getId());
			}
		}}

		//System.out.println("in process result");
		if(result.getStatus().equals(Status.WAITING)) {
			List<Closure> closures = result.getChildClosures();
			// Add Closures from
			for (Closure closure : closures) {
				receivedClosures.add(closure);
				receivedTasks.put(closure.getTask());
			}
		}
		else if(result.getStatus().equals(Status.COMPLETED)) {
			double oldShared = (Double) getShared().get();
			double newShared = (Double) result.getTaskReturnDistance();
			if (newShared<oldShared){
				System.out.println("Space is setting new TSP shared");
				setShared(new TspShared(newShared));
			}
			// return to parent closure
			for(Closure c : receivedClosures){
				if(c.getTask().getId().equals(result.getId())){
					
					c.receiveResult(result);
					if(result.pruned){
						c.setJoinCounter(0);
						c.getAdder().setResult(result);
						
					}
					
				}
			}					
		}
		else {
			System.out.println("Result received did not have a valid Status");
		}


	}
}