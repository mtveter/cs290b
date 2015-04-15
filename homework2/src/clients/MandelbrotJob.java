package clients;

import java.awt.geom.Point2D;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tasks.TaskMandelbrotSet;
import api.Result;
import api.Space;
import api.Task;

/**
 * 
 */
public class MandelbrotJob implements Job {

	private static final int TASK_SIZE = 32;
	private double lowerX;
	private double lowerY;
	private double edgeLength;
	private int n;
	private int iterLimit;

	private Integer[][] allValues;

	// Maps each Task object to its input
	private Map<String, Point2D> taskIdentifierMap;
	// Used to time the execution of methods for profiling
	private Map<String, Long> timeMap;

	private int numOfTasks;

	/**
	 * 
	 * @param lowerX  	X-coordinate of lower left corner of a square in the complex plane 
	 * @param lowerY	Y-coordinate of lower left corner of a square in the complex plane
	 * @param edgeLength	The edge length of a square in the complex plane, whose sides are parallel to the axes
	 * @param n			An integer such that the square region of the complex plane is subdivided into n X n squares,
	 *  each of which is visualized by 1 pixel
	 * @param iterLimit	The representative point of a region that is considered to be in the Mandelbrot set 
	 */
	public MandelbrotJob(double lowerX, double lowerY, double edgeLength,
			int n, int iterLimit) {
		super();
		this.lowerX = lowerX;
		this.lowerY = lowerY;
		this.edgeLength = edgeLength;
		this.n = n;
		this.iterLimit = iterLimit;
		this.allValues = new Integer[n][n];
		this.taskIdentifierMap = new HashMap<String, Point2D>();
		this.timeMap = new HashMap<String, Long>();
		this.numOfTasks = 0;
	}

	/**
	 * Decomposes the Mandelbrot Set task into subtasks
	 * 
	 * @param space Compute space server that tasks are sent to 
	 * @throws RemoteException If there is a connection error
	 * 
	 */
	public void generateTasks(Space space) throws RemoteException {
		System.out.println("MANDELBROTJOB: Job received");
		int i = 0, j = 0;
		double jump = edgeLength / n;
		List<Task> taskList = new ArrayList<Task>();
		for (double xIndex = this.lowerX; i < n; xIndex += jump
				* MandelbrotJob.TASK_SIZE, i += MandelbrotJob.TASK_SIZE) {
			j = 0;
			for (double yIndex = this.lowerY; j < n; yIndex += jump
					* MandelbrotJob.TASK_SIZE, j += MandelbrotJob.TASK_SIZE) {
				String taskIdentifier = i + "," + j;
				Task<int[][]> aMandelbrotSetTask = new TaskMandelbrotSet(
						xIndex, yIndex, jump, MandelbrotJob.TASK_SIZE,
						iterLimit, taskIdentifier);
				timeMap.put(taskIdentifier, System.currentTimeMillis());
				taskList.add(aMandelbrotSetTask);
				this.taskIdentifierMap.put(taskIdentifier, new Point2D.Double(
						i, j));
				numOfTasks++;
			}
		}
		System.out.println("MANDELBROT: List of tasks is being sent to Space: " + taskList);
		space.putAll(taskList);
	}

	/**
	 * Collects results from all subtask execution from the compute space,
	 * gathers them in a data structure as a 2D array of Integers 
	 * and returns the resulting array
	 * 
	 * @param space Compute space storing the results of executions of subtasks
	 * @throws RemoteException If there is a connection error
	 * @return Integer[][] 2D array storing the composed solution to the task
	 */
	public Integer[][] collectResults(Space space) throws RemoteException {
		long computerTotalTime = 0L;
		long clientTotalTime = 0L;

		for (int i = 0; i < taskIdentifierMap.size(); i++) {
			Result<int[][]> r = (Result<int[][]>) space.take();
			long currentTime = System.currentTimeMillis();
			System.out.println("Client Task : " + (i + 1)
					+ " Elapsed   time : "
					+ (currentTime - timeMap.get(r.getId()))
					+ " ms");
			clientTotalTime += (currentTime - timeMap
					.get(r.getId()));
			computerTotalTime += (r.getTaskRunTime());
			int[][] values = (int[][]) r.getTaskReturnValue();
			String identifier = r.getId();
			Point2D point = this.taskIdentifierMap.get(identifier);
			double startX = new Double(point.getX());
			double startY = new Double(point.getY());

			for (int valuesRow = 0; valuesRow < values.length; valuesRow++) {
				for (int valuesCol = 0; valuesCol < values[0].length; valuesCol++) {
					int actualX = new Double(valuesRow + startX).intValue();
					int actualY = new Double(n - 1 - (valuesCol + startY))
							.intValue();
					allValues[actualX][actualY] = values[valuesRow][valuesCol];
				}
			}
			System.out.println("Computer Task : " + (i + 1)
					+ " Elapsed   time : "
					+ (r.getTaskRunTime()) + " ms");
			computerTotalTime += (r.getTaskRunTime());
		}
		// Print out Total Client time elapsed
		System.out.println("\n\nClient Total time : " + clientTotalTime + " ms");
		// Print out average Client time elapsed
		System.out.println("Client Average time : " + ((0.0f + clientTotalTime) / (numOfTasks + 0.0f)) + " ms");
		// Print out Total Computer time elapsed
		System.out.println("\n\nComputer Total time : " + computerTotalTime + " ms");
		// Print out average Computer time elapsed
		System.out.println("Computer Average time : " + ((0.0f + computerTotalTime) / (numOfTasks + 0.0f)) + " ms");
		
		return allValues;
	}
}