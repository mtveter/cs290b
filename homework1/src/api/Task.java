package api;

import java.io.Serializable;

/**
 * Interface of a Task
 * 
 * @param <T> Type to return
 */
public interface Task<T> extends Serializable{
	/**
	 * Execute task
	 * @return Result as type T
	 */
	public T execute();
}
