package system;

public class ComputerPreferences {
	
	/** Initial buffer size of Computer*/
	static int initBuffer = 5;
	static int fastBuffer = 3;
	static int slowBuffer = 15;
	
	static int initPruning;
	static int slowPruning;
	static int fastPruning;
	
	static int initRecLimit = 8;
	static int slowRecLimit = 7;
	static int fastRecLimit = 6;
	
	static int treshold = 38; //less than this is considered fast
	
	
	
	int recLimit;
	int prining;
	int buffer;
	
	private double averageLatency = 0;
	private long averageLatencycount = 0;
	
	public enum Speed{
		FAST, SLOW, DEFAULT;
	}
	
	public ComputerPreferences() {
		// TODO Auto-generated constructor stub
		this.averageLatency=0;
		this.recLimit = slowRecLimit;
		this.buffer = initBuffer;
	}
	
	public void takeStatus(ComputerStatus cs){
		averageLatencycount++;
		averageLatency += cs.getAverageLatency();
	}
	
	public Speed getSpeed(){
		if(getAvgLatency()>treshold){
			return Speed.SLOW;
		}else {
			return Speed.FAST;
		}
	}
	
	public double getAvgLatency(){
		return this.averageLatency/this.averageLatencycount;
	}
}
