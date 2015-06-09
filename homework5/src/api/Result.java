package api;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import system.Closure;
import system.MetaData;

/**
 * @param <T> type of return value of corresponding Task.
 */
public class Result<T> implements Serializable
{
    /** Generated serial ID	 */
	private static final long serialVersionUID = 1L;
	/** Value to return from execution of task */
	private final T taskReturnValue;
	/** Value to return from execution of task with distance */
	private final T taskReturnDistance;
	/** Time to execute task */
    private final long taskRunTime;
    /** Identifier of Result */
    private final String id;
    /** Status of this result */
    private Status status;
    /** List of children Closures in task decomposition tree */
    private List<Closure> childClosures = new ArrayList<>();
    /** True if result is a result of a tasks that pruned all subtasks */
    private boolean pruned = false;
    /** Number of child tasks that has been pruned from the tasks this result is of */
    private Integer nrOfPrunedTasks = 0;
    
    private long latency = 0;
    private long workTime = 0; //in ms
    
    private static MetaData md = new MetaData(-1, -1, -1);
    
    /** Result is completed if it the bottom case of the recursion tree, or belonging task has been pruned*/ 
    public enum Status{
    	WAITING, COMPLETED;
    }
    /**
     * Constructor used for result with a task value.Can be used to return result of base cases of fibonacci(n=1 and n=0)
     * @param taskReturnValue 	Return value of Result
     * @param taskRunTime		Time to execute task
     * @param id 				Identifier of Result
     */
    public Result( T taskReturnValue, long taskRunTime, String id)
    {
    	this.status = Status.COMPLETED;
        assert taskReturnValue != null;
        assert taskRunTime >= 0;
        this.taskReturnValue = taskReturnValue;
        this.taskRunTime = taskRunTime;
        this.id = id;
        this.childClosures = null;
        this.taskReturnDistance = null;
    }
    /**
     * Constructor used for result with a Tsp task default value. Can be used to return result of base cases of fibonacci(n=1 and n=0)
     * @param taskReturnValue 	Return value of Result
     * @param taskReturnDistance Return value of distance of TSP tour
     * @param taskRunTime		Time to execute task
     * @param id 				Identifier of Result
     */
    public Result( T taskReturnValue, T taskReturnDistance, long taskRunTime, String id)
    {
    	this.status = Status.COMPLETED;
        assert taskReturnValue != null;
        assert taskRunTime >= 0;
        this.taskReturnValue = taskReturnValue;
        this.taskRunTime = taskRunTime;
        this.id = id;
        this.childClosures = null;
        this.taskReturnDistance = taskReturnDistance;
    }
    public Result( T taskReturnValue, T taskReturnDistance, long taskRunTime, String id, boolean pruned, Integer nrOfPrunedTasks)
    {
    	this.status = Status.COMPLETED;
        assert taskReturnValue != null;
        assert taskRunTime >= 0;
        this.taskReturnValue = taskReturnValue;
        this.taskRunTime = taskRunTime;
        this.id = id;
        this.childClosures = null;
        this.taskReturnDistance = taskReturnDistance;
        this.pruned = true;
        this.nrOfPrunedTasks = nrOfPrunedTasks;
    }
    /**
     * Constructor used for result with a task value
     * Can be used to return result of partial node cases of fibonacci(n greater than 1)
     * @param childClosures	List of Closure objects that are child object of parent Closure
     * @param taskRunTime	Time to execute task
     * @param id			Identifier of Result
     */
    public Result(List<Closure> childClosures,long taskRunTime, String id)
    {
    	this.status = Status.WAITING;
        assert taskRunTime >= 0;
        this.taskRunTime = taskRunTime;
        this.id = id;
        this.childClosures = childClosures;
        this.taskReturnValue = null;
        this.taskReturnDistance = null;
    }
    
    public T getTaskReturnDistance() { return taskReturnDistance; }
    
    public T getTaskReturnValue() { return taskReturnValue; }

    public long getTaskRunTime() { return taskRunTime; }
    
    public String getId() { return this.id; }
    
    public Status getStatus() { return this.status; }
    
    public List<Closure> getChildClosures() { return this.childClosures; }
    
    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append( getClass() );
        stringBuilder.append( "\n\tExecution time:\n\t" ).append( taskRunTime );
        //stringBuilder.append( "\n\tReturn value:\n\t" ).append( taskReturnValue.toString() );
        return stringBuilder.toString();
    }

	public Integer getNrOfPrunedTasks() {
		return nrOfPrunedTasks;
	}
	public boolean isPruned() {
		return this.pruned;
	}
    public void setLatency(long latency){
    	
    	this.latency = latency;
    	
    }
    public long getLatency(){
    	return this.latency;
    }
    
    public long getWorkTime() {
		return workTime;
	}
	public void setWorkTime(long workTime) {
		this.workTime = workTime;
	}
	public void setMetaData(MetaData m){
    	this.md = m;
    }

}