package system;

public class MetaData {
	
	public int latency =-1;
	public long splitTime=-1;
	public long workTime=-1;
	
	public MetaData(int latency,long splitTime,long workTime) {
		this.latency = latency;
		this.splitTime =splitTime;
		this.workTime = workTime;
	}	
}
