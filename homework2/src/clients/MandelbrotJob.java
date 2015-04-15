package clients;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import tasks.TaskMandelbrotSet;
import api.Space;
import api.Task;

public class MandelbrotJob implements Job{
	// TODO: Finish implementation of this class
	
	/** X-coordinate of lower left corner of a square in the complex plane */
	private final double lowerLeftX;
	/** Y-coordinate of lower left corner of a square in the complex plane */
	private final double lowerLeftY;
	/** The edge length of a square in the complex plane, whose sides are parallel to the axes */
	private final double edgeLength;
	/** An integer such that the square region of the complex plane is subdivided into n X n squares,
	 *  each of which is visualized by 1 pixel*/
	private final int n;
	/** The representative point of a region that is considered to be in the Mandelbrot set */
	private final int iterationLimit;
	
	private int taskPartsSent;
	private int taskPartsReceived;
	
	/**
	 * 
	 * @param lowerLeftX		X-coordinate of lower left corner of a square in the complex plane	
	 * @param lowerLeftY		Y-coordinate of lower left corner of a square in the complex plane
	 * @param edgeLength		The edge length of a square in the complex plane
	 * @param nPixels			Number of subdivisions of the square in the complex plane
	 * @param iterationLimit	Representative point of a region that is considered to be in set
	 */
	public MandelbrotJob(double lowerLeftX, double lowerLeftY,
			double edgeLength, int nPixels, int iterationLimit) {
		this.lowerLeftX = lowerLeftX;
		this.lowerLeftY = lowerLeftY;
		this.edgeLength = edgeLength;
		this.n = nPixels;
		this.iterationLimit = iterationLimit;
		
		this.taskPartsSent = 0;
		this.taskPartsReceived = 0;
	}
	
	@Override
	public void generateTasks(Space space) throws RemoteException {
		// TODO Auto-generated method stub
		Integer count[][] = new Integer[n][n];
		List<Task> taskList = new ArrayList<Task>(); 
		for (int i = 0; i < n; i++) {
			TaskMandelbrotSet task = new TaskMandelbrotSet(count[i], i, lowerLeftX, lowerLeftY, edgeLength, n, iterationLimit);
			taskList.add(task);
		}
		space.putAll(taskList);
		this.taskPartsSent += taskList.size();
	}

	@Override
	public Object collectResults(Space space) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getAllResults() {
		// TODO Auto-generated method stub
		return null;
	}

}
