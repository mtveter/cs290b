package clients;

import java.rmi.RemoteException;

import api.Space;


/**
 * Implementation of {@link Job} interface for the Euclidean TSP problem
 */
public class TspJob implements Job {

	public TspJob(double[][] cities) {
		// TODO Auto-generated constructor stub
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void generateTasks(Space space) throws RemoteException {
		// TODO Auto-generated method stub
		
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object collectResults(Space space) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

}
