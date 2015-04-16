package clients;

import java.rmi.RemoteException;
import java.util.List;

import system.Computer;
import api.Space;

/**
 * Implementation of {@link Job} interface for the Euclidean TSP problem
 */

public class TspJob implements Job{
	// TODO: Finish implementation of this class

	public TspJob(double[][] cities) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void generateTasks(Space space) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Integer> collectResults(Space space) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
}
