package tasks;

import system.Computer;
import api.Task;

public abstract class BaseTask<T> implements Task<T> {
	
	private Computer computer;

	public Computer getComputer() {
		return computer;
	}

	public void setComputer(Computer computer) {
		this.computer = computer;
	}

}
