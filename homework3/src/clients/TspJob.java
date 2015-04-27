package clients;

import java.rmi.RemoteException;

import api.Space;


/**
 * Implementation of {@link Job} interface for the Euclidean TSP problem
 * @param <T> Generic type of Job
 */
public class TspJob<T> implements Job<T> {

	public TspJob(double[][] cities) {
		// TODO Auto-generated constructor stub
	}
	/**
	 * @see clients.Job Job
	 */
	@Override
	public void generateTasks(Space space) throws RemoteException {
		// TODO Auto-generated method stub
		
	}
	/**
	 * @see clients.Job Job
	 */
	@Override
	public Object collectResults(Space space) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

}
