package tasks;

import java.io.Serializable;

import api.Task;

/**
 * Computes the Mandelbrot Set
 */
public final class TaskMandelbrotSet implements Task<int[][]>, Serializable {

	private static final long serialVersionUID = -7445556151823532932L;
	/** X-coordinate of lower left corner of a square in the complex plane */
	private double lowerX;
	/** Y-coordinate of lower left corner of a square in the complex plane */
	private double lowerY;
	/** The representative point of a region that is considered to be in the Mandelbrot set */
	private int iterLimit;
	private static final double MANDELBROT_LIMIT = 4.0;
	/** Identifier of MandelbrotSet task */
	private String id;
	/** An integer such that the square region of the complex plane is subdivided into n X n squares,
	 *  each of which is visualized by 1 pixel*/
	private int n;
	/** The edge length of a square in the complex plane, whose sides are parallel to the axes */
	private double edgeLength;
	
	/**
	 * @param lowerX  	X-coordinate of lower left corner of a square in the complex plane 
	 * @param lowerY	Y-coordinate of lower left corner of a square in the complex plane
	 * @param edgeLength	The edge length of a square in the complex plane, whose sides are parallel to the axes
	 * @param n			An integer such that the square region of the complex plane is subdivided into n X n squares,
	 *  each of which is visualized by 1 pixel
	 * @param iterLimit	The representative point of a region that is considered to be in the Mandelbrot set
	 * @param id ID of Task 
	 */
	public TaskMandelbrotSet(double lowerX, double lowerY, double edgeLength, int n, int iterLimit, String id) {
		this.lowerX = lowerX;
		this.lowerY = lowerY;
		this.edgeLength = edgeLength;
		this.n = n;
		this.iterLimit = iterLimit;
		this.id = id;
	}

	/**
	 * Generates the solution to the Mandelbrot Set task
	 */
	public int[][] call() {
		
		int[][] values = new int[n][n];
		int i = 0, j = 0;
		for (double xIndex = this.lowerX; i<n; xIndex += edgeLength, i++) {
			j = 0;
			
			for (double yIndex = this.lowerY; j<n; yIndex += edgeLength, j++) {
				double zLowerReal = xIndex;
				double zLowerComplex = yIndex;
				double zReal = zLowerReal;
				double zComplex = zLowerComplex;

				int k;
				for (k = 0; k < this.iterLimit && ((zReal*zReal - zComplex*zComplex) < MANDELBROT_LIMIT); k++) {
					double zPrevReal = zReal;
					zReal = zReal * zReal - zComplex * zComplex + zLowerReal;
					zComplex = 2 * zPrevReal * zComplex + zLowerComplex;
				}

				if ((zReal*zReal - zComplex*zComplex) < 4) {

					values[i][j] = this.iterLimit;
				} else {

					values[i][j] = k;
				}
			}
		}
		return values;
	}
	
	/**
	 * Returns the unique ID of task
	 */
	public String getId(){
		return id;
	}
}