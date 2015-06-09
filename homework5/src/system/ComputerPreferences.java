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
	static int slowRecLimit = 9;
	static int fastRecLimit = 7;
	
	static int fast = 100; //less than this is considered fast
	
	static int slow = 300; // slower than this is concidered slow
	
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
		this.recLimit = initRecLimit;
		this.buffer = initBuffer;
	}
	
	public void takeStatus(ComputerStatus cs){
		System.out.println("Average latency "+cs.getAverageLatency());
		averageLatencycount++;
		averageLatency+=cs.getAverageLatency();
		
		
	}
	
	public Speed getSpeed(){
		if(getAvgLatency()>slow){
			return Speed.SLOW;
		}else if(getAvgLatency()<fast){
			return Speed.FAST;
		}else{
			return Speed.DEFAULT;
		}
	}
	
	public double getAvgLatency(){
		return this.averageLatency/this.averageLatencycount;
	}
}
