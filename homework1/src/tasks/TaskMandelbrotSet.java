package tasks;

import api.Task;

public class TaskMandelbrotSet implements Task<Integer[][]>{
	
	private double lowerLeftX; 
	private double lowerLeftY; 
	private double edgeLength; 
	private int n;
	private int iterationLimit; 
	

	public TaskMandelbrotSet(double lowerLeftX, double lowerLeftY,
			double edgeLength, int nPixels, int iterationLimit) {
		this.lowerLeftX = lowerLeftX;
		this.lowerLeftY = lowerLeftY;
		this.edgeLength = edgeLength;
		this.n = nPixels;
		this.iterationLimit = iterationLimit;
	}


	@Override
	public Integer[][] execute() {
		Integer count[][] = new Integer[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				double x0 = lowerLeftX + (edgeLength/n)*i; 
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
				count[i][n-j-1] = k; 
			}
		}
		return count;
	}

}
