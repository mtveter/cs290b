package tasks;

import api.Task;

public class TaskMandelbrotSet implements Task<Integer[]> {
	// TODO: Finish implementation of this class
	
	/** Generated serial ID */
	private static final long serialVersionUID = -7723233187547469315L;
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
	
	private final Integer[] countRow;
	private final int rowIndex;
	
	/**
	 * 
	 * @param lowerLeftX		X-coordinate of lower left corner of a square in the complex plane	
	 * @param lowerLeftY		Y-coordinate of lower left corner of a square in the complex plane
	 * @param edgeLength		The edge length of a square in the complex plane
	 * @param nPixels			Number of subdivisions of the square in the complex plane
	 * @param iterationLimit	Representative point of a region that is considered to be in set
	 */
	public TaskMandelbrotSet(Integer[] countRow, int rowIndex, double lowerLeftX, double lowerLeftY,
			double edgeLength, int nPixels, int iterationLimit) {
		this.lowerLeftX = lowerLeftX;
		this.lowerLeftY = lowerLeftY;
		this.edgeLength = edgeLength;
		this.n = nPixels;
		this.iterationLimit = iterationLimit;
		this.countRow = countRow;
		this.rowIndex = rowIndex;
	}

	@Override
	public Integer[] call() {
		// TODO Auto-generated method stub
		for (int j = 0; j < n; j++) {
			double x0 = lowerLeftX + (edgeLength/n)*rowIndex; 
			double y0 = lowerLeftY + (edgeLength/n)*j;
			double zReal = x0;
			double zImaginary = y0;
			int k=0; 
			while((zReal*zReal - zImaginary*zImaginary) < 4 && k < iterationLimit){
				double xtemp = zReal*zReal - zImaginary*zImaginary + x0;
				double ytemp = 2*zReal*zImaginary + y0;
				zReal = xtemp;
				zImaginary = ytemp;
				k++;
			}
			countRow[n-j-1] = k; 
		}
		return countRow;
	}
}
