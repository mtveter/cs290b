package api;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import system.Closure;

/**
 * @param <T> type of return value of corresponding Task.
 */
public class Result<T> implements Serializable
{
    /** Generated serial ID	 */
	private static final long serialVersionUID = 1L;
	/** Value to return from execution of task */
	private final T taskReturnValue;
	/** Time to execute task */
    private final long taskRunTime;
    /** Identifier of Result */
    private final String id;
    /**  */
    private Status status;
    /** */
    private List<Closure> childClosures = new ArrayList<>();
    /**  */
    public enum Status{
    	WAITING, COMPLETED;
    }
    /**
     * Constructor used for result with a task value
     * Can be used to return result of base cases of fibonacci(n=1 && n=0)
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
    }
    /**
     * Constructor used for result with a task value
     * Can be used to return result of partial node cases of fibonacci(n>=2)
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
    }
    
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
}