package system;

import java.io.Serializable;

public class ComputerStatus implements Serializable{
	
	double taskTime;
	double taskTimeCount;
	
	double bottomcaseTime;
	double bottomcaseCount;
	
	double sumLatency;
	double countLatency;
	
	public ComputerStatus(){
		this.bottomcaseTime =0.0;
		this.sumLatency = 0.0;
		this.taskTime =0.0;
		this.countLatency =0;
		
	}

	
	public void addLatency(double latency){
		// add it to average
		if(latency<10000){
		
		this.sumLatency += latency;
		this.countLatency+=1;}
	}
	
	public void addBottomcaseTime(long time ){
		bottomcaseTime+=time;
		bottomcaseCount++;
		
	}
	public void addTaskTime(long time ){
		taskTime+=time;
		taskTimeCount++;
	}

	public double getAverageLatency() {
		if (this.countLatency==0){
			return 0;
		}
		return this.sumLatency/this.countLatency;
	}
	
	
	public double getAverageTaskTime() {
		return this.taskTime/this.taskTimeCount;
	}

	
	public double getAverageBottomcaseTime() {
		return this.bottomcaseTime/this.bottomcaseCount;
	}


//done
	public void setAverageLatency(double averageLatency) {
		this.sumLatency += averageLatency;
		this.countLatency+=1;
	}

}
