package api;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import system.Closure;

/**
 *
 * @author Peter Cappello
 * @param <T> type of return value of corresponding Task.
 */
public class Result<T> implements Serializable
{
    /** Generated serial ID	 */
	private static final long serialVersionUID = 1L;
	private final T taskReturnValue;
    private final long taskRunTime;
    private final String id;
    private Status status;
    private List<Closure> childClosures = new ArrayList<>();
    
    public enum Status{
    	WAITING, COMPLETED;
    }

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
    
    public Result(long taskRunTime, String id, List<Closure> childClosures)
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
    
    public String getId() {
    	return this.id;
    }
    
    public Status getStatus() {
    	return this.status;
    }
    public List<Closure> getChildClosures() {
    	return this.childClosures;
    }
    
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