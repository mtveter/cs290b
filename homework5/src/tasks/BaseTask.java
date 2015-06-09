package tasks;

import system.Computer;
import api.Task;

public abstract class BaseTask<T> implements Task<T> {
	
	/** Generated serial identifier */
	private static final long serialVersionUID = -935021180915365225L;
	private Computer computer;

	public Computer getComputer() {
		return computer;
	}

	public void setComputer(Computer computer) {
		this.computer = computer;
	}

}
