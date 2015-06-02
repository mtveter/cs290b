package system;

import java.io.Serializable;

public class ComputerStatus implements Serializable{
	
	double averageTaskTime;
	
	double averageBottomcaseTime;
	
	double sumLatency;
	double countLatency;
	
	public ComputerStatus(){
		this.averageBottomcaseTime =0.0;
		this.sumLatency = 0.0;
		this.averageTaskTime =0.0;
		this.countLatency =0;
		
	}

	
	public void addLatency(long latency){
		// add it to average
		this.sumLatency += latency;
		this.countLatency+=1;
	}

	public double getAverageLatency() {
		
		return this.sumLatency/this.countLatency;
	}
	
	
	public double getAverageTaskTime() {
		return averageTaskTime;
	}


	public void setAverageTaskTime(double averageTaskTime) {
		this.averageTaskTime = averageTaskTime;
	}


	public double getAverageBottomcaseTime() {
		return averageBottomcaseTime;
	}


	public void setAverageBottomcaseTime(double averageBottomcaseTime) {
		this.averageBottomcaseTime = averageBottomcaseTime;
	}



//done
	public void setAverageLatency(double averageLatency) {
		this.sumLatency += averageLatency;
		this.countLatency+=1;
	}

}
